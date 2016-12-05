package com.musicocracy.fpgk.domain.net;

import com.musicocracy.fpgk.domain.util.Box;
import com.musicocracy.fpgk.domain.util.RxUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.client.RxClient;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A functional reactive TCP client. Allows users to respond to events asynchronously.
 * @see RxTcpServer
 */
public class RxTcpClient {
    private final SharedSubject<Boolean> isRunningStream;
    private final SharedSubject<String> logStream;
    private final SharedSubject<String> transmitStream;
    private final SharedSubject<String> receiveStream;
    private RxClient<String, String> client = null;
    private ObservableConnection<String, String> connection;
    private Subscription clientSubscription;

    /**
     * Basic constructor. Initializes {@link #isRunning()} to false
     */
    public RxTcpClient() {
        this(SharedSubject.<Boolean>create(),
                SharedSubject.<String>create(),
                SharedSubject.<String>create(),
                SharedSubject.<String>create());
    }

    /**
     * Dependency Injection friendly constructor. Recommended for testing only.
     * @param isRunningStream Subject used to notify user if running status has changed
     * @param logStream Subject used to notify user of newly logged events
     * @param transmitStream Subject used to allow the user to send messages
     * @param receiveStream Subject used to notify the user of received messages
     */
    public RxTcpClient(SharedSubject<Boolean> isRunningStream,
                       SharedSubject<String> logStream,
                       SharedSubject<String> transmitStream,
                       SharedSubject<String> receiveStream) {
        this.isRunningStream = isRunningStream;
        this.logStream = logStream;
        this.receiveStream = receiveStream;
        this.transmitStream = transmitStream;

        // The client is not running initially. Set here because we grab the last result
        //  of isRunningStream for isRunning().
        isRunningStream.onNext(false);
    }

    /**
     * Starts the client. The client will automatically attempt to connect to servers on the
     * specified host/port, and automatically attempt reconnection if the server goes down.
     * @param host IPv4 address
     * @param port Port number
     */
    public void start(String host, int port) {
        if (client == null) {
            logStream.onNext("Attempting to start...");
            client = RxNetty.createTcpClient(host, port, PipelineConfigurators.textOnlyConfigurator());
            clientSubscription = client.connect()
                    .flatMap(new Func1<ObservableConnection<String, String>, Observable<String>>() {
                        @Override
                        public Observable<String> call(final ObservableConnection<String, String> serverConnection) {
                            // Called whenever a connection is established. We store the connection for later so that we can close it.
                            logStream.onNext("Client started.");
                            connection = serverConnection;
                            isRunningStream.onNext(true);

                            Observable<String> receiver = serverConnection.getInput();

                            Observable<String> transmitter = transmitStream.getObservable()
                                    .flatMap(new Func1<String, Observable<String>>() {
                                        @Override
                                        public Observable<String> call(String message) {
                                            serverConnection.writeAndFlush(message);
                                            logStream.onNext("Client sent: " + message);
                                            return Observable.just("");
                                        }
                                    });

                            // Events must be merged and subscribed to in order to actually run. i.e. these are cold observables.
                            return Observable.merge(receiver, transmitter).subscribeOn(Schedulers.io());
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            logStream.onNext("Client completed");
                            stop();
                        }

                        @Override
                        public void onError(Throwable e) {
                            logStream.onNext("Client error: " + e);
                            stop();
                        }

                        @Override
                        public void onNext(String message) {
                            if (!message.isEmpty()) {
                                logStream.onNext("Client receive: " + message);
                                receiveStream.onNext(message);
                            }
                        }
                    });
        } else {
            logStream.onNext("Ignoring redundant start request.");
        }
    }

    /**
     * Sends a message to the connected server. If no server is connected, the message is swallowed.
     * @param message Message that will attempted to be sent to the server. Adds newline delimiter.
     */
    public void send(String message) {
        transmitStream.onNext(message);
    }

    /**
     * Stops the client. The client will no longer attempt to reconnect/reconnect to servers.
     */
    public void stop() {
        if (client != null) {
            try {
                logStream.onNext("Disconnecting client...");
                RxClient<String, String> temp = client;
                client = null;
                isRunningStream.onNext(false);
                RxUtils.safeUnsubscribe(clientSubscription);
                if (connection != null) {
                    connection.getChannel().close();
                    connection.close();
                }
                temp.shutdown();
                logStream.onNext("Client stopped.");
            } catch (Exception e) {
                logStream.onNext(e.toString());
            }
        }
    }

    /**
     * Boolean condition indicating if client is running. Note that running is not the same as
     * connected, i.e. running server might not be connected to a server. This is because a client
     * won't start running until it successfully connects to a server, but if that server goes down,
     * the client will still be running, attempting to reconnect.
     * @return Boolean condition indicating if server is running.
     * @see #getIsRunningObservable()
     */
    public boolean isRunning() {
        return isRunningStream.getLast();
    }

    /**
     * Waits until the isRunning status has changed for a duration.
     * @param timeSpan How long to wait for the isRunning status to change.
     * @param timeUnit How to interpret timeSpan.
     * @return If a timeout has occurred.
     */
    public boolean awaitNextIsRunningChanged(long timeSpan, TimeUnit timeUnit) {
        final Box<Boolean> timedOut = new Box<>(false);
        final boolean isRunningNow = isRunningStream.getLast();
        getIsRunningObservable()
            .timeout(timeSpan, timeUnit)
            .onErrorResumeNext(new Func1<Throwable, Observable<Boolean>>() {
                @Override
                public Observable<Boolean> call(Throwable e) {
                    timedOut.value = true;
                    return Observable.just(isRunningNow);
                }
            })
            .toBlocking()
            .first();
        return timedOut.value;
    }

    /**
     * An observable stream of newline delimited messages received from the server.
     * @return An observable stream of newline delimited messages received from the server.
     */
    public Observable<String> getObservable() {
        return receiveStream.getObservable();
    }

    /**
     * An observable stream of client running status changes.
     * @return An observable stream of server running status changes.
     * @see #isRunning()
     */
    public Observable<Boolean> getIsRunningObservable() {
        return isRunningStream.getObservable();
    }

    /**
     * An observable stream of log messages provided as Strings.
     * @return An observable stream of log messages provided as Strings.
     */
    public Observable<String> getObservableLog() {
        return logStream.getObservable();
    }
}

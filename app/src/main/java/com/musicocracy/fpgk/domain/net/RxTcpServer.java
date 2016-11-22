package com.musicocracy.fpgk.domain.net;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ConnectionHandler;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.server.RxServer;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 * A functional reactive TCP server. Allows users to respond to events asynchronously.
 * @see RxTcpClient
 */
public class RxTcpServer {
    private final SharedSubject<Boolean> isRunningStream;
    private final SharedSubject<String> logStream;
    private final SharedSubject<String> transmitStream;
    private final SharedSubject<StringMessageBySender> receiveStream;
    private RxServer<String, String> server = null;

    /**
     * Basic constructor. Initializes {@link #isRunning()} to false
     */
    public RxTcpServer() {
        this(SharedSubject.<Boolean>create(),
                SharedSubject.<String>create(),
                SharedSubject.<String>create(),
                SharedSubject.<StringMessageBySender>create());
    }

    /**
     * Dependency Injection friendly constructor. Recommended for testing only.
     * @param isRunningStream Subject used to notify user if running status has changed
     * @param logStream Subject used to notify user of newly logged events
     * @param transmitStream Subject used to allow the user to send messages
     * @param receiveStream Subject used to notify the user of received messages
     */
    public RxTcpServer(SharedSubject<Boolean> isRunningStream,
                       SharedSubject<String> logStream,
                       SharedSubject<String> transmitStream,
                       SharedSubject<StringMessageBySender> receiveStream) {
        this.isRunningStream = isRunningStream;
        this.logStream = logStream;
        this.transmitStream = transmitStream;
        this.receiveStream = receiveStream;

        // The server is not running initially. Set here because we grab the last result
        //  of isRunningStream for isRunning().
        isRunningStream.onNext(false);
    }

    /**
     * Starts the server on the specified port. The server will automatically service any new clients
     * that connect.
     * @param port Port number
     */
    public void start(int port) {
        if (server == null) {
            logStream.onNext("Starting server...");
            server = RxNetty.createTcpServer(port, PipelineConfigurators.textOnlyConfigurator(), new ConnectionHandler<String, String>() {
                @Override
                public Observable<Void> handle(final ObservableConnection<String, String> newConnection) {
                    // Called every time a new client connects.
                    logStream.onNext("New connection established...");
                    Observable<Void> receiver = newConnection.getInput()
                            .flatMap(new Func1<String, Observable<? extends Void>>() {
                                @Override
                                public Observable<? extends Void> call(String message) {    // called when connection sends something
                                    // Because the server doesn't stop immediately, we manually check if we're supposed to be running before sending events.
                                    if (isRunning()) {
                                        logStream.onNext("Received: " + message);
                                        message = message.trim();
                                        if (!message.isEmpty()) {
                                            receiveStream.onNext(new StringMessageBySender(message, newConnection));
                                        }
                                    }
                                    return Observable.empty();
                                }
                            })
                            .doAfterTerminate(new Action0() {
                                @Override
                                public void call() {
                                    logStream.onNext("Terminating connection...");
                                    if (newConnection != null) {
                                        newConnection.getChannel().close();
                                        newConnection.close();
                                    }
                                }
                            });

                    Observable<Void> transmitter = transmitStream.getObservable()
                            .flatMap(new Func1<String, Observable<? extends Void>>() {
                                @Override
                                public Observable<? extends Void> call(String message) {
                                    logStream.onNext("Server sent: " + message);
                                    return newConnection.writeAndFlush(message);
                                }
                            });

                    // Events must be merged and subscribed to in order to actually run. i.e. these are cold observables.
                    return Observable.merge(receiver, transmitter);
                }
            });
            server.start();
            isRunningStream.onNext(true);
            logStream.onNext("Server started.");
        } else {
            logStream.onNext("Ignoring redundant start request.");
        }
    }

    /**
     * Sends a message to every client. If no clients are connected, the message is swallowed.
     * @param message Message that will be broadcast to all clients. Adds newline delimiter.
     */
    public void sendToAll(String message) {
        transmitStream.onNext(message);
    }

    /**
     * Stops the server. The server will no longer respond to events.
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            logStream.onNext("Stopping server...");
            isRunningStream.onNext(false);
            try {
                server.shutdown();
                logStream.onNext("Server stopped.");
            } finally {
                server = null;
            }
        }
    }

    /**
     * Boolean condition indicating if server is running.
     * @return Boolean condition indicating if server is running.
     * @see #getIsRunningObservable()
     */
    public boolean isRunning() {
        return isRunningStream.getLast();
    }

    /**
     * An observable stream of newline delimited messages received from a client. Messages are
     * provided with the accompanied sender connection, enabling users to reply to client messages
     * should they choose to.
     * @return An observable stream of messages by client sender. Separated on newline boundaries.
     */
    public Observable<StringMessageBySender> getObservable() {
        return receiveStream.getObservable();
    }

    /**
     * An observable stream of server running status changes.
     * @return An observable stream of server running status changes.
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

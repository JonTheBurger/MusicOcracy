package com.musicocracy.fpgk.model;

import com.musicocracy.fpgk.model.net.WritableEventStream;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ConnectionHandler;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.client.RxClient;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.server.RxServer;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NetworkTestModel {
    private static class MessageBySender {
        public final String message;
        public final ObservableConnection<String, String> sender;

        public MessageBySender(String message, ObservableConnection<String, String> sender) {
            this.message = message;
            this.sender = sender;
        }
    }

    private final WritableEventStream<String> serverLog = new WritableEventStream<>();
    private final WritableEventStream<String> serverOutput = new WritableEventStream<>();
    private final WritableEventStream<MessageBySender> serverInput = new WritableEventStream<>();
    private RxServer<String, String> server = null;

    private final WritableEventStream<String> clientLog = new WritableEventStream<>();
    private final WritableEventStream<String> clientOutput = new WritableEventStream<>();
    private ObservableConnection<String, String> clientConnection;
    private RxClient<String, String> client = null;
    private Subscription clientSubscription;

    public NetworkTestModel() {
        serverInput.getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MessageBySender>() {
            @Override
            public void call(MessageBySender messageBySender) {
                messageBySender.sender.writeAndFlush("echo -> " + messageBySender.message + '\n');
            }
        });
    }

    public void startServer(int port) {
        if (server == null) {
            serverLog.broadcast("Starting server...");
            server = RxNetty.createTcpServer(port, PipelineConfigurators.textOnlyConfigurator(), new ConnectionHandler<String, String>() {
                @Override
                public Observable<Void> handle(final ObservableConnection<String, String> newConnection) {
                    serverLog.broadcast("New connection established...");
                    // Receiver
                    Observable<Void> rx = newConnection.getInput()
                        .flatMap(new Func1<String, Observable<? extends Void>>() {
                            @Override
                            public Observable<? extends Void> call(String msg) {    // called when connection sends something
                                if (server != null) {
                                    serverLog.broadcast("Received: " + msg);
                                    msg = msg.trim();
                                    if (!msg.isEmpty()) {
                                        serverInput.broadcast(new MessageBySender(msg, newConnection));
                                    }
                                }
                                return Observable.empty();
                            }
                        })
                        .doAfterTerminate(new Action0() {
                            @Override
                            public void call() {
                                serverLog.broadcast("Terminating connection...");
                                if (newConnection != null) {
                                    newConnection.getChannel().close();
                                    newConnection.close();
                                }
                            }
                        });

                    // Transmitter
                    Observable<Void> tx = serverOutput.getObservable()
                        .flatMap(new Func1<String, Observable<? extends Void>>() {
                            @Override
                            public Observable<? extends Void> call(String s) {
                                return newConnection.writeAndFlush(s);
                            }
                        });

                    return Observable.merge(rx, tx);
                }
            });
            server.start();
            serverLog.broadcast("Server started.");
        } else {
            serverLog.broadcast("Ignoring redundant start request.");
        }
    }

    public void serverSend(String s) {
        serverOutput.broadcast(s);
    }

    public void stopServer() throws InterruptedException {
        if (server != null) {
            serverLog.broadcast("Stopping server...");
            RxServer<String, String> temp = server;
            server = null;
            temp.shutdown();
            serverLog.broadcast("Server stopped.");
        }
    }

    public Observable<String> getServerEventObservable() {
        return serverLog.getObservable();
    }

    public boolean isServerRunning() {
        return server != null;
    }

    public void startClient(String host, int port) {
        if (client == null) {
            clientLog.broadcast("Attempting to connect...");
            client = RxNetty.createTcpClient(host, port, PipelineConfigurators.textOnlyConfigurator());
            clientSubscription = client.connect()
                    .flatMap(new Func1<ObservableConnection<String, String>, Observable<String>>() {
                        @Override
                        public Observable<String> call(final ObservableConnection<String, String> serverConnection) {
                            clientConnection = serverConnection;

                            // Receive
                            Observable<String> rx = serverConnection
                                    .getInput()
                                    .map(new Func1<String, String>() {
                                        @Override
                                        public String call(String s) {
                                            return s.trim();
                                        }
                                    });

                            // Transmit
                            Observable<String> tx = clientOutput.getObservable()
                                    .flatMap(new Func1<String, Observable<String>>() {
                                        @Override
                                        public Observable<String> call(String s) {
                                            serverConnection.writeAndFlush(s);
                                            return Observable.just("");
                                        }
                                    });

                            return Observable.merge(rx, tx);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            clientLog.broadcast("Client completed");
                            stopClient();
                        }

                        @Override
                        public void onError(Throwable e) {
                            clientLog.broadcast("Client error: " + e);
                            stopClient();
                        }

                        @Override
                        public void onNext(String s) {
                            if (!s.isEmpty()) {
                                clientLog.broadcast("Client receive: " + s);
                            }
                        }
                    });

            clientLog.broadcast("Client connected.");
        } else {
            clientLog.broadcast("Ignoring redundant connect request.");
        }
    }

    public void clientSend(String s) {
        clientOutput.broadcast(s);
    }

    public void stopClient() {
        if (client != null) {
            clientLog.broadcast("Disconnecting client...");
            clientSubscription.unsubscribe();
            if (clientConnection != null) {
                clientConnection.getChannel().close();
                clientConnection.close();
            }
            RxClient<String, String> temp = client;
            client = null;
            temp.shutdown();
            clientLog.broadcast("Client disconnected.");
        }
    }

    public Observable<String> getClientEventObservable() {
        return clientLog.getObservable();
    }

    public boolean isClientConnected() {
        return client != null;
    }
}

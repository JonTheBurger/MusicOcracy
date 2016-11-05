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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class NetworkTestModel {
    private static class MessageBySender {
        public final String message;
        public final ObservableConnection<String, String> sender;

        public MessageBySender(String message, ObservableConnection<String, String> sender) {
            this.message = message;
            this.sender = sender;
        }
    }

    private final Subject<String, String> serverEvents = PublishSubject.create();
    private final Observable<String> serverEventObservable = serverEvents.asObservable().share();
    private final WritableEventStream<String> serverOutput = new WritableEventStream<>();
    private final WritableEventStream<MessageBySender> serverInput = new WritableEventStream<>();
    private RxServer<String, String> server = null;

    private final Subject<String, String> clientEvents = PublishSubject.create();
    private final Observable<String> clientEventObservable = clientEvents.asObservable().share();
    private final WritableEventStream<String> clientOutput = new WritableEventStream<>();
    private RxClient<String, String> client = null;

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
            serverEvents.onNext("Starting Server...");
            server = RxNetty.createTcpServer(port, PipelineConfigurators.textOnlyConfigurator(), new ConnectionHandler<String, String>() {
                @Override
                public Observable<Void> handle(final ObservableConnection<String, String> newConnection) {
                    serverEvents.onNext("New Connection Established...");
                    // Receiver
                    Observable<Void> rx = newConnection.getInput().flatMap(new Func1<String, Observable<? extends Void>>() {
                        @Override
                        public Observable<? extends Void> call(String msg) {    // called when connection sends something
                            serverEvents.onNext("Received: " + msg);
                            msg = msg.trim();
                            if (!msg.isEmpty()) {
                                serverInput.broadcast(new MessageBySender(msg, newConnection));
                            }
                            return Observable.empty();
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
            serverEvents.onNext("Server Started.");
        } else {
            serverEvents.onNext("Ignoring redundant start request.");
        }
    }

    public void serverSend(String s) {
        serverOutput.broadcast(s);
    }

    public void stopServer() throws InterruptedException {
        if (server != null) {
            serverEvents.onNext("Stopping Server...");
            RxServer<String, String> temp = server;
            server = null;
            temp.shutdown();
            serverEvents.onNext("Server Stopped.");
            serverEvents.onCompleted();
        }
    }

    public Observable<String> getServerEventObservable() {
        return serverEventObservable;
    }

    public boolean isServerRunning() {
        return server != null;
    }

    public void startClient(String host, int port) {
        if (client == null) {
            clientEvents.onNext("Attempting to connect...");
            client = RxNetty.createTcpClient(host, port, PipelineConfigurators.textOnlyConfigurator());
            client.connect()
                .flatMap(new Func1<ObservableConnection<String, String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(final ObservableConnection<String, String> serverConnection) {
                        // Receive
                        Observable<String> rx = serverConnection
                            .getInput()
                            .map(new Func1<String, String>() {
                                @Override
                                public String call(String s) {
                                    return s.trim();
                                }
                            })
                            .takeWhile(new Func1<String, Boolean>() {
                                @Override
                                public Boolean call(String s) {
                                    return !s.equals("END");
                                }
                            });

                        // Transmit
                        Observable<String> tx = clientOutput.getObservable()
                            .flatMap(new Func1<String, Observable<Void>>() {
                                @Override
                                public Observable<Void> call(String s) {
                                    return serverConnection.writeAndFlush(s);
                                }
                            })
                            .map(new Func1<Void, String>() {
                                @Override
                                public String call(Void aVoid) {
                                    return "";
                                }
                            });

                        return Observable.merge(rx, tx);
                    }
                })
                .takeWhile(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !s.equals("END");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        clientEvents.onNext("OnCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        clientEvents.onNext("OnError: " + e);
                        stopClient();
                    }

                    @Override
                    public void onNext(String s) {
                        clientEvents.onNext("OnNext: " + s);
                    }
                });

            clientEvents.onNext("Client connected.");
        } else {
            clientEvents.onNext("Ignoring redundant connect request.");
        }
    }

    public void clientSend(String s) {
        clientOutput.broadcast(s);
    }

    public void stopClient() {
        if (client != null) {
            clientEvents.onNext("Disconnecting Client...");
            RxClient<String, String> temp = client;
            client = null;
            temp.shutdown();
            clientEvents.onNext("Client Disconnected.");
            clientEvents.onCompleted();
        }
    }

    public Observable<String> getClientEventObservable() {
        return clientEventObservable;
    }

    public boolean isClientConnected() {
        return client != null;
    }
}

package com.musicocracy.fpgk.model;

import java.util.concurrent.TimeUnit;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ConnectionHandler;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.client.RxClient;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.server.RxServer;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class NetworkTestModel {
    private final Subject<String, String> serverEvents = PublishSubject.create();
    private final Observable<String> serverEventObservable = serverEvents.asObservable().share();
    private RxServer<String, String> server = null;

    private final Subject<String, String> clientEvents = PublishSubject.create();
    private final Observable<String> clientEventObservable = clientEvents.asObservable().share();
    private RxClient<String, String> client = null;

    public NetworkTestModel() {
    }

    public void startServer(int port) {
        if (server == null) {
            serverEvents.onNext("Starting Server...");
            server = RxNetty.createTcpServer(port, PipelineConfigurators.textOnlyConfigurator(), new ConnectionHandler<String, String>() {
                @Override
                public Observable<Void> handle(final ObservableConnection<String, String> newConnection) {
                    serverEvents.onNext("New Connection Established...");
                    return newConnection.getInput().flatMap(new Func1<String, Observable<? extends Void>>() {
                        @Override
                        public Observable<? extends Void> call(String msg) {    // called when connection sends something
                            serverEvents.onNext("Received: " + msg);
                            msg = msg.trim();
                            if (!msg.isEmpty()) {
                                return newConnection.writeAndFlush("echo -> " + msg + '\n');
                            } else {
                                return Observable.empty();
                            }
                        }
                    });
                }
            });
            server.start();
            serverEvents.onNext("Server Started.");
        } else {
            serverEvents.onNext("Ignoring redundant start request.");
        }
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
            client
                .connect()
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
                        Observable<String> tx = Observable.interval(500, TimeUnit.MILLISECONDS)
                                .flatMap(new Func1<Long, Observable<String>>() {
                                    @Override
                                    public Observable<String> call(Long aLong) {
                                        return serverConnection.writeAndFlush(String.valueOf(aLong + 1))
                                                .map(new Func1<Void, String>() {
                                                    @Override
                                                    public String call(Void aVoid) {
                                                        return "";
                                                    }
                                                });
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

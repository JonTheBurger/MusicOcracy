package com.musicocracy.fpgk.model.net;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ConnectionHandler;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.server.RxServer;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class RxTcpServer {
    private final SharedSubject<Boolean> isRunningStream = SharedSubject.create();
    private final SharedSubject<String> logStream = SharedSubject.create();
    private final SharedSubject<String> transmitStream = SharedSubject.create();
    private final SharedSubject<MessageBySender> receiveStream = SharedSubject.create();
    private RxServer<String, String> server = null;

    public RxTcpServer() {
        isRunningStream.onNext(false);
    }

    public void start(int port) {
        if (server == null) {
            logStream.onNext("Starting server...");
            server = RxNetty.createTcpServer(port, PipelineConfigurators.textOnlyConfigurator(), new ConnectionHandler<String, String>() {
                @Override
                public Observable<Void> handle(final ObservableConnection<String, String> newConnection) {
                    logStream.onNext("New connection established...");
                    // Receiver
                    Observable<Void> rx = newConnection.getInput()
                            .flatMap(new Func1<String, Observable<? extends Void>>() {
                                @Override
                                public Observable<? extends Void> call(String msg) {    // called when connection sends something
                                    if (server != null) {
                                        logStream.onNext("Received: " + msg);
                                        msg = msg.trim();
                                        if (!msg.isEmpty()) {
                                            receiveStream.onNext(new MessageBySender(msg, newConnection));
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

                    // Transmitter
                    Observable<Void> tx = transmitStream.getObservable()
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
            isRunningStream.onNext(true);
            logStream.onNext("Server started.");
        } else {
            logStream.onNext("Ignoring redundant start request.");
        }
    }

    public Observable<MessageBySender> getObservable() {
        return receiveStream.getObservable();
    }

    public Observable<Boolean> getIsRunningObservable() {
        return isRunningStream.getObservable();
    }

    public void sendToAll(String s) {
        transmitStream.onNext(s);
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            logStream.onNext("Stopping server...");
            isRunningStream.onNext(false);
            RxServer<String, String> temp = server;
            server = null;
            temp.shutdown();
            logStream.onNext("Server stopped.");
        }
    }

    public Observable<String> getObservableLog() {
        return logStream.getObservable();
    }

    public boolean isRunning() {
        return isRunningStream.getLast();
    }
}

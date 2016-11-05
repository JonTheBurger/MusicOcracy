package com.musicocracy.fpgk.model.net;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ConnectionHandler;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.server.RxServer;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

public class TcpServerEventBus {
    private final SharedSubject<String> serverLog = SharedSubject.create();
    private final SharedSubject<String> serverOutput = SharedSubject.create();
    private final SharedSubject<MessageBySender> serverInput = SharedSubject.create();
    private RxServer<String, String> server = null;

    public void startServer(int port) {
        if (server == null) {
            serverLog.onNext("Starting server...");
            server = RxNetty.createTcpServer(port, PipelineConfigurators.textOnlyConfigurator(), new ConnectionHandler<String, String>() {
                @Override
                public Observable<Void> handle(final ObservableConnection<String, String> newConnection) {
                    serverLog.onNext("New connection established...");
                    // Receiver
                    Observable<Void> rx = newConnection.getInput()
                            .flatMap(new Func1<String, Observable<? extends Void>>() {
                                @Override
                                public Observable<? extends Void> call(String msg) {    // called when connection sends something
                                    if (server != null) {
                                        serverLog.onNext("Received: " + msg);
                                        msg = msg.trim();
                                        if (!msg.isEmpty()) {
                                            serverInput.onNext(new MessageBySender(msg, newConnection));
                                        }
                                    }
                                    return Observable.empty();
                                }
                            })
                            .doAfterTerminate(new Action0() {
                                @Override
                                public void call() {
                                    serverLog.onNext("Terminating connection...");
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
            serverLog.onNext("Server started.");
        } else {
            serverLog.onNext("Ignoring redundant start request.");
        }
    }

    public void broadcast(String msg) {
        serverOutput.onNext(msg);
    }

    public Observable<MessageBySender> getObservable() {
        return serverInput.getObservable();
    }

    public void serverSend(String s) {
        serverOutput.onNext(s);
    }

    public void stopServer() throws InterruptedException {
        if (server != null) {
            serverLog.onNext("Stopping server...");
            RxServer<String, String> temp = server;
            server = null;
            temp.shutdown();
            serverLog.onNext("Server stopped.");
        }
    }

    public Observable<String> getServerLogObservable() {
        return serverLog.getObservable();
    }

    public boolean isServerRunning() {
        return server != null;
    }
}

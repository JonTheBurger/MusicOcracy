package com.musicocracy.fpgk.model.net;

import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.ObservableConnection;
import io.reactivex.netty.client.RxClient;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TcpClientEventBus {
    private final SharedSubject<String> clientLog = SharedSubject.create();
    private final SharedSubject<String> clientInput = SharedSubject.create();
    private final SharedSubject<String> clientOutput = SharedSubject.create();
    private ObservableConnection<String, String> clientConnection;
    private RxClient<String, String> client = null;
    private Subscription clientSubscription;

    public void startClient(String host, int port) {
        if (client == null) {
            clientLog.onNext("Attempting to connect...");
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
                            clientLog.onNext("Client completed");
                            clientInput.onCompleted();
                            stopClient();
                        }

                        @Override
                        public void onError(Throwable e) {
                            clientLog.onNext("Client error: " + e);
                            clientInput.onError(e);
                            stopClient();
                        }

                        @Override
                        public void onNext(String s) {
                            if (!s.isEmpty()) {
                                clientLog.onNext("Client receive: " + s);
                                clientInput.onNext(s);
                            }
                        }
                    });

            clientLog.onNext("Client connected.");
        } else {
            clientLog.onNext("Ignoring redundant connect request.");
        }
    }

    public void clientSend(String s) {
        clientOutput.onNext(s);
    }

    public void stopClient() {
        if (client != null) {
            clientLog.onNext("Disconnecting client...");
            clientSubscription.unsubscribe();
            if (clientConnection != null) {
                clientConnection.getChannel().close();
                clientConnection.close();
            }
            RxClient<String, String> temp = client;
            client = null;
            temp.shutdown();
            clientLog.onNext("Client disconnected.");
        }
    }

    public Observable<String> getObservable() {
        return clientInput.getObservable();
    }

    public Observable<String> getClientLogObservable() {
        return clientLog.getObservable();
    }

    public boolean isClientConnected() {
        return client != null;
    }
}

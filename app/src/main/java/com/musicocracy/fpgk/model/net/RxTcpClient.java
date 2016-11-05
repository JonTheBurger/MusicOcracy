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

public class RxTcpClient {
    private final SharedSubject<Boolean> isRunningStream = SharedSubject.create();
    private final SharedSubject<String> logStream = SharedSubject.create();
    private final SharedSubject<String> receiveStream = SharedSubject.create();
    private final SharedSubject<String> transmitStream = SharedSubject.create();
    private ObservableConnection<String, String> connection;
    private RxClient<String, String> client = null;
    private Subscription clientSubscription;

    public RxTcpClient() {
        isRunningStream.onNext(false);
    }

    public void start(String host, int port) {
        if (client == null) {
            logStream.onNext("Attempting to start...");
            client = RxNetty.createTcpClient(host, port, PipelineConfigurators.textOnlyConfigurator());
            clientSubscription = client.connect()
                    .flatMap(new Func1<ObservableConnection<String, String>, Observable<String>>() {
                        @Override
                        public Observable<String> call(final ObservableConnection<String, String> serverConnection) {
                            connection = serverConnection;

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
                            Observable<String> tx = transmitStream.getObservable()
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
                            logStream.onNext("Client completed");
                            stop();
                        }

                        @Override
                        public void onError(Throwable e) {
                            logStream.onNext("Client error: " + e);
                            stop();
                        }

                        @Override
                        public void onNext(String s) {
                            if (!s.isEmpty()) {
                                logStream.onNext("Client receive: " + s);
                                receiveStream.onNext(s);
                            }
                        }
                    });

            isRunningStream.onNext(true);
            logStream.onNext("Client connected.");
        } else {
            logStream.onNext("Ignoring redundant start request.");
        }
    }

    public void send(String s) {
        transmitStream.onNext(s);
    }

    public void stop() {
        if (client != null) {
            logStream.onNext("Disconnecting client...");
            isRunningStream.onNext(false);
            clientSubscription.unsubscribe();
            if (connection != null) {
                connection.getChannel().close();
                connection.close();
            }
            RxClient<String, String> temp = client;
            client = null;
            temp.shutdown();
            logStream.onNext("Client disconnected.");
        }
    }

    public Observable<String> getObservable() {
        return receiveStream.getObservable();
    }

    public Observable<Boolean> getIsRunningStream() {
        return isRunningStream.getObservable();
    }

    public Observable<String> getObservableLog() {
        return logStream.getObservable();
    }

    public boolean isConnected() {
        return isRunningStream.getLast();
    }
}

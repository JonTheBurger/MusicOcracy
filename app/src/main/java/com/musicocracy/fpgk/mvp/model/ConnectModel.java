package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.ClientEventBus;

import rx.Observable;

public class ConnectModel {
    private final ClientEventBus client;

    public ConnectModel(ClientEventBus client) {
        this.client = client;
    }

    public void connect(String host, int port) {
        client.start(host, port);
    }

    public boolean isRunning() {
        return client.isRunning();
    }

    public Observable<Boolean> getIsRunningObservable() {
        return client.getIsRunningObservable();
    }

    public void stopClient() {
        client.stop();
    }
}

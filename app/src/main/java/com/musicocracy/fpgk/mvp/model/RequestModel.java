package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.ClientEventBus;

public class RequestModel {
    private final ClientEventBus client;

    public RequestModel(ClientEventBus client) {
        this.client = client;
    }

    public void stopClient() {
        client.stop();
    }
}

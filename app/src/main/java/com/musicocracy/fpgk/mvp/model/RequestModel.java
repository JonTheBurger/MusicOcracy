package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.net.ClientHandler;

public class RequestModel {
    private final ClientEventBus client;
    private final ClientHandler clientHandler;

    public RequestModel(ClientEventBus client, ClientHandler clientHandler) {
        this.client = client;
        this.clientHandler = clientHandler;
    }

    public void stopClient() {
        clientHandler.onDestroy();
        client.stop();
    }
}

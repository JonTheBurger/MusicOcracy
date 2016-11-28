package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.musicocracy.fpgk.domain.util.PartySettings;

public class PartyConfigModel {
    private final PartySettings settings;
    private final ServerEventBus server;
    private final ServerHandler handler;
    private final int port;

    public PartyConfigModel(PartySettings settings, ServerEventBus server, ServerHandler handler, int port) {
        this.settings = settings;
        this.server = server;
        this.handler = handler;
        this.port = port;
    }

    public PartySettings getSettings() {
        return settings;
    }

    public void startServer() {
        server.start(port);
        handler.onCreate();
    }

    public boolean serverRunning() {
        return server.isRunning();
    }

    public void stopServer() throws InterruptedException {
        server.stop();
        handler.onDestroy();
    }
}

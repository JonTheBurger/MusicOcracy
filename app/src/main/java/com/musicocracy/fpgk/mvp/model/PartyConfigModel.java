package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.IpUtils;
import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.util.PartySettings;

public class PartyConfigModel {
    private final PartySettings settings;
    private final ServerEventBus server;

    public PartyConfigModel(PartySettings settings, ServerEventBus server) {
        this.settings = settings;
        this.server = server;
    }

    public PartySettings getSettings() {
        return settings;
    }

    public void startServer() {
        server.start(IpUtils.DEFAULT_PORT);
    }

    public void stopServer() throws InterruptedException {
        server.stop();
    }
}

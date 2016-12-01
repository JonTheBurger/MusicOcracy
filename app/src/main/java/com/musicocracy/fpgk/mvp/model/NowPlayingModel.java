package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class NowPlayingModel {
    private ServerHandler serverHandler;

    public NowPlayingModel(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public ServerHandler getServerHandler() { return serverHandler; }

    public Metadata.Track getCurrentPlayingTrack() {
        return serverHandler.getCurrentlyPlayingTrack();
    }
}

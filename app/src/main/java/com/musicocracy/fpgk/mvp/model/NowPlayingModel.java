package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class NowPlayingModel {
    private ServerHandler serverHandler;
    private ReadOnlyPartySettings partySettings;

    public NowPlayingModel(ServerHandler serverHandler, ReadOnlyPartySettings partySettings) {
        this.serverHandler = serverHandler;
    }

    public ServerHandler getServerHandler() { return serverHandler; }

    public Metadata.Track getCurrentPlayingTrack() {
        return serverHandler.getCurrentlyPlayingTrack();
    }

    public String getPartyCode() {
        return partySettings.getPartyCode();
    }

    public String getPartyName() {
        return partySettings.getPartyName();
    }
}

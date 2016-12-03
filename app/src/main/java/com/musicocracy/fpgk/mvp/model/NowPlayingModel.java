package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class NowPlayingModel {
    private ServerHandler serverHandler;
    private ReadOnlyPartySettings partySettings;

    public NowPlayingModel(ServerHandler serverHandler, ReadOnlyPartySettings partySettings) {
        this.serverHandler = serverHandler;
        this.partySettings = partySettings;
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

    public List<Track> getVotableTracks() {
        return serverHandler.getVotableTracks();
    }
}

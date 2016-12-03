package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.musicocracy.fpgk.domain.spotify.SpotifyPlayerHandler;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;
import com.spotify.sdk.android.player.Metadata;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class NowPlayingModel {
    private final ServerHandler serverHandler;
    private final ReadOnlyPartySettings partySettings;
    private final SpotifyPlayerHandler playerHandler;

    public NowPlayingModel(ServerHandler serverHandler, ReadOnlyPartySettings partySettings, SpotifyPlayerHandler playerHandler) {
        this.serverHandler = serverHandler;
        this.partySettings = partySettings;
        this.playerHandler = playerHandler;
    }

    public SpotifyPlayerHandler getPlayerHandler() { return playerHandler; }

    public Metadata.Track getCurrentPlayingTrack() {
        return playerHandler.getCurrentlyPlayingTrack();
    }

    public String getPartyCode() {
        return partySettings.getPartyCode();
    }

    public String getPartyName() {
        return partySettings.getPartyName();
    }

    public List<String> getVotableURIs() {
        return serverHandler.getVotableURIs();
    }

    public List<Track> getVotableTracks(List<String> votableURIs) {
        return serverHandler.getVotableTracks(votableURIs);
    }
}

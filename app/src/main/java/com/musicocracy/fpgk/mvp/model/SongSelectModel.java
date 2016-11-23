package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.net.proto.BrowseSongsMsg;

import kaaes.spotify.webapi.android.SpotifyApi;

public class SongSelectModel {
    private final Browser browser;
    private final SpotifyApi api;
    private final ClientEventBus client;

    public SongSelectModel(Browser browser, SpotifyApi api, ClientEventBus client) {
        this.browser = browser;
        this.api = api;
        this.client = client;
    }

    public void sendBrowseMsg(BrowseSongsMsg msg) {
        client.send(msg);
    }

    public void setToken(String token) {
        api.setAccessToken(token);
    }
}

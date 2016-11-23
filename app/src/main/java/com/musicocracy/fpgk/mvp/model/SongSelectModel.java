package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.spotify.Browser;

import kaaes.spotify.webapi.android.SpotifyApi;

public class SongSelectModel {
    private final Browser browser;
    private final SpotifyApi api;

    public SongSelectModel(Browser browser, SpotifyApi api) {
        this.browser = browser;
        this.api = api;
    }

    public void setToken(String token) {

    }
}

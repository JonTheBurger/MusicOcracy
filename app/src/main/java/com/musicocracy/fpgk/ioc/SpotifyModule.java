package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.spotify.Browser;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

@Module
public class SpotifyModule {
    private static final String ClientID = "SpotifyClientID";

    @Provides
    @Singleton
    public SpotifyApi provideSpotifyApi() {
        SpotifyApi api = new SpotifyApi();
        return api;
    }

    @Provides
    @Singleton
    public SpotifyService provideSpotifyService(SpotifyApi api) {
        return api.getService();
    }

    @Provides
    @Singleton
    public Browser provideBrowser(SpotifyService spotifyService) {
        return new Browser(spotifyService);
    }

    @Provides @Named(ClientID) @Singleton
    public String provideSpotifyClientID () {
        return "4becf88681f74bda9e38baac3bcf66d6";
    }
}

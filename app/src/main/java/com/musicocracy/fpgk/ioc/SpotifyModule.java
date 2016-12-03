package com.musicocracy.fpgk.ioc;

import android.content.Context;

import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.domain.spotify.SpotifyPlayerHandler;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import retrofit.client.Client;

@Module
public class SpotifyModule {
    private static final String CLIENT_ID = "SpotifyClientID";

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

    @Provides
    @Singleton
    public Config providePlayerConfig(Context context, ReadOnlyPartySettings partySettings, @Named(CLIENT_ID) String clientID) {
        return new Config(context, partySettings.getSpotifyToken(), clientID);
    }

    @Provides
    @Singleton
    public SpotifyPlayer provideSpotifyPlayer(Config playerConfig, Context context, final Logger Log) {
        SpotifyPlayer.InitializationObserver spotifyPlayerObserver = new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {

            }

            @Override
            public void onError(Throwable throwable) {
                Log.error("SpotifyModule", "Could not initialize player: " + throwable.getMessage());
            }
        };
        return Spotify.getPlayer(playerConfig, context, spotifyPlayerObserver);
    }

    @Provides @Named(CLIENT_ID) @Singleton
    public String provideSpotifyClientID () {
        return "4becf88681f74bda9e38baac3bcf66d6";
    }

    @Provides
    @Singleton
    public SpotifyPlayerHandler provideSpotifyPlayerHandler(Logger log, SpotifyPlayer player,
                                                            DjAlgorithm djAlgorithm) {
        return new SpotifyPlayerHandler(log, player, djAlgorithm);
    }
}

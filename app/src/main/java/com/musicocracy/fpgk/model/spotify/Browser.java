package com.musicocracy.fpgk.model.spotify;

import android.content.Context;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Browser {
    private static final String CLIENT_ID = "4becf88681f74bda9e38baac3bcf66d6";
    private static SpotifyApi api;
    private static SpotifyService spotify;
    private static String token;
    private Player mPlayer;

    public Browser(String token, Context context) {
        api = new SpotifyApi();
        api.setAccessToken(token);
        spotify = api.getService();
        Config playerConfig = new Config(context, token, CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                mPlayer = spotifyPlayer;
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("BrowseActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    public void browseTracks(String trackName) {
        spotify.searchTracks(trackName, new Callback<TracksPager>() {
            @Override
            public void success(TracksPager tp, Response response) {
                Log.d("Browser", "Get TracksPager success");
                Log.d("TracksPager success", tp.tracks.items.get(0).uri);
                mPlayer.playUri(null, tp.tracks.items.get(0).uri, 0, 0);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Browser", "Get TracksPager failure");
                Log.d("TracksPager failure", error.toString());
            }
        });
    }
}
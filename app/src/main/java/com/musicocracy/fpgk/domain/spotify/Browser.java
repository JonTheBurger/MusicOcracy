package com.musicocracy.fpgk.domain.spotify;

import android.content.Context;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class Browser {
    private static final String CLIENT_ID = "4becf88681f74bda9e38baac3bcf66d6";
    private static final int NUM_RESULTS = 10;
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
                Log.e("TestBrowseActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    public List<String> browseTracks(String trackName) {

        List<Track> resultTracks = spotify.searchTracks(trackName).tracks.items;
        List<String> resultStrings = new ArrayList<>();
        //If result tracks are found
        if (resultTracks.size() != 0) {
            //Construct result string for tracks
            for (int i = 0; i < resultTracks.size() && i < NUM_RESULTS; i++) {
                resultStrings.add("Album: " + resultTracks.get(i).album.name +
                        ", Artist: " + resultTracks.get(i).artists.get(0).name);
            }
        }

        return resultStrings;
    }
}
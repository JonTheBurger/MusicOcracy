package com.musicocracy.fpgk.model.spotify;

import android.content.Context;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;
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
                Log.e("TestBrowseActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    public List<Track> browseTracks(String trackName) {

        List<Track> resultTracks = spotify.searchTracks(trackName).tracks.items;

        //If result tracks are found
        if (resultTracks.size() != 0) {
            //Get the top 5 result tracks
            resultTracks = resultTracks.subList(0, 5);

            //Display the top 5 results
            for (int i = 0; i < resultTracks.size() && i < 5; i++) {
                Log.d("Browser", "Result: " + i + ", Album: " + resultTracks.get(i).album.name +
                        ", Artist: " + resultTracks.get(i).artists.get(0).name);
            }
        }

        return resultTracks;
    }
}
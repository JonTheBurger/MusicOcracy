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
    private static final int NUM_RESULTS = 10;
    private final SpotifyService spotify;

    public Browser(SpotifyService spotify) {
        this.spotify = spotify;
    }

    public List<Track> browseTracks(String trackName) {

        List<Track> resultTracks = spotify.searchTracks(trackName).tracks.items;
        //List<String> resultStrings = new ArrayList<>();
        //If result tracks are found
        if (resultTracks.size() != 0) {

            resultTracks = resultTracks.subList(0, NUM_RESULTS);
            /*
            //Construct result string for tracks
            for (int i = 0; i < resultTracks.size() && i < NUM_RESULTS; i++) {
                resultStrings.add("Album: " + resultTracks.get(i).album.name +
                        ", Artist: " + resultTracks.get(i).artists.get(0).name);
            }*/
        }

        return resultTracks;
    }
}
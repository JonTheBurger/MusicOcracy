package com.musicocracy.fpgk.domain.spotify;

import java.util.List;

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
        //If result tracks are found
        if (resultTracks.size() != 0) {
            resultTracks = resultTracks.subList(0, NUM_RESULTS);
        }

        return resultTracks;
    }

    public Track getTrackByURI(String uri) {
        String spotifyID = uri.split(":")[2];

        Track track = spotify.getTrack(spotifyID);

        return track;
    }
}
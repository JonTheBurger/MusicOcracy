package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class NowPlayingModel {
    private ServerHandler serverHandler;

    public NowPlayingModel(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public ServerHandler getServerHandler() { return serverHandler; }


    /*
    public String getCurrentPlayingArtist() {
        if (player.getPlaybackState().isPlaying) {
            return player.getMetadata().currentTrack.artistName;
        } else {
            return "No Artist";
        }
    }

    public String getCurrentPlayingSongName() {
        if (player.getPlaybackState().isPlaying) {
            return player.getMetadata().currentTrack.name;
        } else {
            return "No Track";
        }
    }

    public String getCurrentPlayingAlbumCover() {
        if (player.getPlaybackState().isPlaying) {
            return player.getMetadata().currentTrack.albumCoverWebUrl;
        } else {
            return "No Album";
        }
    }

    public long getCurrentPlayingDuration() {
        if (player.getPlaybackState().isPlaying) {
            return player.getMetadata().currentTrack.durationMs;
        } else {
            return 0;
        }
    }*/
}

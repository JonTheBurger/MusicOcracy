package com.musicocracy.fpgk.domain.spotify;

import com.musicocracy.fpgk.domain.util.Logger;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class SpotifyPlayerHandler implements SpotifyPlayer.NotificationCallback {
    private static final String TAG = "SpotifyPlayerHandler";
    private static final int TIME_BEFORE_NEXT_SONG = 10000; //ms
    private final Logger log;
    private SpotifyPlayer player;
    private Timer nextSongTimer;
    private Timer playTimer;
    private boolean timerStarted = false;
    private String nextURI;

    public SpotifyPlayerHandler(Logger log, SpotifyPlayer player) {
        this.log = log;
        this.player = player;
        nextSongTimer = new Timer();
        playTimer = new Timer();

        player.addNotificationCallback(this);
    }

    public void startPlayTimer(String uri) {
        nextURI = uri;
        if (!isTimerStarted()) {
            player.playUri(null, uri, 0, 0);
            scheduleTimer();
            timerStarted = true;
        }
    }

    public boolean isTimerStarted() {
        return timerStarted;
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        if (playerEvent == PlayerEvent.kSpPlaybackNotifyTrackChanged) {
            scheduleTimer();
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        log.error(TAG, "Error in Playback of Spotify Player.");
    }

    private void scheduleTimer() {
        long currentTrackDuration = player.getMetadata().currentTrack.durationMs;

        // Timer will execute task before the song is done playing
        nextSongTimer.schedule(new TimerTask() {
            public void run() {
                // TODO: retrieve song from DJ algorithm, clear votes for previously played song
                player.queue(null, nextURI);
            }
        }, (currentTrackDuration - TIME_BEFORE_NEXT_SONG));
    }

    public void stopTimer() {
        player.removeNotificationCallback(this);
    }
}

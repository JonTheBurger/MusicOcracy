package com.musicocracy.fpgk.domain.util;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class SystemTimers implements SpotifyPlayer.NotificationCallback {
    private static final String TAG = "SystemTimers";
    private static final int TIME_BEFORE_NEXT_SONG = 10;
    private final Logger log;
    private SpotifyPlayer player;
    private Timer timer;
    private boolean timerStarted = false;

    public SystemTimers(Logger log, SpotifyPlayer player) {
        this.log = log;
        this.player = player;
        timer = new Timer();

        player.addNotificationCallback(this);
    }

    public void startPlayTimer(String uri) {
        if (!timerStarted) {
            player.playUri(null, uri, 0, 0);
            scheduleTimer();
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
        timer.schedule(new TimerTask() {
            public void run() {
                // TODO: retrieve song from DJ algorithm, queue the song, clear votes for previously played song
            }
        }, (currentTrackDuration - TIME_BEFORE_NEXT_SONG));
    }

    // TODO: remove notification callback when SystemTimers is done being used
}

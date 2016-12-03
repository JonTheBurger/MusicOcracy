package com.musicocracy.fpgk.domain.spotify;

import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.util.Logger;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Action1;

public class SpotifyPlayerHandler implements SpotifyPlayer.NotificationCallback {
    private static final String TAG = "SpotifyPlayerHandler";
    private static final int TIME_BEFORE_NEXT_SONG = 10000; //ms
    private final Logger log;
    private final DjAlgorithm djAlgorithm;
    private SpotifyPlayer player;
    private boolean playerStarted = false;

    public SpotifyPlayerHandler(Logger log, SpotifyPlayer player, DjAlgorithm djAlgorithm) {
        this.log = log;
        this.player = player;
        this.djAlgorithm = djAlgorithm;

        player.addNotificationCallback(this);
    }

    public void play(String uri) {
        if (!playerStarted) {
            try {
                player.playUri(null, djAlgorithm.dequeueNextSongUri(), 0, 0);
            } catch (SQLException e) {
                log.error(TAG, e.toString());
            }
            playerStarted = true;
        }
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
        long timerDelay = player.getMetadata().currentTrack.durationMs - TIME_BEFORE_NEXT_SONG;

        // If timer delay is negative, start the timer now
        if (timerDelay < 0) {
            timerDelay = 0;
        }

        Observable.timer(timerDelay , TimeUnit.MILLISECONDS)
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    String nextURI = null;
                    try {
                        nextURI = djAlgorithm.dequeueNextSongUri();
                    } catch (SQLException e) {
                        log.error(TAG, e.toString());
                    }
                    if (nextURI != null) {
                        player.queue(null, nextURI);
                        log.info(TAG, "Song Queued(URI): " + nextURI);
                    } else {
                        log.error(TAG, "Next song from DJ algorithm is null.");
                        scheduleTimer();
                    }
                }
            });
    }

    public void onDestroy() {
        player.removeNotificationCallback(this);
        playerStarted = false;
    }
}

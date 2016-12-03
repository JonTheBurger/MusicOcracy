package com.musicocracy.fpgk.domain.spotify;

import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.RxUtils;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class SpotifyPlayerHandler implements SpotifyPlayer.NotificationCallback {
    private static final String TAG = "SpotifyPlayerHandler";
    private static final int TIME_BEFORE_NEXT_SONG = 10000; //ms
    private final Logger log;
    private SpotifyPlayer player;
    private boolean playerStarted = false;

    public SpotifyPlayerHandler(Logger log, SpotifyPlayer player) {
        this.log = log;
        this.player = player;

        player.addNotificationCallback(this);
    }

    public void play(String uri) {
        if (!playerStarted) {
            // TODO: Load uri from algorithm
            player.playUri(null, uri, 0, 0);
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
                    // TODO: retrieve song from DJ algorithm, clear votes for previously played song
                    String nextURI = null;
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

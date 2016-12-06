package com.musicocracy.fpgk.domain.spotify;

import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.net.SharedSubject;
import com.musicocracy.fpgk.domain.util.Logger;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Action1;

public class SpotifyPlayerHandler implements SpotifyPlayer.NotificationCallback {
    private static final String TAG = "SpotifyPlayerHandler";
    private static final int TIME_BEFORE_NEXT_SONG = 10000; //ms
    private static final int DEFAULT_WAIT = 10000; // ms
    private final Logger log;
    private final DjAlgorithm djAlgorithm;
    private SpotifyPlayer player;
    private boolean playerStarted = false;
    private final SharedSubject<Metadata.Track> newTrackPlayingSubject = SharedSubject.create();

    public SpotifyPlayerHandler(Logger log, SpotifyPlayer player, DjAlgorithm djAlgorithm) {
        this.log = log;
        this.player = player;
        this.djAlgorithm = djAlgorithm;

        player.addNotificationCallback(this);
    }

    public void play() {
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
        log.verbose(TAG, "PlayerEvent: " + playerEvent.toString());
        if (playerEvent == PlayerEvent.kSpPlaybackNotifyTrackChanged) {
            log.verbose(TAG, "Track Changed Event.");
            newTrackPlayingSubject.onNext(player.getMetadata().currentTrack);
            scheduleTimer();
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        log.error(TAG, "Error in Playback of Spotify Player.");
    }

    private void scheduleTimer() {
        long timerDelay = DEFAULT_WAIT;
        if (player.getPlaybackState().isPlaying) {
             timerDelay = player.getMetadata().currentTrack.durationMs - TIME_BEFORE_NEXT_SONG;
        }

        // If timer delay is negative, start the timer now
        if (timerDelay < 0) {
            timerDelay = 0;
        }

        log.verbose(TAG, "Set play observable for: " + timerDelay + "ms");
        Observable.timer(timerDelay, TimeUnit.MILLISECONDS)
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    String nextURI = null;
                    try {
                        log.info(TAG, "Getting Song from DJ Algorithm...");
                        nextURI = djAlgorithm.dequeueNextSongUri();
                    } catch (SQLException e) {
                        log.error(TAG, e.toString());
                    }
                    if (nextURI != null && !nextURI.equals("")) {
                        player.queue(null, nextURI);
                        log.info(TAG, "Song Queued(URI): " + nextURI);
                    } else {
                        log.error(TAG, "Next song from DJ algorithm is null or empty.");
                        scheduleTimer();
                    }
                }
            });
    }

    public Observable<Metadata.Track> newSongPlaying() {
        return newTrackPlayingSubject.asObservable();
    }

    public Metadata.Track getCurrentlyPlayingTrack() {
        return newTrackPlayingSubject.getLast();
    }

    public void onDestroy() {
        newTrackPlayingSubject.onCompleted();
        player.removeNotificationCallback(this);
        playerStarted = false;
        Spotify.destroyPlayer(player);
    }
}

package com.musicocracy.fpgk.domain.spotify;

import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.net.SharedSubject;
import com.musicocracy.fpgk.domain.util.Logger;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class SpotifyPlayerHandler implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    private static final String TAG = "SpotifyPlayerHandler";
    private static final int TIME_BEFORE_NEXT_SONG = 10000; //ms
    private static final int DEFAULT_WAIT = 10000; // ms
    private static final int ONE_SECOND = 1000; //ms
    private final Logger log;
    private final DjAlgorithm djAlgorithm;
    private SpotifyPlayer player;
    private boolean playerStarted = false;
    private final SharedSubject<Metadata.Track> newTrackPlayingSubject = SharedSubject.create();
    private AtomicBoolean playbackCheckLock = new AtomicBoolean(false);
    private String lastNextURI;
    private final Player.OperationCallback operationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(Error error) {
            log.error(TAG, "Operation callback error: " + error.toString());
        }
    };

    public SpotifyPlayerHandler(Logger log, SpotifyPlayer player, DjAlgorithm djAlgorithm) {
        this.log = log;
        this.player = player;
        this.djAlgorithm = djAlgorithm;

        player.addNotificationCallback(this);
        player.addConnectionStateCallback(this);
    }

    public void play() {
        if (!playerStarted) {
            try {
                player.playUri(operationCallback, djAlgorithm.dequeueNextSongUri(), 0, 0);
            } catch (SQLException e) {
                log.error(TAG, e.toString());
            }
            playerStarted = true;
        }
        playBackCheck(ONE_SECOND);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        log.verbose(TAG, "PlayerEvent: " + playerEvent.toString());
        if (playerEvent == PlayerEvent.kSpPlaybackNotifyTrackChanged) {
            log.verbose(TAG, "Track Changed Event.");
            newTrackPlayingSubject.onNext(player.getMetadata().currentTrack);
            scheduleTimer();
        }

        /*
        if (playerEvent == PlayerEvent.kSpPlaybackNotifyBecameActive) {
            playBackCheck();
        }*/

        if (playerEvent == PlayerEvent.kSpPlaybackNotifyMetadataChanged) {
            if (!player.getPlaybackState().isPlaying) {
                playBackCheck(ONE_SECOND);
            }
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        log.error(TAG, "Error in Playback of Spotify Player: " + error.toString());
    }

    private void scheduleTimer() {
        long timerDelay = ONE_SECOND;
        if (player.getPlaybackState().isPlaying) {
            timerDelay = player.getMetadata().currentTrack.durationMs - TIME_BEFORE_NEXT_SONG;
        }

        // If timer delay is negative, start the timer now
        if (timerDelay < 0) {
            timerDelay = 0;
        }

        log.verbose(TAG, "Set play observable for: " + timerDelay + "ms.");
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
                        player.queue(operationCallback, nextURI);
                        log.info(TAG, "Song Queued(URI): " + nextURI);
                        if (player.getMetadata().nextTrack != null) {
                            if (!player.getPlaybackState().isPlaying || nextURI.equals(lastNextURI)) {
                                log.verbose(TAG, "Player not playing, skipping to next in queue.");
                                player.skipToNext(operationCallback);
                            }
                        }
                        lastNextURI = nextURI;
                        playBackCheck(DEFAULT_WAIT + (5 * ONE_SECOND));
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

    private void playBackCheck(long delayToCheckPlayBack) {
        if (!playbackCheckLock.getAndSet(true)) {
            log.verbose(TAG, "PlayBack Check. Set for " + delayToCheckPlayBack + "ms.");
            Observable.timer(delayToCheckPlayBack, TimeUnit.MILLISECONDS)
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {
                            if (!player.getPlaybackState().isPlaying) {
                                log.verbose(TAG, "Scheduling playback timer, because player never started.");
                                scheduleTimer();
                            }
                            playbackCheckLock.set(false);
                            log.verbose(TAG, "PlayBack Check completed. Player.isPlaying(): " + player.getPlaybackState().isPlaying);
                        }

                        @Override
                        public void onError(Throwable e) {
                            playbackCheckLock.set(false);
                            log.error(TAG, "Error in playBackCheck: " + e.toString());
                        }

                        @Override
                        public void onNext(Long aLong) {

                        }
                    });
        }
    }

    @Override
    public void onLoggedIn() {
        log.verbose(TAG, "Spotify Player logged in.");
    }

    @Override
    public void onLoggedOut() {
        log.verbose(TAG, "Spotify Player logged out.");
    }

    @Override
    public void onLoginFailed(Error error) {
        log.error(TAG, "Spotify Player login failed: " + error.toString());
    }

    @Override
    public void onTemporaryError() {
        log.error(TAG, "Spotify Player temporary error." );
    }

    @Override
    public void onConnectionMessage(String s) {
        log.verbose(TAG, "Spotify Player Connection Message: " + s);
    }

    public void onDestroy() {
        newTrackPlayingSubject.onCompleted();
        player.removeNotificationCallback(this);
        playerStarted = false;
        Spotify.destroyPlayer(player);
    }
}

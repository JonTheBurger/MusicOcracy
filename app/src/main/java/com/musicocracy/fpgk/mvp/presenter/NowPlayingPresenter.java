package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.util.RxUtils;
import com.musicocracy.fpgk.mvp.model.NowPlayingModel;
import com.musicocracy.fpgk.mvp.view.NowPlayingView;
import com.spotify.sdk.android.player.Metadata;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NowPlayingPresenter implements Presenter<NowPlayingView> {
    private final NowPlayingModel model;
    private final Subscription newNowPlayingUpdate;
    private final Subscription newVotableUpdate;
    private final Subscription newPlayRequest;
    private NowPlayingView view;

    public NowPlayingPresenter(NowPlayingModel model) {
        this.model = model;
        newNowPlayingUpdate = createPlayEventNowPlayingSub();
        newVotableUpdate = createPlayEventVotableSongsSub();
        newPlayRequest = createPlayRequestSub();
    }

    @Override
    public void setView(NowPlayingView view) {
        this.view = view;
    }

    public Subscription createPlayEventNowPlayingSub() {
        return model.getPlayerHandler().newSongPlaying()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Metadata.Track>() {
                    @Override
                    public void call(Metadata.Track track) {
                        updateNowPlaying(track);
                    }
                });
    }

    public Subscription createPlayEventVotableSongsSub() {
        return model.getPlayerHandler().newSongPlaying()
                .map(new Func1<Metadata.Track, List<String>>() {
                    @Override
                    public List<String> call(Metadata.Track ignored) {
                        return getVotableTrackStrings();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> votableSongs) {
                        view.updateVotableSongs(votableSongs);
                    }
                });
    }

    public Subscription createPlayRequestSub() {
        return model.getServerHandler().newPlayRequest()
                .map(new Func1<String, List<String>>() {
                        @Override
                        public List<String> call(String ignored) {
                            return getVotableTrackStrings();
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> votableSongs) {
                        view.updateVotableSongs(votableSongs);
                    }
                });
    }

    private List<String> getVotableTrackStrings() {
        List<String> uris = model.getVotableURIs();
        List<Track> tracks = model.getVotableTracks(uris);
        return convertTrackToString(tracks);
    }

    private List<String> convertTrackToString(List<Track> tracks) {
        List<String> voteList = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            voteList.add(Integer.toString(i + 1) + ") Title: " + tracks.get(i).name
                    + "\nArtist: " + tracks.get(i).artists.get(0).name);
        }
        return voteList;
    }

    public void onDestroy() {
        RxUtils.safeUnsubscribe(newNowPlayingUpdate);
        RxUtils.safeUnsubscribe(newVotableUpdate);
        RxUtils.safeUnsubscribe(newPlayRequest);
    }

    public void updatePartyParameters() {
        view.updatePartyCode(model.getPartyCode());
        view.updatePartyName(model.getPartyName());
    }

    public void updateCurrentPlayingTrack() {
        // Update the Now Playing Artist and Song with the last received PlayRequest
        updateNowPlaying(model.getCurrentPlayingTrack());
        updateVotableSongs();
    }

    public void updateVotableSongs() {
        Observable.just(null)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<Object, List<String>>() {
                    @Override
                    public List<String> call(Object ignored) {
                        return getVotableTrackStrings();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> votableSongs) {
                        view.updateVotableSongs(votableSongs);
                    }
                });
    }

    private void updateNowPlaying(Metadata.Track track) {
        if (track != null) {
            if (track.artistName != null) {
                view.updateArtist(track.artistName);
            }

            if (track.name != null) {
                view.updateSong(track.name);
            }
        }
    }
}

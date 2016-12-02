package com.musicocracy.fpgk.mvp.presenter;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.net.ProtoMessageBySender;
import com.musicocracy.fpgk.domain.util.RxUtils;
import com.musicocracy.fpgk.mvp.model.NowPlayingModel;
import com.musicocracy.fpgk.mvp.view.NowPlayingView;
import com.musicocracy.fpgk.net.proto.MessageType;
import com.musicocracy.fpgk.net.proto.PlayRequestRequest;
import com.spotify.sdk.android.player.Metadata;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class NowPlayingPresenter implements Presenter<NowPlayingView> {
    private final NowPlayingModel model;
    private final Subscription newPlayRequest;
    private NowPlayingView view;

    public NowPlayingPresenter(NowPlayingModel model) {
        this.model = model;
        newPlayRequest = createPlayRequestSub();
    }

    @Override
    public void setView(NowPlayingView view) {
        this.view = view;
    }

    public Subscription createPlayRequestSub() {
        return model.getServerHandler().newSongPlaying()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Metadata.Track>() {
                    @Override
                    public void call(Metadata.Track track) {
                        updateNowPlaying(track);
                    }
                });
    }

    public void onDestroy() {
        RxUtils.safeUnsubscribe(newPlayRequest);
    }

    public void updatePartyParameters() {
        view.updatePartyCode(model.getPartyCode());
        view.updatePartyName(model.getPartyName());
    }

    public void updateCurrentPlayingTrack() {
        // Update the Now Playing Artist and Song with the last received PlayRequest
        updateNowPlaying(model.getCurrentPlayingTrack());
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

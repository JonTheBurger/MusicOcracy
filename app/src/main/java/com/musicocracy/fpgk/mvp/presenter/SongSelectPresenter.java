package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.util.RxUtils;
import com.musicocracy.fpgk.mvp.model.SongSelectModel;
import com.musicocracy.fpgk.mvp.view.SongSelectView;
import com.musicocracy.fpgk.net.proto.BrowseSongsReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsRequest;
import com.musicocracy.fpgk.net.proto.PlayRequestRequest;
import com.musicocracy.fpgk.net.proto.SendVoteRequest;
import com.musicocracy.fpgk.net.proto.VotableSongsReply;
import com.musicocracy.fpgk.net.proto.VotableSongsRequest;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SongSelectPresenter implements Presenter<SongSelectView> {
    private final SongSelectModel model;
    private final Subscription browseSubscription;
    private final Subscription voteSubscription;
    private final String uniqueAndroidId;
    private SongSelectView view;
    private BrowseSongsReply currentBrowseReply;
    private VotableSongsReply currentVotableReply;

    public SongSelectPresenter(SongSelectModel model, String uniqueAndroidId) {
        this.model = model;
        browseSubscription = model.getBrowseReply()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BrowseSongsReply>() {
                    @Override
                    public void call(BrowseSongsReply BrowseSongsReply) {
                        onBrowseResultsReceived(BrowseSongsReply);
                    }
                });

        voteSubscription = model.getVotableSongsReply()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VotableSongsReply>() {
                    @Override
                    public void call(VotableSongsReply VotableSongsReply) {
                        onVotableSongsReceived(VotableSongsReply);
                    }
                });
        this.uniqueAndroidId = uniqueAndroidId;
    }

    public void populateBrowseSongs(String requestString) {
        BrowseSongsRequest msg = BrowseSongsRequest.newBuilder()
                .setSongTitle(requestString)
                .build();

        model.sendBrowseMsg(msg);
    }

    public void onBrowseResultsReceived(BrowseSongsReply msg) {
        currentBrowseReply = msg;

        ArrayList<String> browseList = new ArrayList<>();
        for (int i = 0; i < msg.getSongsCount(); i++) {
            browseList.add(Integer.toString(i + 1) + ") Title: " + msg.getSongs(i).getTitle()
                + "\nArtist: " + msg.getSongs(i).getArtist());
        }

        view.updateBrowseSongs(browseList);
    }

    public void populateVoteSongs() {
        VotableSongsRequest msg = VotableSongsRequest.newBuilder().build();

        model.sendVotableSongsRequestMsg(msg);
    }

    public void onVotableSongsReceived(VotableSongsReply msg) {
        currentVotableReply = msg;

        ArrayList<String> voteList = new ArrayList<>();
        for (int i = 0; i < msg.getSongsCount(); i++) {
            voteList.add(Integer.toString(i + 1) + ") Title: " + msg.getSongs(i).getTitle()
                    + "\nArtist: " + msg.getSongs(i).getArtist());
        }
        view.updateVotableSongs(voteList);
    }

    public void playRequest(int songId) {
        BrowseSongsReply.BrowsableSong song = currentBrowseReply.getSongs(songId);
        PlayRequestRequest msg = PlayRequestRequest.newBuilder()
                .setRequesterId(uniqueAndroidId)
                .setMusicService(song.getMusicService())
                .setUri(song.getUri())
                .build();

        model.sendPlayRequest(msg);
    }

    public void voteRequest(int songId) {
        VotableSongsReply.VotableSong song = currentVotableReply.getSongs(songId);
        SendVoteRequest msg = SendVoteRequest.newBuilder()
                .setChoiceId(song.getChoiceId())
                .build();

        model.sendVoteRequest(msg);
    }

    public void onDestroy() {
        RxUtils.safeUnsubscribe(browseSubscription);
    }

    @Override
    public void setView(SongSelectView view) {
        this.view = view;
    }
}

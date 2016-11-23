package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.mvp.model.SongSelectModel;
import com.musicocracy.fpgk.mvp.view.SongSelectView;
import com.musicocracy.fpgk.net.proto.BrowseSongsAckMsg;
import com.musicocracy.fpgk.net.proto.BrowseSongsMsg;

import java.util.ArrayList;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SongSelectPresenter implements Presenter<SongSelectView> {
    private final SongSelectModel model;
    private final Subscription browseSubscription;
    private SongSelectView view;

    public SongSelectPresenter(SongSelectModel model) {
        this.model = model;
        browseSubscription = model.getBrowseResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BrowseSongsAckMsg>() {
            @Override
            public void call(BrowseSongsAckMsg browseSongsAckMsg) {
                onBrowseResultsReceived(browseSongsAckMsg);
            }
        });
    }

    public void populateBrowseSongs(String requestString) {
        BrowseSongsMsg msg = BrowseSongsMsg.newBuilder()
                .setArtist("")
                .setSongTitle(requestString)
                .build();

        model.sendBrowseMsg(msg);
    }

    public void onBrowseResultsReceived(BrowseSongsAckMsg msg) {
        ArrayList<String> testList = new ArrayList<>();
        for (int i = 0; i < msg.getSongsCount(); i++) {
            testList.add(msg.getSongs(i).toString());
        }

        view.updateSongs(testList);
    }

    public void populateVoteSongs() {
        ArrayList<String> testList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testList.add("vote song " + i);
        }
        view.updateSongs(testList);
    }

    public void onDestroy() {
        if (browseSubscription != null && !browseSubscription.isUnsubscribed()) {
            browseSubscription.unsubscribe();
        }
    }

    @Override
    public void setView(SongSelectView view) {
        this.view = view;
    }
}

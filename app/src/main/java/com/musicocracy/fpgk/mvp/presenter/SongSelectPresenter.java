package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.RxUtils;
import com.musicocracy.fpgk.mvp.model.SongSelectModel;
import com.musicocracy.fpgk.mvp.view.SongSelectView;
import com.musicocracy.fpgk.net.proto.BasicReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsReply;
import com.musicocracy.fpgk.net.proto.BrowseSongsRequest;
import com.musicocracy.fpgk.net.proto.PlayRequestRequest;
import com.musicocracy.fpgk.net.proto.SendVoteRequest;
import com.musicocracy.fpgk.net.proto.VotableSongsReply;
import com.musicocracy.fpgk.net.proto.VotableSongsRequest;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SongSelectPresenter implements Presenter<SongSelectView> {
    private static final String TAG = "SongSelectPresenter";
    private final SongSelectModel model;
    private final Logger log;
    private final String uniqueAndroidId;
    private final Subscription browseSubscription;
    private final Subscription voteSubscription;
    private final Subscription playRequestSub;
    private final Subscription voteRequestSub;
    private SongSelectView view;
    private BrowseSongsReply currentBrowseReply;
    private VotableSongsReply currentVotableReply;

    private int browseRetries = 0;
    private static final int MAX_BROWSE_RETRY = 2;
    private String browseRequest = "";

    public SongSelectPresenter(SongSelectModel model, final Logger log, String uniqueAndroidId) {
        this.model = model;
        this.log = log;
        this.uniqueAndroidId = uniqueAndroidId;

        browseSubscription = model.getBrowseReply()
                .map(new Func1<BrowseSongsReply, List<String>>() {
                    @Override
                    public List<String> call(BrowseSongsReply reply) {
                        currentBrowseReply = reply;
                        List<String> browseList = new ArrayList<>();
                        if (reply != BrowseSongsReply.getDefaultInstance()) {
                            for (int i = 0; i < reply.getSongsCount(); i++) {
                                browseList.add(Integer.toString(i + 1) + ") Title: " + reply.getSongs(i).getTitle()
                                        + "\nArtist: " + reply.getSongs(i).getArtist());
                            }
                            browseRetries = 0;
                        } else if (browseRetries < MAX_BROWSE_RETRY) {
                            browseRetries++;
                            populateBrowseSongs(browseRequest);
                        }
                        return browseList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                        log.warning(TAG, "Unexpected browseSubscription: onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        log.error(TAG, "Unexpected browseSubscription: onError " + e.toString());
                    }

                    @Override
                    public void onNext(List<String> browseList) {
                       view.updateBrowseSongs(browseList);
                    }
                });

        voteSubscription = model.getVotableSongsReply()
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<VotableSongsReply>() {
                               @Override
                               public void onCompleted() {
                                   log.warning(TAG, "Unexpected voteSubscription: onCompleted");
                               }
                               @Override
                               public void onError(Throwable e) {
                                   log.error(TAG, "Unexpected voteSubscription: onError " + e.toString());
                               }

                               @Override
                               public void onNext(VotableSongsReply votableSongsReply) {
                                   onVotableSongsReceived(votableSongsReply);
                               }
                           });

        playRequestSub = model.getPlayRequestReply()
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BasicReply>() {
                    @Override
                    public void onCompleted() {
                        log.warning(TAG, "Unexpected playRequestSub: onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        log.error(TAG, "Unexpected playRequestSub: onError " + e.toString());
                    }

                    @Override
                    public void onNext(BasicReply basicReply) {
                       if (basicReply != BasicReply.getDefaultInstance() && basicReply.getSuccess()) {
                           view.onPlayRequestSuccess();
                       } else {
                           view.onPlayRequestError(basicReply.getMessage());
                       }
                    }
                });

        voteRequestSub = model.getVoteRequestReply()
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BasicReply>() {
                    @Override
                    public void onCompleted() {
                        log.warning(TAG, "Unexpected voteRequestSub: onCompleted");
                    }
                    @Override
                    public void onError(Throwable e) {
                        log.error(TAG, "Unexpected voteRequestSub: onError " + e.toString());
                    }

                    @Override
                    public void onNext(BasicReply basicReply) {
                        if (basicReply != BasicReply.getDefaultInstance() && basicReply.getSuccess()) {
                            view.onVoteRequestSuccess();
                        } else {
                            view.onVoteRequestError(basicReply.getMessage());
                        }
                    }
                });
    }

    public void populateBrowseSongs(String requestString) {
        browseRequest = requestString;
        BrowseSongsRequest msg = BrowseSongsRequest.newBuilder()
                .setSongTitle(requestString)
                .build();

        model.sendBrowseMsg(msg);
    }

    public void populateVoteSongs() {
        VotableSongsRequest msg = VotableSongsRequest.newBuilder().build();

        model.sendVotableSongsRequestMsg(msg);
    }

    private void onVotableSongsReceived(VotableSongsReply msg) {
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
                .setRequesterId(uniqueAndroidId)
                .setChoiceId(song.getChoiceId())
                .build();

        model.sendVoteRequest(msg);
    }

    public void onDestroy() {
        RxUtils.safeUnsubscribe(browseSubscription);
        RxUtils.safeUnsubscribe(voteSubscription);
        RxUtils.safeUnsubscribe(playRequestSub);
        RxUtils.safeUnsubscribe(voteRequestSub);
    }

    @Override
    public void setView(SongSelectView view) {
        this.view = view;
    }
}

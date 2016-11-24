package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.net.NetworkUtils;
import com.musicocracy.fpgk.mvp.model.ConnectModel;
import com.musicocracy.fpgk.mvp.view.ConnectView;
import com.musicocracy.fpgk.net.proto.BasicReply;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ConnectPresenter implements Presenter<ConnectView> {
    private final ConnectModel model;
    private final Subscription joinSubscription;
    private ConnectView view;

    public ConnectPresenter(ConnectModel model) {
        this.model = model;
        joinSubscription = model.getJoinResultObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BasicReply>() {
            @Override
            public void call(BasicReply basicReply) {
                if (basicReply.getSuccess()) {
                    view.onJoinSuccess();
                } else {
                    view.onJoinError(basicReply.getMessage());
                    ConnectPresenter.this.leaveParty();
                }
            }
        });
    }

    public void joinParty() {
        try {
            String ip = NetworkUtils.base36ToIpAddress(view.getPartyCode().toLowerCase());
            model.connect(ip);
            model.joinParty(view.getPartyName(), view.getPartyCode());
        } catch (NumberFormatException nfe) {
            view.onJoinError("Could not find host");
            leaveParty();
        } catch (UnsupportedOperationException uoe) {
            view.onJoinError("Could not establish connection");
            leaveParty();
        }
    }

    public void onDestroy() {
        if (joinSubscription != null && !joinSubscription.isUnsubscribed()) {
            joinSubscription.unsubscribe();
        }
    }

    @Override
    public void setView(ConnectView view) {
        this.view = view;
    }

    public void leaveParty() {
        model.stopClient();
    }
}

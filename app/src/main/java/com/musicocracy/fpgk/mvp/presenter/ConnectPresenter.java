package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.net.NetworkUtils;
import com.musicocracy.fpgk.domain.util.RxUtils;
import com.musicocracy.fpgk.mvp.model.ConnectModel;
import com.musicocracy.fpgk.mvp.view.ConnectView;
import com.musicocracy.fpgk.net.proto.BasicReply;

import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class ConnectPresenter implements Presenter<ConnectView> {
    private final ConnectModel model;
    private final Subscription joinSubscription;
    private ConnectView view;

    public ConnectPresenter(ConnectModel model) {
        this.model = model;
        joinSubscription = model.getJoinResultObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    view.onJoinError(throwable.getMessage());
                }
            })
            .subscribe(new Action1<BasicReply>() {
                @Override
                public void call(BasicReply reply) {
                    if (reply != BasicReply.getDefaultInstance() && reply.getSuccess()) {
                        view.onJoinSuccess();
                    } else {
                        view.onJoinError(reply.getMessage());
                    }
                }
            });
    }

    public void joinParty() {
        try {
            String ip = NetworkUtils.base36ToIpAddress(view.getPartyCode().toLowerCase());
            model.connect(ip);
            model.joinParty(view.getPartyName());
        } catch (UnsupportedOperationException e) {
            view.onJoinError(e.getMessage());
        }
    }

    public void onDestroy() {
        RxUtils.safeUnsubscribe(joinSubscription);
    }

    @Override
    public void setView(ConnectView view) {
        this.view = view;
    }

    public void leaveParty() {
        model.stopClient();
    }
}

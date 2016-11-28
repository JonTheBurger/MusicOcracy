package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.net.NetworkUtils;
import com.musicocracy.fpgk.mvp.model.ConnectModel;
import com.musicocracy.fpgk.mvp.view.ConnectView;
import com.musicocracy.fpgk.net.proto.BasicReply;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class ConnectPresenter implements Presenter<ConnectView> {
    private final ConnectModel model;
    private final Observable<BasicReply> joinObservable;
    private Subscription joinSubscription = null;
    private ConnectView view;

    public ConnectPresenter(ConnectModel model) {
        this.model = model;
        this.joinObservable = Observable.defer(new Func0<Observable<BasicReply>>() {
            @Override
            public Observable<BasicReply> call() {
                String ip = NetworkUtils.base36ToIpAddress(view.getPartyCode().toLowerCase());
                ConnectPresenter.this.model.connect(ip);
                ConnectPresenter.this.model.joinParty(view.getPartyName(), view.getPartyCode());
                return ConnectPresenter.this.model.getJoinResultObservable();
            }
        });
    }

    public void joinParty() {
        joinSubscription = joinObservable
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
                    public void call(BasicReply basicReply) {
                        if (basicReply.getSuccess()) {
                            view.onJoinSuccess();
                        } else {
                            view.onJoinError(basicReply.getMessage());
                        }
                    }
                });
    }

    public void onDestroy() {
        if (joinSubscription != null && !joinSubscription.isUnsubscribed()) {
            joinSubscription.unsubscribe();
            joinSubscription = null;
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

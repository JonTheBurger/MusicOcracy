package com.musicocracy.fpgk.mvp.presenter;

import android.content.Context;

import com.musicocracy.fpgk.mvp.model.PartyConfigModel;
import com.musicocracy.fpgk.domain.net.NetworkUtils;
import com.musicocracy.fpgk.mvp.view.PartyConfigView;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class PartyConfigPresenter implements Presenter<PartyConfigView> {
    private final PartyConfigModel model;
    private PartyConfigView view;
    private Subscription globalIpSub;

    public PartyConfigPresenter(PartyConfigModel model) {
        this.model = model;
    }

    public void onCreate() {
        globalIpSub = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(NetworkUtils.getPublicIpAddress());
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<String>() {
            @Override
            public void call(String address) {
                view.setPartyCode(NetworkUtils.ipAddressToBase36(address).toUpperCase());
            }
        });
    }

    public void confirmSettings() {
        model.getSettings().setPartyCode(view.getPartyCode());
        model.getSettings().setPartyName(view.getPartyName());
        model.getSettings().setTokens(view.getTokenCount());
        long refillMillis = (view.getTokenRefillMinutes() * 60 + view.getTokenRefillSeconds()) * 1000;
        model.getSettings().setTokenRefillMillis(refillMillis);

        model.startServer();
    }

    public void onBack() throws InterruptedException {
        model.stopServer();
    }

    public void onDestroy() {
        if (globalIpSub != null && !globalIpSub.isUnsubscribed()) {
            globalIpSub.unsubscribe();
        }
    }

    @Override
    public void setView(PartyConfigView view) {
        this.view = view;
    }
}

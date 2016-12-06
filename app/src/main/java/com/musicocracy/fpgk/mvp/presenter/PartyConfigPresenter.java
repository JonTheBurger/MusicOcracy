package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.RxUtils;
import com.musicocracy.fpgk.mvp.model.PartyConfigModel;
import com.musicocracy.fpgk.domain.net.NetworkUtils;
import com.musicocracy.fpgk.mvp.view.PartyConfigView;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class PartyConfigPresenter implements Presenter<PartyConfigView> {
    private static final String TAG = "PartyConfigPresenter";
    private final PartyConfigModel model;
    private final Logger log;
    private PartyConfigView view;
    private Subscription globalIpSub;

    public PartyConfigPresenter(PartyConfigModel model, Logger log) {
        this.model = model;
        this.log = log;
    }

    public void onCreate() {
        globalIpSub = Observable.defer(new Func0<Observable<String>>() {    // TODO: Is defer needed here?
            @Override
            public Observable<String> call() {
                return Observable.just(NetworkUtils.getPublicIpAddress());
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                log.warning(TAG, "Unexpected createClientConnectSub: onCompleted");
            }
            @Override
            public void onError(Throwable e) {
                log.error(TAG, "Unexpected createClientConnectSub: onError " + e.toString());
            }

            @Override
            public void onNext(String address) {
                view.setPartyCode(NetworkUtils.ipAddressToBase36(address).toUpperCase());
            }
        });
    }

    public void confirmSettings() {
        model.getSettings().setPartyCode(view.getPartyCode());
        model.getSettings().setPartyName(view.getPartyName());
        model.getSettings().setCoinAllowance(view.getTokenCount());
        long refillMillis = (view.getTokenRefillMinutes() * 60 + view.getTokenRefillSeconds()) * 1000;
        model.getSettings().setCoinRefillMillis(refillMillis);

        model.startServer();
    }

    public void onBack() throws InterruptedException {
        model.stopServer();
    }

    public void onDestroy() {
        RxUtils.safeUnsubscribe(globalIpSub);
    }

    @Override
    public void setView(PartyConfigView view) {
        this.view = view;
    }
}

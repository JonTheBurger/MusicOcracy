package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.net.NetworkUtils;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.RxUtils;
import com.musicocracy.fpgk.mvp.model.ConnectModel;
import com.musicocracy.fpgk.mvp.view.ConnectView;
import com.musicocracy.fpgk.net.proto.BasicReply;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ConnectPresenter implements Presenter<ConnectView> {
    private static final String TAG = "ConnectPresenter";
    private final ConnectModel model;
    private final Logger log;
    private ConnectView view;
    private Subscription joinSubscription;

    public ConnectPresenter(ConnectModel model, Logger log) {
        this.model = model;
        this.log = log;
    }

    public void onResume() {
        joinSubscription = createJoinSubscription();
    }

    private Subscription createJoinSubscription() {
        return model.getJoinResultObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BasicReply>() {
                    @Override
                    public void onCompleted() {
                        log.warning(TAG, "Client join Observable in unexpected state: onCompleted");
                        joinSubscription = createJoinSubscription();
                    }

                    @Override
                    public void onError(Throwable e) {
                        log.warning(TAG, "Client join Observable in unexpected state: onError " + e.toString());
                        view.showJoinError(e.getMessage());
                        joinSubscription = createJoinSubscription();
                    }

                    @Override
                    public void onNext(BasicReply reply) {
                        if (reply.getSuccess()) {
                            view.showJoinSuccess();
                        } else {
                            view.showJoinError(reply.getMessage());
                        }
                    }
                });
    }

    public void joinParty() {
        try {
            String ip = NetworkUtils.base36ToIpAddress(view.getPartyCode().toLowerCase());
            model.connect(ip);
            model.joinParty(view.getPartyName());

            Observable.timer(1500, TimeUnit.MILLISECONDS)   // RxNetty drops packets sometimes, so we retry once after 1500ms. A repeat message won't hurt, 1) because it doesn't change client/server state & 2) our joinSubscription will be unsubscribed at this time.
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            model.joinParty(view.getPartyName());
                        }
                    });
        } catch (Exception e) {
            view.showJoinError(e.getMessage());
        }
    }

    public void onPause() {
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

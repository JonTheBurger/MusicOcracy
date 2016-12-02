package com.musicocracy.fpgk.domain.net;

import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.RxUtils;

import rx.Subscription;
import rx.functions.Action1;

public class ClientHandler {
    private static final Subscription[] EMPTY_SUBS = new Subscription[0];
    private static final String TAG = "ClientHandler";
    private final ClientEventBus eventBus;
    private final Logger log;
    private Subscription[] subscriptions = EMPTY_SUBS;

    public ClientHandler(ClientEventBus eventBus, Logger log) {
        this.eventBus = eventBus;
        this.log = log;
    }

    public void onCreate() {
        if (subscriptions == EMPTY_SUBS) {
            subscriptions = new Subscription[] {
                    createLogSub(),
            };
        }
    }

    private Subscription createLogSub() {
        return eventBus.getObservableLog()
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log.verbose(TAG, s);
                    }
                });
    }

    public void onDestroy() {
        for (Subscription subscription : subscriptions) {
            RxUtils.safeUnsubscribe(subscription);
        }
        subscriptions = EMPTY_SUBS;
    }
}

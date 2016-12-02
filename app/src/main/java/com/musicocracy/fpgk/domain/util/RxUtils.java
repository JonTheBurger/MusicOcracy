package com.musicocracy.fpgk.domain.util;

import rx.Subscription;

public class RxUtils {
    public static void safeUnsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}

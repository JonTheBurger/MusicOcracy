package com.musicocracy.fpgk.model.net;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class WritableEventStream<T> {
    private final Subject<T, T> subject = PublishSubject.create();
    private final Observable<T> stream = subject.share();

    public void broadcast(T message) {
        subject.onNext(message);
    }

    public Observable<T> getObservable() {
        return stream;
    }
}

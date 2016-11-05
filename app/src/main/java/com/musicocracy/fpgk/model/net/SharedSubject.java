package com.musicocracy.fpgk.model.net;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class SharedSubject<T> extends Subject<T, T> {
    private final Subject<T, T> subject;
    private final Observable<T> stream;
    private T last;

    public static <T> SharedSubject<T> create() {
        final Subject subject = PublishSubject.create();

        OnSubscribe<T> onSubscribe = new OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subject.subscribe(subscriber);
            }
        };

        return new SharedSubject<>(onSubscribe, subject);
    }

    protected SharedSubject(OnSubscribe<T> onSubscribe, Subject<T, T> subject) {
        super(onSubscribe);
        this.subject = subject;
        this.stream = subject.asObservable().share();
    }

    public void onNext(T message) {
        last = message;
        subject.onNext(message);
    }

    public void onError(Throwable t) {
        subject.onError(t);
    }

    public void onCompleted() {
        subject.onCompleted();
    }

    public Observable<T> getObservable() {
        return stream;
    }

    @Override
    public boolean hasObservers() {
        return subject.hasObservers();
    }

    public T getLast() {
        return last;
    }
}

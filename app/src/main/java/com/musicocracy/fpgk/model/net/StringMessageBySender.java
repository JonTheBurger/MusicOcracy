package com.musicocracy.fpgk.model.net;

import io.reactivex.netty.channel.ObservableConnection;

public class StringMessageBySender {
    public final String message;
    public final ObservableConnection<String, String> sender;

    public StringMessageBySender(String message, ObservableConnection<String, String> sender) {
        this.message = message;
        this.sender = sender;
    }
}

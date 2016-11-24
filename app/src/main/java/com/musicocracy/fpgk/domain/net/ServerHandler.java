package com.musicocracy.fpgk.domain.net;

import com.musicocracy.fpgk.net.proto.BasicReply;
import com.musicocracy.fpgk.net.proto.MessageType;

import rx.Subscription;
import rx.functions.Action1;

public class ServerHandler {
    private final ServerEventBus eventBus;
    private Subscription clientConnectSub;

    public ServerHandler(ServerEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void onCreate() {
        clientConnectSub = createClientConnectSub();
    }

    private Subscription createClientConnectSub() {
        return eventBus.getObservable(MessageType.CONNECT_REQUEST)
                .subscribe(new Action1<ProtoMessageBySender>() {
                    @Override
                    public void call(ProtoMessageBySender msgBySender) {
                        BasicReply reply = BasicReply.newBuilder()
                                .setSuccess(true)
                                .setMessage("")
                                .setReplyingTo(MessageType.CONNECT_REQUEST)
                                .build();
                        msgBySender.replyWith(reply);
                    }
                });
    }

    public void onDestroy() {
        safeUnsubscribe(clientConnectSub);
    }

    private static void safeUnsubscribe(Subscription sub) {
        if (sub != null && !sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }
}

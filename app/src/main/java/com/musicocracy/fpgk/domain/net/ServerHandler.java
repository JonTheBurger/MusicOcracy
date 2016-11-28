package com.musicocracy.fpgk.domain.net;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.net.proto.BasicReply;
import com.musicocracy.fpgk.net.proto.BasicReplyOrBuilder;
import com.musicocracy.fpgk.net.proto.ConnectRequest;
import com.musicocracy.fpgk.net.proto.MessageType;

import rx.Subscription;
import rx.functions.Action1;

public class ServerHandler {
    private final ServerEventBus eventBus;
    private final PartySettings partySettings;
    private Subscription clientConnectSub;

    public ServerHandler(ServerEventBus eventBus, PartySettings partySettings) {
        this.eventBus = eventBus;
        this.partySettings = partySettings;
    }

    public void onCreate() {
        clientConnectSub = createClientConnectSub();
    }

    private Subscription createClientConnectSub() {
        return eventBus.getObservable(MessageType.CONNECT_REQUEST)
                .subscribe(new Action1<ProtoMessageBySender>() {
                    @Override
                    public void call(ProtoMessageBySender msgBySender) {
                        ConnectRequest request;
                        try {
                            request = ConnectRequest.parseFrom(msgBySender.message.getBody());
                        } catch (InvalidProtocolBufferException e) {
                            request = ConnectRequest.getDefaultInstance();
                            e.printStackTrace();
                        }

                        BasicReply reply;
                        if (request != ConnectRequest.getDefaultInstance() &&
                                request.getPartyName().equals(partySettings.getPartyName())) {
                            reply = BasicReply.newBuilder()
                                .setSuccess(true)
                                .setMessage("")
                                .setReplyingTo(msgBySender.message.getHeader().getType())
                                .build();
                        } else {
                            reply = BasicReply.newBuilder()
                                .setSuccess(false)
                                .setMessage("Invalid party credentials")
                                .setReplyingTo(msgBySender.message.getHeader().getType())
                                .build();
                        }
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

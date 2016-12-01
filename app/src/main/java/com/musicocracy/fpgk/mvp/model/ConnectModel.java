package com.musicocracy.fpgk.mvp.model;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.net.proto.BasicReply;
import com.musicocracy.fpgk.net.proto.ConnectRequest;
import com.musicocracy.fpgk.net.proto.Envelope;
import com.musicocracy.fpgk.net.proto.MessageType;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rx.Observable;
import rx.functions.Func1;

public class ConnectModel {
    private final ClientEventBus client;
    private final String uniqueAndroidId;
    private final int defaultPort;

    public ConnectModel(ClientEventBus client, String uniqueAndroidId, int defaultPort) {
        this.client = client;
        this.uniqueAndroidId = uniqueAndroidId;
        this.defaultPort = defaultPort;
    }

    public void connect(String host) throws UnsupportedOperationException {
        client.start(host, defaultPort);
        client.awaitNextIsRunningChanged(2500, TimeUnit.MILLISECONDS);
        if (!client.isRunning()) {
            throw new UnsupportedOperationException("Could not connect to host");
        }
    }

    public Observable<Boolean> getIsRunningObservable() {
        return client.getIsRunningObservable();
    }

    public void joinParty(String partyName, String partyCode) {
        ConnectRequest message = ConnectRequest.newBuilder()
                .setRequesterId(uniqueAndroidId)
                .setPartyName(partyName)
                .build();
        client.send(message);
    }

    public Observable<BasicReply> getJoinResultObservable() {
        return client.getObservable(MessageType.BASIC_REPLY)
                .map(new Func1<Envelope, BasicReply>() {
                    @Override
                    public BasicReply call(Envelope envelope) {
                        try {
                            return BasicReply.parseFrom(envelope.getBody());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                            return BasicReply.getDefaultInstance();
                        }
                    }
                })
                .filter(new Func1<BasicReply, Boolean>() {
                    @Override
                    public Boolean call(BasicReply basicReply) {
                        return basicReply.getReplyingTo() == MessageType.CONNECT_REQUEST;
                    }
                });
    }

    public void stopClient() {
        client.stop();
    }
}

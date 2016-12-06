package com.musicocracy.fpgk.mvp.model;

import com.google.protobuf.InvalidProtocolBufferException;
import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.net.ClientHandler;
import com.musicocracy.fpgk.net.proto.BasicReply;
import com.musicocracy.fpgk.net.proto.ConnectRequest;
import com.musicocracy.fpgk.net.proto.Envelope;
import com.musicocracy.fpgk.net.proto.MessageType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.functions.Func1;

public class ConnectModel {
    private final ClientEventBus client;
    private final ClientHandler clientHandler;
    private final String uniqueAndroidId;
    private final int defaultPort;

    public ConnectModel(ClientEventBus client, ClientHandler clientHandler, String uniqueAndroidId, int defaultPort) {
        this.client = client;
        this.clientHandler = clientHandler;
        this.uniqueAndroidId = uniqueAndroidId;
        this.defaultPort = defaultPort;
    }

    private final AtomicBoolean connectLock = new AtomicBoolean(false);
    public void connect(String host) throws UnsupportedOperationException, TimeoutException {
        if (!connectLock.getAndSet(true)) { // Client connection is extremely sensitive. We're going to ensure single entrance from button clicks here.
            try {
                if (client.isRunning()) {
                    client.stop();
                }
                client.start(host, defaultPort);

                if (!client.isRunning()) {  // If client hasn't connected yet, we'll wait for 1500ms.
                    boolean timeoutOccurred = client.awaitNextIsRunningChanged(1500, TimeUnit.MILLISECONDS);
                    if (timeoutOccurred) {
                        throw new TimeoutException("Connection timed out");
                    }

                    if (!client.isRunning()) {  // If we're still not running when a running status change has occurred, we have a networking error, e.g. we've been refused.
                        throw new UnsupportedOperationException("Could not connect to host");
                    }
                }

                clientHandler.onCreate();
            } finally {
                connectLock.set(false);
            }
        }
    }

    public void joinParty(String partyName) {
        final ConnectRequest message = ConnectRequest.newBuilder()
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
        clientHandler.onDestroy();
        client.stop();
    }
}

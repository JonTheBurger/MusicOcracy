package com.musicocracy.fpgk.mvp.model;

import com.google.protobuf.MessageLite;
import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.domain.net.ProtoMessageBySender;
import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.net.proto.EnvelopeMsg;
import com.musicocracy.fpgk.net.proto.MessageType;

import rx.Observable;

public class NetworkTestModel {
    private final ClientEventBus client;
    private final ServerEventBus server;
    private final ProtoEnvelopeFactory factory;

    public NetworkTestModel(ClientEventBus client, ServerEventBus server, ProtoEnvelopeFactory factory) {
        this.client = client;
        this.server = server;
        this.factory = factory;
    }

    public Observable<Boolean> getClientIsRunningObservable() {
        return client.getIsRunningObservable();
    }

    public Observable<Boolean> getServerIsRunningObservable() {
        return server.getIsRunningObservable();
    }

    public Observable<EnvelopeMsg> getClientReceiver() {
        Observable<EnvelopeMsg> votable = client.getObservable(MessageType.SEND_VOTABLE_SONGS);
        Observable<EnvelopeMsg> browse = client.getObservable(MessageType.BROWSE_SONGS_ACK);
        return Observable.merge(votable, browse);
    }

    public Observable<ProtoMessageBySender> getServerReceiver() {
        return server.getObservable(MessageType.BROWSE_SONGS);
    }

    public Observable<String> getClientLog() {
        return client.getObservableLog();
    }

    public Observable<String> getServerLog() {
        return server.getObservableLog();
    }

    public boolean isClientRunning() {
        return client.isRunning();
    }

    public boolean isServerRunning() {
        return server.isRunning();
    }

    public void startServer(int port) {
        server.start(port);
    }

    public void stopServer() throws InterruptedException {
        server.stop();
    }

    public void startClient(String host, int port) {
        client.start(host, port);
    }

    public void stopClient() {
        client.stop();
    }

    public void serverSend(MessageLite message) {
        server.sendToAll(factory.createEnvelopeFor(message));
    }

    public void clientSend(MessageLite message) {
        client.send(factory.createEnvelopeFor(message));
    }
}

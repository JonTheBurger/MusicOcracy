package com.musicocracy.fpgk.model;

import com.musicocracy.fpgk.model.net.MessageBySender;
import com.musicocracy.fpgk.model.net.TcpClientEventBus;
import com.musicocracy.fpgk.model.net.TcpServerEventBus;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class NetworkTestModel {
    private final TcpClientEventBus client = new TcpClientEventBus();
    private final TcpServerEventBus server = new TcpServerEventBus();

    public NetworkTestModel() {
        server.getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MessageBySender>() {
            @Override
            public void call(MessageBySender messageBySender) {
                messageBySender.sender.writeAndFlush("echo -> " + messageBySender.message + '\n');
            }
        });
    }

    public Observable getClientEventObservable() {
        return client.getClientLogObservable();
    }

    public Observable getServerEventObservable() {
        return server.getServerLogObservable();
    }

    public boolean isClientConnected() {
        return client.isClientConnected();
    }

    public boolean isServerRunning() {
        return server.isServerRunning();
    }

    public void startServer(int i) {
        server.startServer(i);
    }

    public void stopServer() throws InterruptedException {
        server.stopServer();
    }

    public void startClient(String host, int i) {
        client.startClient(host, i);
    }

    public void stopClient() {
        client.stopClient();
    }

    public void serverSend(String s) {
        server.serverSend(s);
    }

    public void clientSend(String s) {
        client.clientSend(s);
    }
}

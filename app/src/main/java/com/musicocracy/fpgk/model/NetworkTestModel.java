package com.musicocracy.fpgk.model;

import com.musicocracy.fpgk.model.net.StringMessageBySender;
import com.musicocracy.fpgk.model.net.RxTcpClient;
import com.musicocracy.fpgk.model.net.RxTcpServer;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class NetworkTestModel {
    private final RxTcpClient client = new RxTcpClient();
    private final RxTcpServer server = new RxTcpServer();

    public NetworkTestModel() {
        server.getObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<StringMessageBySender>() {
            @Override
            public void call(StringMessageBySender messageBySender) {
                messageBySender.sender.writeAndFlush("echo -> " + messageBySender.message + '\n');
            }
        });
    }

    public Observable<Boolean> getClientIsConnectedObservable() {
        return client.getIsRunningObservable();
    }

    public Observable<Boolean> getServerIsRunningObservable() {
        return server.getIsRunningObservable();
    }

    public Observable<String> getClientEventObservable() {
        return client.getObservableLog();
    }

    public Observable<String> getServerEventObservable() {
        return server.getObservableLog();
    }

    public boolean isClientConnected() {
        return client.isRunning();
    }

    public boolean isServerRunning() {
        return server.isRunning();
    }

    public void startServer(int i) {
        server.start(i);
    }

    public void stopServer() throws InterruptedException {
        server.stop();
    }

    public void startClient(String host, int i) {
        client.start(host, i);
    }

    public void stopClient() {
        client.stop();
    }

    public void serverSend(String s) {
        server.sendToAll(s);
    }

    public void clientSend(String s) {
        client.send(s);
    }
}

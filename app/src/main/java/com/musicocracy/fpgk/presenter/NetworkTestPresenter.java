package com.musicocracy.fpgk.presenter;

import com.musicocracy.fpgk.model.NetworkTestModel;
import com.musicocracy.fpgk.view.NetworkTestView;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class NetworkTestPresenter {
    private final NetworkTestView view;
    private final NetworkTestModel model;
    private Subscription clientSub;
    private Subscription serverSub;

    public NetworkTestPresenter(final NetworkTestView view, final NetworkTestModel model) {
        this.view = view;
        this.model = model;
        clientSub = model.getClientEventObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                view.logClientEvent(s);
            }
        });
        serverSub = model.getServerEventObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                view.logServerEvent(s);
            }
        });
    }

    public void serverToggle() throws InterruptedException {
        if (view.getServerToggle()) {
            model.startServer(Integer.parseInt(view.getPortText()));
        } else {
            model.stopServer();
        }
        view.setServerConnected(model.isServerRunning());
    }

    public void clientToggle() {
        if (view.getClientToggle()) {
            String host;
            if (view.getClientLocalToggle()) {
                host = "localhost";
            } else {
                host = view.getIpText();
            }

            model.startClient(host, Integer.parseInt(view.getPortText()));
        } else {
            model.stopClient();
        }
        view.setClientConnected(model.isClientConnected());
    }

    public void clientLocalToggle() {
        view.setClientLocal(view.getClientLocalToggle());
    }

    public void serverSend() {

    }

    public void clientSend() {

    }

    public void destroy() throws InterruptedException {
        clientSub.unsubscribe();
        serverSub.unsubscribe();
        model.stopClient();
        model.stopServer();
    }
}

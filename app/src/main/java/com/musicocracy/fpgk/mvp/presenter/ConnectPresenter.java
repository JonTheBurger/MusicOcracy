package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.net.IpUtils;
import com.musicocracy.fpgk.mvp.model.ConnectModel;
import com.musicocracy.fpgk.mvp.view.ConnectView;

public class ConnectPresenter implements Presenter<ConnectView> {
    private final ConnectModel model;
    private ConnectView view;

    public ConnectPresenter(ConnectModel model) {
        this.model = model;
    }

    public boolean startClient() {
        String ip = IpUtils.base36ToIpAddress(view.getPartyCode());
        model.connect(ip, IpUtils.DEFAULT_PORT);
        return model.isRunning();
    }

    public void onDestroy() {

    }

    @Override
    public void setView(ConnectView view) {
        this.view = view;
    }

    public void stopClient() {
        model.stopClient();
    }
}

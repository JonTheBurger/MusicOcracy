package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.mvp.model.ConnectModel;
import com.musicocracy.fpgk.mvp.view.ConnectView;

public class ConnectPresenter implements Presenter<ConnectView> {
    private final ConnectModel model;
    private ConnectView view;

    public ConnectPresenter(ConnectModel model) {
        this.model = model;
    }

    @Override
    public void setView(ConnectView view) {
        this.view = view;
    }
}

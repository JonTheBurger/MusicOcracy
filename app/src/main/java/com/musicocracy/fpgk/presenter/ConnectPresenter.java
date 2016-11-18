package com.musicocracy.fpgk.presenter;

import com.musicocracy.fpgk.model.ConnectModel;
import com.musicocracy.fpgk.view.ConnectView;

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

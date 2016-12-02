package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.mvp.model.RequestModel;
import com.musicocracy.fpgk.mvp.view.RequestView;

public class RequestPresenter implements Presenter<RequestView> {
    private final RequestModel model;
    private RequestView view;

    public RequestPresenter(RequestModel model) {
        this.model = model;
    }

    public void stopClient() {
        model.stopClient();
    }

    @Override
    public void setView(RequestView view) {
        this.view = view;
    }
}

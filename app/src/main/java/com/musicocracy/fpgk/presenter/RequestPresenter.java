package com.musicocracy.fpgk.presenter;

import com.musicocracy.fpgk.model.RequestModel;
import com.musicocracy.fpgk.view.RequestView;

public class RequestPresenter implements Presenter<RequestView> {
    private final RequestModel model;
    private RequestView view;

    public RequestPresenter(RequestModel model) {
        this.model = model;
    }

    @Override
    public void setView(RequestView view) {
        this.view = view;
    }
}

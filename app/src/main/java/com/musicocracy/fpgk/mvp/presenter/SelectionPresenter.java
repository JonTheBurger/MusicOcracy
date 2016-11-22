package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.mvp.model.SelectionModel;
import com.musicocracy.fpgk.mvp.view.SelectionView;

public class SelectionPresenter implements Presenter<SelectionView> {
    private final SelectionModel model;
    private SelectionView view;

    public SelectionPresenter(SelectionModel model) {
        this.model = model;
    }

    @Override
    public void setView(SelectionView view) {
        this.view = view;
    }
}

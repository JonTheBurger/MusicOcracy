package com.musicocracy.fpgk.presenter;

import com.musicocracy.fpgk.model.AddTermModel;
import com.musicocracy.fpgk.view.AddTermView;

public class AddTermPresenter implements Presenter<AddTermView> {
    private final AddTermModel model;
    private AddTermView view;

    public AddTermPresenter(AddTermModel model) {
        this.model = model;
    }

    @Override
    public void setView(AddTermView view) {
        this.view = view;
    }
}

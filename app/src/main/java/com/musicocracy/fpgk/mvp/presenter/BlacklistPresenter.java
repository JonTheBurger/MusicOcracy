package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.mvp.model.BlacklistModel;
import com.musicocracy.fpgk.mvp.view.BlacklistView;

public class BlacklistPresenter implements Presenter<BlacklistView> {
    private final BlacklistModel model;
    private BlacklistView view;

    public BlacklistPresenter(BlacklistModel model) {
        this.model = model;
    }

    @Override
    public void setView(BlacklistView view) {
        this.view = view;
    }
}

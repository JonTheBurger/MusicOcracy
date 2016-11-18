package com.musicocracy.fpgk.presenter;

import com.musicocracy.fpgk.model.BlacklistModel;
import com.musicocracy.fpgk.view.BlacklistView;

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

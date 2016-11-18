package com.musicocracy.fpgk.presenter;

import com.musicocracy.fpgk.model.SongSelectModel;
import com.musicocracy.fpgk.view.SongSelectView;

public class SongSelectPresenter implements Presenter<SongSelectView> {
    private final SongSelectModel model;
    private SongSelectView view;

    public SongSelectPresenter(SongSelectModel model) {
        this.model = model;
    }

    @Override
    public void setView(SongSelectView view) {
        this.view = view;
    }
}

package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.dal.SongFilter;
import com.musicocracy.fpgk.mvp.model.BlacklistModel;
import com.musicocracy.fpgk.mvp.view.BlacklistView;

import java.util.List;

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

    public List<SongFilter> getAllBlacklistedSongFilters() {
        return model.getAllBlacklistedSongFilters();
    }

    public List<String> getAllBlacklistedSongIds() {
        return model.getAllBlacklistedSongIds();
    }

}

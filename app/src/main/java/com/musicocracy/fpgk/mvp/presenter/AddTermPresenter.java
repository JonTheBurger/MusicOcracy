package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.mvp.model.AddTermModel;
import com.musicocracy.fpgk.mvp.view.AddTermView;

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

    public void addSongFilter(MusicService service, String songId, Party party, FilterMode filterMode) {
        model.addSongFilter(service, songId, party, filterMode);
    }
}

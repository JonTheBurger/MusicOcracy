package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.model.SongSelectModel;
import com.musicocracy.fpgk.presenter.SongSelectPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class SongSelectModule {
    @Provides
    public SongSelectModel provideSongSelectModel() {
        return new SongSelectModel();
    }

    @Provides
    public SongSelectPresenter provideSongSelectPresenter(SongSelectModel model) {
        return new SongSelectPresenter(model);
    }
}

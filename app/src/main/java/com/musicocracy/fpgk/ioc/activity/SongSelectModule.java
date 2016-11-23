package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.mvp.model.SongSelectModel;
import com.musicocracy.fpgk.mvp.presenter.SongSelectPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class SongSelectModule {
    @Provides
    public SongSelectModel provideSongSelectModel(Browser browser) {
        return new SongSelectModel(browser, api);
    }

    @Provides
    public SongSelectPresenter provideSongSelectPresenter(SongSelectModel model) {
        return new SongSelectPresenter(model);
    }
}

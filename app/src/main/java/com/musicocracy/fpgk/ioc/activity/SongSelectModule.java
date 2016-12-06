package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.ioc.ApplicationModule;
import com.musicocracy.fpgk.mvp.model.SongSelectModel;
import com.musicocracy.fpgk.mvp.presenter.SongSelectPresenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class SongSelectModule {
    @Provides
    public SongSelectModel provideSongSelectModel(ClientEventBus client, Logger log) {
        return new SongSelectModel(client, log);
    }

    @Provides
    public SongSelectPresenter provideSongSelectPresenter(SongSelectModel model, Logger log, @Named(ApplicationModule.UNIQUE_ANDROID_ID)String uniqueAndroidId) {
        return new SongSelectPresenter(model, log, uniqueAndroidId);
    }
}

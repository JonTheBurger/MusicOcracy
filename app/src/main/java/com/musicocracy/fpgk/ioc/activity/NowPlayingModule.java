package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.model.NowPlayingModel;
import com.musicocracy.fpgk.presenter.NowPlayingPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class NowPlayingModule {
    @Provides
    public NowPlayingModel provideNowPlayingModel() {
        return new NowPlayingModel();
    }

    @Provides
    public NowPlayingPresenter provideNowPlayingPresenter(NowPlayingModel model) {
        return new NowPlayingPresenter(model);
    }
}

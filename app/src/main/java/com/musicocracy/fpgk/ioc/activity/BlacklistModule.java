package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.mvp.model.BlacklistModel;
import com.musicocracy.fpgk.mvp.presenter.BlacklistPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class BlacklistModule {
    @Provides
    public BlacklistModel provideBlacklistModel() {
        return new BlacklistModel();
    }

    @Provides
    public BlacklistPresenter provideBlacklistPresenter(BlacklistModel model) {
        return new BlacklistPresenter(model);
    }
}

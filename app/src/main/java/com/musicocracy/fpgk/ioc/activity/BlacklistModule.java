package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;
import com.musicocracy.fpgk.mvp.model.BlacklistModel;
import com.musicocracy.fpgk.mvp.presenter.BlacklistPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class BlacklistModule {
    @Provides
    public BlacklistModel provideBlacklistModel(SongFilterRepository songFilterRepository) {
        return new BlacklistModel(songFilterRepository);
    }

    @Provides
    public BlacklistPresenter provideBlacklistPresenter(BlacklistModel model) {
        return new BlacklistPresenter(model);
    }
}

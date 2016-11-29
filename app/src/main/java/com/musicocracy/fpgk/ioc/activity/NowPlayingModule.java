package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.musicocracy.fpgk.mvp.model.NowPlayingModel;
import com.musicocracy.fpgk.mvp.presenter.NowPlayingPresenter;
import com.spotify.sdk.android.player.SpotifyPlayer;

import dagger.Module;
import dagger.Provides;

@Module
public class NowPlayingModule {
    @Provides
    public NowPlayingModel provideNowPlayingModel(ServerHandler serverHandler) {
        return new NowPlayingModel(serverHandler);
    }

    @Provides
    public NowPlayingPresenter provideNowPlayingPresenter(NowPlayingModel model) {
        return new NowPlayingPresenter(model);
    }
}

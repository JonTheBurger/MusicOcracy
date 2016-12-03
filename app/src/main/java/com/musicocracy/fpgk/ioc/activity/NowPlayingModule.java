package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.musicocracy.fpgk.domain.spotify.SpotifyPlayerHandler;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;
import com.musicocracy.fpgk.mvp.model.NowPlayingModel;
import com.musicocracy.fpgk.mvp.presenter.NowPlayingPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class NowPlayingModule {
    @Provides
    public NowPlayingModel provideNowPlayingModel(ServerHandler serverHandler,
                                                  ReadOnlyPartySettings partySettings,
                                                  SpotifyPlayerHandler playerHandler) {
        return new NowPlayingModel(serverHandler, partySettings, playerHandler);
    }

    @Provides
    public NowPlayingPresenter provideNowPlayingPresenter(NowPlayingModel model) {
        return new NowPlayingPresenter(model);
    }
}

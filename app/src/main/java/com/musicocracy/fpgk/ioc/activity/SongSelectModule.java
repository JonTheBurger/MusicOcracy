package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.mvp.model.SongSelectModel;
import com.musicocracy.fpgk.mvp.presenter.SongSelectPresenter;

import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyApi;

@Module
public class SongSelectModule {
    @Provides
    public SongSelectModel provideSongSelectModel(Browser browser, SpotifyApi api, ClientEventBus client) {
        return new SongSelectModel(browser, api, client);
    }

    @Provides
    public SongSelectPresenter provideSongSelectPresenter(SongSelectModel model) {
        return new SongSelectPresenter(model);
    }
}

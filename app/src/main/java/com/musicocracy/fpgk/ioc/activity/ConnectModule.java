package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.mvp.model.ConnectModel;
import com.musicocracy.fpgk.mvp.presenter.ConnectPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ConnectModule {
    @Provides
    public ConnectModel provideConnectModel(ClientEventBus client) {
        return new ConnectModel(client);
    }

    @Provides
    public ConnectPresenter provideConnectPresenter(ConnectModel model) {
        return new ConnectPresenter(model);
    }
}

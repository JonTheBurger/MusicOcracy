package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.net.ClientHandler;
import com.musicocracy.fpgk.ioc.ApplicationModule;
import com.musicocracy.fpgk.ioc.NetworkingModule;
import com.musicocracy.fpgk.mvp.model.ConnectModel;
import com.musicocracy.fpgk.mvp.presenter.ConnectPresenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ConnectModule {
    @Provides
    public ConnectModel provideConnectModel(ClientEventBus client, ClientHandler clientHandler, @Named(ApplicationModule.UNIQUE_ANDROID_ID) String uniqueAnroidId, @Named(NetworkingModule.DEFAULT_PORT) int defaultPort) {
        return new ConnectModel(client, clientHandler, uniqueAnroidId, defaultPort);
    }

    @Provides
    public ConnectPresenter provideConnectPresenter(ConnectModel model) {
        return new ConnectPresenter(model);
    }
}

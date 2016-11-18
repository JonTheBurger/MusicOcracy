package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.model.ConnectModel;
import com.musicocracy.fpgk.presenter.ConnectPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ConnectModule {
    @Provides
    public ConnectModel provideConnectModel() {
        return new ConnectModel();
    }

    @Provides
    public ConnectPresenter provideConnectPresenter(ConnectModel model) {
        return new ConnectPresenter(model);
    }
}

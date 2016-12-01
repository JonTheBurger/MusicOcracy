package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.mvp.model.RequestModel;
import com.musicocracy.fpgk.mvp.presenter.RequestPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class RequestModule {
    @Provides
    public RequestModel provideRequestModel(ClientEventBus client) {
        return new RequestModel(client);
    }

    @Provides
    public RequestPresenter provideRequestPresenter(RequestModel model) {
        return new RequestPresenter(model);
    }
}

package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.model.RequestModel;
import com.musicocracy.fpgk.presenter.RequestPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class RequestModule {
    @Provides
    public RequestModel provideRequestModel() {
        return new RequestModel();
    }

    @Provides
    public RequestPresenter provideRequestPresenter(RequestModel model) {
        return new RequestPresenter(model);
    }
}

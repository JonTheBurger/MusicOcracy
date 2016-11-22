package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.mvp.model.NetworkTestModel;
import com.musicocracy.fpgk.domain.net.ClientEventBus;
import com.musicocracy.fpgk.domain.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.mvp.presenter.NetworkTestPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkTestModule {
    @Provides
    public NetworkTestModel provideNetworkTestModel(ClientEventBus client, ServerEventBus server, ProtoEnvelopeFactory factory) {
        return new NetworkTestModel(client, server, factory);
    }

    @Provides
    public NetworkTestPresenter provideNetworkTestPresenter(NetworkTestModel model) {
        return new NetworkTestPresenter(model);
    }
}

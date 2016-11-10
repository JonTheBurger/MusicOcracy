package com.musicocracy.fpgk.ioc.fullstack;

import com.musicocracy.fpgk.model.NetworkTestModel;
import com.musicocracy.fpgk.model.net.ClientEventBus;
import com.musicocracy.fpgk.model.net.ProtoEnvelopeFactory;
import com.musicocracy.fpgk.model.net.ServerEventBus;
import com.musicocracy.fpgk.presenter.NetworkTestPresenter;

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

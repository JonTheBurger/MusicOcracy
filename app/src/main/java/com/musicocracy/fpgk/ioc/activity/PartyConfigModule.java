package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.domain.net.ServerHandler;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.ioc.NetworkingModule;
import com.musicocracy.fpgk.mvp.model.PartyConfigModel;
import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.mvp.presenter.PartyConfigPresenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class PartyConfigModule {
    @Provides
    public PartyConfigModel providePartyConfigModel(PartySettings settings, ServerEventBus server, ServerHandler handler, @Named(NetworkingModule.DEFAULT_PORT) int port) {
        return new PartyConfigModel(settings, server, handler, port);
    }

    @Provides
    public PartyConfigPresenter providePartyConfigPresenter(PartyConfigModel model, Logger log) {
        return new PartyConfigPresenter(model, log);
    }
}

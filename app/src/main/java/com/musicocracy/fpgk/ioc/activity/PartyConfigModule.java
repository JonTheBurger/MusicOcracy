package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.net.ServerEventBus;
import com.musicocracy.fpgk.mvp.model.PartyConfigModel;
import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.mvp.presenter.PartyConfigPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class PartyConfigModule {
    @Provides
    public PartyConfigModel providePartyConfigModel(PartySettings settings, ServerEventBus server) {
        return new PartyConfigModel(settings, server);
    }

    @Provides
    public PartyConfigPresenter providePartyConfigPresenter(PartyConfigModel model) {
        return new PartyConfigPresenter(model);
    }
}

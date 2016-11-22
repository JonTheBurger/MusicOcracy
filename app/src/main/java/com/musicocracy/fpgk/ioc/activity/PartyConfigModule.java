package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.model.PartyConfigModel;
import com.musicocracy.fpgk.model.util.PartySettings;
import com.musicocracy.fpgk.presenter.PartyConfigPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class PartyConfigModule {
    @Provides
    public PartyConfigModel providePartyConfigModel(PartySettings settings) {
        return new PartyConfigModel(settings);
    }

    @Provides
    public PartyConfigPresenter providePartyConfigPresenter(PartyConfigModel model) {
        return new PartyConfigPresenter(model);
    }
}

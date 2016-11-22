package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UtilityModule {
    @Provides
    @Singleton
    public PartySettings providePartySettings() {
        return new PartySettings();
    }

    @Provides
    @Singleton
    public ReadOnlyPartySettings provideReadOnlyPartySettings(PartySettings settings) {
        return settings;
    }
}

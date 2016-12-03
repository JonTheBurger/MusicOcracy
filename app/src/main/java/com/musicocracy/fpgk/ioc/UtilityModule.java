package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.util.AndroidLogger;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;
import com.musicocracy.fpgk.domain.spotify.SpotifyPlayerHandler;
import com.spotify.sdk.android.player.SpotifyPlayer;

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

    @Provides
    @Singleton
    public Logger provideLogger() {
        return new AndroidLogger();
    }

    @Provides
    @Singleton
    public SpotifyPlayerHandler provideSystemTimers(Logger log, SpotifyPlayer player) { return new SpotifyPlayerHandler(log, player);}
}

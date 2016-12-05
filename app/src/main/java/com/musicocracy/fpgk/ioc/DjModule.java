package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.util.PartySettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DjModule {
    @Provides
    @Singleton
    DjAlgorithm provideDjAlgorithm(Database database, PartySettings partySettings) {
        return new DjAlgorithm(database, partySettings);
    }
}

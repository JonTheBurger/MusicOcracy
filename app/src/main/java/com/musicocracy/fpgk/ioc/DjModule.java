package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;
import com.musicocracy.fpgk.domain.util.PartySettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DjModule {
    @Provides
    @Singleton
    DjAlgorithm provideDjAlgorithm(Database database, PlayRequestRepository playRequestRepository, SongFilterRepository songFilterRepository, PartySettings partySettings) {
        return new DjAlgorithm(database, playRequestRepository, songFilterRepository, partySettings);
    }
}

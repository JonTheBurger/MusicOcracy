package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DjModule {
    @Provides
    @Singleton
    DjAlgorithm provideDjAlgorithm(Database database, PlayRequestRepository playRequestRepository, SongFilterRepository songFilterRepository, ReadOnlyPartySettings partySettings, Browser browser) {
        return new DjAlgorithm(database, playRequestRepository, songFilterRepository, partySettings, browser);
    }
}

package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.PlayedVoteRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;

import java.sql.SQLException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DjModule {
    @Provides
    @Singleton
    DjAlgorithm provideDjAlgorithm(Database database, PlayRequestRepository playRequestRepository, PlayedVoteRepository playedVoteRepository, SongFilterRepository songFilterRepository, ReadOnlyPartySettings partySettings) {
        return new DjAlgorithm(database, playRequestRepository, playedVoteRepository, songFilterRepository, partySettings);
    }
}

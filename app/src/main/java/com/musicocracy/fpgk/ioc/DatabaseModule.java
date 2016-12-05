package com.musicocracy.fpgk.ioc;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.PlayedVoteRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    @Provides
    @Singleton
    public Database provideDatabase(Context context) {
        return OpenHelperManager.getHelper(context, Database.class);
    }

    @Provides
    @Singleton
    public PlayRequestRepository providePlayRequestRepository(Database database) {
        return new PlayRequestRepository(database);
    }

    @Provides
    @Singleton
    public SongFilterRepository provideSongRequestRepository(Database database) {
        return new SongFilterRepository(database);
    }

    @Provides
    @Singleton
    public PlayedVoteRepository providePlayedVoteRepository(Database database) {
        return new PlayedVoteRepository(database);
    }
}

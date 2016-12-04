package com.musicocracy.fpgk.ioc;

import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.util.AndroidLogger;
import com.musicocracy.fpgk.domain.util.Logger;
import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;

import java.sql.SQLException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class UtilityModule {
    @Provides
    @Singleton
    public PartySettings providePartySettings(Database database) {
        Dao<Party, Integer> dao = null;
        try {
            dao = database.getPartyDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new PartySettings(dao);
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
}

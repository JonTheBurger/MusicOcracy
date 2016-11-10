package com.musicocracy.fpgk.ioc;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.musicocracy.fpgk.model.dal.Database;

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
}

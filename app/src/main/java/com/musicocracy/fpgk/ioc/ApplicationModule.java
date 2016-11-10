package com.musicocracy.fpgk.ioc;

import android.content.Context;

import com.musicocracy.fpgk.CyberJukeboxApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    // Modules provide instances of classes
    // A program can have multiple modules for the purpose of modularity.
    private final CyberJukeboxApplication application;

    public ApplicationModule(CyberJukeboxApplication application) {
        this.application = application;
    }

    @Provides   // @Provides tells Dagger that this method can be used in the DI graph to fulfill a CyberJukeBoxApplication dependency.
    @Singleton  // @Singleton tells Dagger that we only want to provide one CyberJukeboxApplication instance per application. Dagger will cache @Singletons automatically.
    public CyberJukeboxApplication provideCyberJukeboxApplication() {
        return this.application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return this.application;
    }
}

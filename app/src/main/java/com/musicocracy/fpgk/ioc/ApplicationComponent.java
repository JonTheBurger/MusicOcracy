package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.ioc.fullstack.NetworkTestModule;
import com.musicocracy.fpgk.ui.NetworkTestActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Top-level injector class that defines the dependency injection graph
 */
@Singleton
@Component(
modules = {  // The dependency injection endpoints require class instances provided by the following modules:
        // Business Logic
        ApplicationModule.class,
        DatabaseModule.class,
        NetworkingModule.class,

        // Per-Activity
        NetworkTestModule.class,
})
public interface ApplicationComponent {
    // We put dependency injection endpoints here, e.g. Activities as inject methods.
    void inject(NetworkTestActivity activity); // Tell Dagger that NetworkTestActivity opts in to dependency injection.
}

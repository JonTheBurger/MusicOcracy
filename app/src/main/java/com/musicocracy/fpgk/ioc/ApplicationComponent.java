package com.musicocracy.fpgk.ioc;

import com.musicocracy.fpgk.musicocracy.NetworkTestActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {  // The dependency injection endpoints require class instances provided by the following modules:
    ApplicationModule.class,
})
public interface ApplicationComponent {
    // Components are injector classes that define the dependency injection graph.
    // A program can have multiple components for the purpose of modularity, but for our purposes we'll only be using one.

    // We put dependency injection endpoints here, e.g. Activities as inject methods.
    void inject(NetworkTestActivity activity); // Tell Dagger that DatabaseActivity opts in to dependency injection.
}

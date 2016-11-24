package com.musicocracy.fpgk.ioc;

import android.content.Context;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.ioc.activity.AddTermModule;
import com.musicocracy.fpgk.ioc.activity.BlacklistModule;
import com.musicocracy.fpgk.ioc.activity.ConnectModule;
import com.musicocracy.fpgk.ioc.activity.NetworkTestModule;
import com.musicocracy.fpgk.ioc.activity.NowPlayingModule;
import com.musicocracy.fpgk.ioc.activity.PartyConfigModule;
import com.musicocracy.fpgk.ioc.activity.RequestModule;
import com.musicocracy.fpgk.ioc.activity.SelectionModule;
import com.musicocracy.fpgk.ioc.activity.SongSelectModule;
import com.musicocracy.fpgk.ui.AddTermActivity;
import com.musicocracy.fpgk.ui.BlacklistActivity;
import com.musicocracy.fpgk.ui.ConnectActivity;
import com.musicocracy.fpgk.ui.NowPlayingActivity;
import com.musicocracy.fpgk.ui.PartyConfigActivity;
import com.musicocracy.fpgk.ui.RequestActivity;
import com.musicocracy.fpgk.ui.SelectionActivity;
import com.musicocracy.fpgk.ui.SongSelectActivity;
import com.musicocracy.fpgk.ui.TestNetworkTestActivity;

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
        SpotifyModule.class,
        UtilityModule.class,

        // Per-Activity
        NetworkTestModule.class,
        AddTermModule.class,
        BlacklistModule.class,
        ConnectModule.class,
        NowPlayingModule.class,
        PartyConfigModule.class,
        RequestModule.class,
        SelectionModule.class,
        SongSelectModule.class,
})
public interface ApplicationComponent {
    // We put dependency injection endpoints here, e.g. Activities as inject methods.
    void inject(CyberJukeboxApplication application);
    void inject(TestNetworkTestActivity activity); // Tell Dagger that TestNetworkTestActivity opts in to dependency injection.
    void inject(AddTermActivity activity);
    void inject(BlacklistActivity activity);
    void inject(ConnectActivity activity);
    void inject(NowPlayingActivity activity);
    void inject(PartyConfigActivity activity);
    void inject(RequestActivity activity);
    void inject(SelectionActivity activity);
    void inject(SongSelectActivity activity);
}

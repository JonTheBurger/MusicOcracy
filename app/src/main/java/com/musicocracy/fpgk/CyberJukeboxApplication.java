package com.musicocracy.fpgk;

import android.app.Application;

import com.musicocracy.fpgk.ioc.ApplicationComponent;
import com.musicocracy.fpgk.ioc.DaggerApplicationComponent;

public class CyberJukeboxApplication extends Application {
    ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder().build();
    }
}

package com.musicocracy.fpgk;

import android.app.Application;
import android.content.Context;

import com.musicocracy.fpgk.ioc.ApplicationComponent;
import com.musicocracy.fpgk.ioc.ApplicationModule;
import com.musicocracy.fpgk.ioc.DaggerApplicationComponent;

public class CyberJukeboxApplication extends Application {
    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        component.inject(this);
    }

    public static ApplicationComponent getComponent(Context context) {
        return ((CyberJukeboxApplication)context.getApplicationContext()).component;
    }
}

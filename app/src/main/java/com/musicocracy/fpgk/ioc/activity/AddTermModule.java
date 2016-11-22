package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.mvp.model.AddTermModel;
import com.musicocracy.fpgk.mvp.presenter.AddTermPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class AddTermModule {
    @Provides
    public AddTermModel provideAddTermModel() {
        return new AddTermModel();
    }

    @Provides
    public AddTermPresenter provideAddTermPresenter(AddTermModel model) {
        return new AddTermPresenter(model);
    }
}

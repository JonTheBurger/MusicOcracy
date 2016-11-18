package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.model.SelectionModel;
import com.musicocracy.fpgk.presenter.SelectionPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class SelectionModule {
    @Provides
    public SelectionModel provideSelectionModel() {
        return new SelectionModel();
    }

    @Provides
    public SelectionPresenter provideSelectionPresenter(SelectionModel model) {
        return new SelectionPresenter(model);
    }
}

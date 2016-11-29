package com.musicocracy.fpgk.ioc.activity;

import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.mvp.model.SelectionModel;
import com.musicocracy.fpgk.mvp.presenter.SelectionPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class SelectionModule {
    @Provides
    public SelectionModel provideSelectionModel(PartySettings settings) {
        return new SelectionModel(settings);
    }

    @Provides
    public SelectionPresenter provideSelectionPresenter(SelectionModel model) {
        return new SelectionPresenter(model);
    }
}

package com.musicocracy.fpgk.presenter;

import com.musicocracy.fpgk.model.PartyConfigModel;
import com.musicocracy.fpgk.view.PartyConfigView;

public class PartyConfigPresenter implements Presenter<PartyConfigView> {
    private final PartyConfigModel model;
    private PartyConfigView view;

    public PartyConfigPresenter(PartyConfigModel model) {
        this.model = model;
    }

    @Override
    public void setView(PartyConfigView view) {
        this.view = view;
    }
}

package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.util.PartySettings;

public class SelectionModel {
    private final PartySettings settings;

    public SelectionModel(PartySettings settings) {
        this.settings = settings;
    }

    public PartySettings getSettings() {
        return settings;
    }
}

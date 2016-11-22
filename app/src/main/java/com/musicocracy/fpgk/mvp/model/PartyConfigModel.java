package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.util.PartySettings;

public class PartyConfigModel {
    private final PartySettings settings;

    public PartyConfigModel(PartySettings settings) {
        this.settings = settings;
    }

    public PartySettings getSettings() {
        return settings;
    }
}

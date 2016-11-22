package com.musicocracy.fpgk.model;

import com.musicocracy.fpgk.model.util.PartySettings;

public class PartyConfigModel {
    private final PartySettings settings;

    public PartyConfigModel(PartySettings settings) {
        this.settings = settings;
    }

    public PartySettings getSettings() {
        return settings;
    }
}

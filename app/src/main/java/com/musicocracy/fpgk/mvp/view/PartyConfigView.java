package com.musicocracy.fpgk.mvp.view;

public interface PartyConfigView {
    String getPartyCode();
    void setPartyCode(String code);
    String getPartyName();
    int getTokenCount();
    int getTokenRefillMinutes();
    int getTokenRefillSeconds();
}

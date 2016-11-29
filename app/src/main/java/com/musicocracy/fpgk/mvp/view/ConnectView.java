package com.musicocracy.fpgk.mvp.view;

public interface ConnectView {
    String getPartyCode();
    String getPartyName();
    void onJoinSuccess();
    void onJoinError(String error);
}

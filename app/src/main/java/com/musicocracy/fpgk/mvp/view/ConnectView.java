package com.musicocracy.fpgk.mvp.view;

public interface ConnectView {
    String getPartyCode();
    String getPartyName();
    void showJoinSuccess();
    void showJoinError(String error);
}

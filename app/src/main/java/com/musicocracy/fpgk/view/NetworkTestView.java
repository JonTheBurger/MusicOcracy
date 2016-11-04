package com.musicocracy.fpgk.view;

public interface NetworkTestView {
    String getIpText();
    String getPortText();
    boolean getServerToggle();
    boolean getClientToggle();
    boolean getClientLocalToggle();
    void setServerConnected(boolean isConnected);
    void setClientConnected(boolean isConnected);
    void setClientLocal(boolean isLocal);
    void logServerEvent(String event);
    void logClientEvent(String event);
}

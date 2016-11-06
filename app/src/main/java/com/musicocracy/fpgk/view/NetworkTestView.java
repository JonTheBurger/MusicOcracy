package com.musicocracy.fpgk.view;

public interface NetworkTestView {
    String getIpText();
    String getPortText();
    boolean getServerToggle();
    boolean getClientToggle();
    boolean getLocalHostToggle();
    void setServerRunning(boolean isConnected);
    void setClientRunning(boolean isConnected);
    void setLocalHost(boolean isLocal);
    void logServerEvent(String event);
    void logClientEvent(String event);
}

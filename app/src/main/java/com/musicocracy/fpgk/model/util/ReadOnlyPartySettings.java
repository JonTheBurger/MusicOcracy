package com.musicocracy.fpgk.model.util;

public interface ReadOnlyPartySettings {
    String getPartyCode();
    String getPartyName();
    int getTokens();
    long getTokenRefillMillis();
}

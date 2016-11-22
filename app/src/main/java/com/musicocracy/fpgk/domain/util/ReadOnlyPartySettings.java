package com.musicocracy.fpgk.domain.util;

public interface ReadOnlyPartySettings {
    String getPartyCode();
    String getPartyName();
    int getTokens();
    long getTokenRefillMillis();
}

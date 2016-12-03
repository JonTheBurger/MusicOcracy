package com.musicocracy.fpgk.domain.util;

public interface ReadOnlyPartySettings {
    String getPartyCode();
    String getPartyName();
    int getCoinAllowance();
    long getCoinRefillMillis();
    String getSpotifyToken();
}

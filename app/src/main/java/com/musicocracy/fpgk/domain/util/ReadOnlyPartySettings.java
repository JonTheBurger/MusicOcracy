package com.musicocracy.fpgk.domain.util;

import com.musicocracy.fpgk.domain.dal.FilterMode;

public interface ReadOnlyPartySettings {
    int dbId();
    String getPartyCode();
    String getPartyName();
    FilterMode getFilterMode();
    boolean isHosting();
    int getCoinAllowance();
    long getCoinRefillMillis();
    String getSpotifyToken();
}

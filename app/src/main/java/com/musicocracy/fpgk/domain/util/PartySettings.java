package com.musicocracy.fpgk.domain.util;

public class PartySettings implements ReadOnlyPartySettings {
    private String partyCode = "";
    private String partyName = "";
    private int tokens;
    private long tokenRefillMillis;

    @Override
    public String getPartyCode() {
        return partyCode;
    }

    public void setPartyCode(String code) {
        partyCode = code;
    }

    @Override
    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    @Override
    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    @Override
    public long getTokenRefillMillis() {
        return tokenRefillMillis;
    }

    public void setTokenRefillMillis(long tokenRefillMillis) {
        this.tokenRefillMillis = tokenRefillMillis;
    }
}

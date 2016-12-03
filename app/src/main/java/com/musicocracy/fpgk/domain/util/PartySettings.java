package com.musicocracy.fpgk.domain.util;

public class PartySettings implements ReadOnlyPartySettings {
    private String partyCode = "";
    private String partyName = "";
    private String SpotifyToken = "";
    private int coins;
    private long coinRefillMillis;

    public PartySettings() {
        int x = 5;
    }

    @Override
    public String getPartyCode() {
        return partyCode;
    }

    public PartySettings setPartyCode(String code) {
        partyCode = code;
        return this;
    }

    @Override
    public String getPartyName() {
        return partyName;
    }

    public PartySettings setPartyName(String partyName) {
        this.partyName = partyName;
        return this;
    }

    @Override
    public int getCoinAllowance() {
        return coins;
    }

    public PartySettings setCoinAllowance(int coins) {
        this.coins = coins;
        return this;
    }

    @Override
    public long getCoinRefillMillis() {
        return coinRefillMillis;
    }

    public PartySettings setCoinRefillMillis(long coinRefillMillis) {
        this.coinRefillMillis = coinRefillMillis;
        return this;
    }

    @Override
    public String getSpotifyToken() {
        return SpotifyToken;
    }

    public PartySettings setSpotifyToken(String token) {
        this.SpotifyToken = token;
        return this;
    }
}

package com.musicocracy.fpgk.domain.dal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "PlayRequest")
public class PlayRequest {
    PlayRequest() {} // OrmLite requires a default constructor

    public PlayRequest(Party party, Guest requester, MusicService service, String songId, Timestamp requestTime) {
        this.party = party;
        this.requester = requester;
        this.service = service;
        this.songId = songId;
        this.requestTime = requestTime;
    }

    public int getId() {
        return id;
    }

    public Party getParty() {
        return party;
    }

    public Guest getRequester() {
        return requester;
    }

    public MusicService getService() {
        return service;
    }

    public String getSongId() {
        return songId;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    @Override
    public String toString() {
        return getId() + ": " + getSongId() + " from "  + getRequester().getId() + " at " + getRequestTime().toString();
    }

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true) private int id;
    @DatabaseField(foreign = true, columnName = PARTY_COLUMN) private Party party;
    @DatabaseField(foreign = true, columnName = REQUESTER_COLUMN) private Guest requester;
    @DatabaseField private MusicService service;
    @DatabaseField(columnName = SONG_ID_COLUMN) private String songId;
    @DatabaseField private Timestamp requestTime;

    public static final String PARTY_COLUMN = "party_id";
    public static final String REQUESTER_COLUMN = "requester_id";
    public static final String SONG_ID_COLUMN = "songId";
}

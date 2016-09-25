package com.musicocracy.fpgk.model.dal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "PlayRequest")
public class PlayRequest {
    public PlayRequest() {} // OrmLite requires a default constructor

    public PlayRequest(MusicService service, String songId, int requesterId, Timestamp requestTime) {
        this.service = service;
        this.songId = songId;
        this.requesterId = requesterId;
        this.requestTime = requestTime;
    }

    public int getId() {
        return id;
    }

    public MusicService getService() {
        return service;
    }

    public String getSongId() {
        return songId;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    @Override
    public String toString() {
        return getId() + ": " + getService().toString() + " - " + getSongId() + " from "  + getRequesterId() + " at " + getRequestTime().toString();
    }

    @DatabaseField(allowGeneratedIdInsert = true, generatedId = true)
    private int id;
    @DatabaseField
    private MusicService service;
    @DatabaseField
    private String songId;
    @DatabaseField(defaultValue = "0")
    private int requesterId;
    @DatabaseField
    private Timestamp requestTime;
}

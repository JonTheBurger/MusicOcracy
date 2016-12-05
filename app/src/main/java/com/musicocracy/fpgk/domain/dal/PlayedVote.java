package com.musicocracy.fpgk.domain.dal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "PlayRequest")
public class PlayedVote {
    PlayedVote() {} // OrmLite requires a default constructor

    public PlayedVote(Party party, MusicService service, String songId, Timestamp voteTime) {
        this.party = party;
        this.service = service;
        this.songId = songId;
        this.voteTime = voteTime;
    }

    public int getId() {
        return id;
    }

    public Party getParty() {
        return party;
    }

    public MusicService getService() {
        return service;
    }

    public String getSongId() {
        return songId;
    }

    public Timestamp getVoteTime() {
        return voteTime;
    }

    @Override
    public String toString() {
        return getId() + ": " + getService().toString() + " - " + getSongId() + " at " + getVoteTime().toString();
    }

    @DatabaseField(generatedId = true) private int id;
    @DatabaseField(foreign = true) private Party party;
    @DatabaseField private MusicService service;
    @DatabaseField(columnName = SONG_ID_COL_NAME) private String songId;
    @DatabaseField private Timestamp voteTime;

    public static final String SONG_ID_COL_NAME = "songId";
}

package com.musicocracy.fpgk.domain.dal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "SongFilter")
public class SongFilter {
    SongFilter() {} // OrmLite requires a default constructor

    public SongFilter(MusicService service, String songId, Party party, FilterMode filterMode) {
        this.service = service;
        this.songId = songId;
        this.party = party;
        this.filterMode = filterMode;
    }

    public MusicService getService() {
        return service;
    }

    public String getSongId() {
        return songId;
    }

    public Party getParty() {
        return party;
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    @Override
    public String toString() {
        return "SongId: " + getSongId() + ",Party: " + getParty() + ", FilterMode: "  + getFilterMode();
    }

    @DatabaseField private MusicService service;
    @DatabaseField private String songId;
    @DatabaseField(foreign = true, columnName = PARTY_COLUMN) private Party party;
    @DatabaseField(columnName = FILTER_MODE_COLUMN) private FilterMode filterMode;

    public static final String PARTY_COLUMN = "party_id";
    public static final String FILTER_MODE_COLUMN = "filterMode";
}

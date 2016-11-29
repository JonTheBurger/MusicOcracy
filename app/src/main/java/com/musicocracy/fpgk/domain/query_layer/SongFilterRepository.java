package com.musicocracy.fpgk.domain.query_layer;


import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.SongFilter;

import java.sql.SQLException;
import java.util.List;

public class SongFilterRepository {
    private Database database;
    private Dao<SongFilter, Integer> dao;

    public SongFilterRepository(Database database) {
        this.database = database;
    }

    public void add(SongFilter songFilter) {
        try {
            dao = database.getSongFilterDao();
            dao.createOrUpdate(songFilter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isBlacklistedSongId(String songId) {
        return isValidSongId(songId, FilterMode.BLACK_LIST);
    }

    public boolean isBlacklistedPlayRequest(PlayRequest playRequest) {
        return isValidPlayRequest(playRequest, FilterMode.BLACK_LIST);
    }

    public boolean isWhitelistedSongId(String songId) {
        return isValidSongId(songId, FilterMode.WHITE_LIST);
    }

    public boolean isWhitelistedPlayRequest(PlayRequest playRequest) {
        return isValidPlayRequest(playRequest, FilterMode.WHITE_LIST);
    }

    public boolean isValidSongId(String songId, FilterMode filterMode) {
        try {
            dao = database.getSongFilterDao();
            List<SongFilter> filterList = dao.queryForAll();
            String lowerId = songId.toLowerCase();
            for(SongFilter songFilter : filterList) {
                String lookupId = songFilter.getSongId().toLowerCase();
                boolean songsAreEqual = lookupId.equals(lowerId);
                if(songsAreEqual && filterMode == FilterMode.BLACK_LIST) {
                    return false;
                } else if(songsAreEqual && filterMode == FilterMode.WHITE_LIST) {
                    return true;
                }
            }
            if(filterMode == FilterMode.BLACK_LIST || filterMode == FilterMode.NONE) {
                return true;
            } else if(filterMode == FilterMode.WHITE_LIST) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isValidPlayRequest(PlayRequest playRequest, FilterMode filterMode) {
        return isValidSongId(playRequest.getSongId(), filterMode);
    }
}

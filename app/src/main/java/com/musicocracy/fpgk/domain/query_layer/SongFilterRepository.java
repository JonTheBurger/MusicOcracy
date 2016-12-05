package com.musicocracy.fpgk.domain.query_layer;


import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.SongFilter;

import java.sql.SQLException;
import java.util.ArrayList;
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

    public List<SongFilter> getAllNonelistedSongFilters() {
        List<SongFilter> returnList = new ArrayList<>();
        try {
            dao = database.getSongFilterDao();
            returnList =
                    dao.query(
                            dao.queryBuilder().where()
                                    .eq("filterMode", FilterMode.NONE)
                                    .prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }


    public List<SongFilter> getAllWhitelistedSongFilters() {
        List<SongFilter> returnList = new ArrayList<>();
        try {
            dao = database.getSongFilterDao();
            returnList =
                    dao.query(
                            dao.queryBuilder().where()
                                    .eq("filterMode", FilterMode.WHITE_LIST)
                                    .prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }

    public List<SongFilter> getAllBlacklistedSongFilters() {
        List<SongFilter> returnList = new ArrayList<>();
        try {
            dao = database.getSongFilterDao();
            returnList =
                    dao.query(
                            dao.queryBuilder().where()
                                    .eq("filterMode", FilterMode.BLACK_LIST)
                                    .prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }

    public List<SongFilter> getAllSongFilters() {
        List<SongFilter> returnList = new ArrayList<>();
        try {
            dao = database.getSongFilterDao();
            returnList = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return returnList;
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
            if(filterMode == FilterMode.NONE) {
                return true;
            }
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
            if(filterMode == FilterMode.BLACK_LIST) {
                return true;
            } else if(filterMode == FilterMode.WHITE_LIST) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        return false;
    }

    public boolean isValidPlayRequest(PlayRequest playRequest, FilterMode filterMode) {
        return isValidSongId(playRequest.getSongId(), filterMode);
    }
}

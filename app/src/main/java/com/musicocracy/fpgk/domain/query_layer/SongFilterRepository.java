package com.musicocracy.fpgk.domain.query_layer;


import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.SongFilter;

import java.sql.SQLException;

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
}

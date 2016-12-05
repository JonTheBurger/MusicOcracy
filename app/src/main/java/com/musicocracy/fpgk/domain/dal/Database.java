package com.musicocracy.fpgk.domain.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.musicocracy.fpgk.ui.R;

import java.sql.SQLException;

public class Database extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "MusicOcracy.sqlite";
    private static final int DATABASE_VERSION = 2;

    private Dao<Party, Integer> partyDao;
    private Dao<Guest, Integer> guestDao;
    private Dao<PlayRequest, Integer> playRequestDao;
    private Dao<PlayedVote, Integer> playedVoteDao;
    private Dao<SongFilter, Integer> songFilterDao;

    public Database(Context context) {
        this(context,
             DATABASE_NAME,
             null,
             DATABASE_VERSION,
             R.raw.ormlite_config);
    }

    public Database(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, int configFileId) {
        super(context, databaseName, factory, databaseVersion, configFileId);
    }

    public static Database InMemory(Context context) {
        return new Database(context, null, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Party.class);
            TableUtils.createTable(connectionSource, Guest.class);
            TableUtils.createTable(connectionSource, PlayRequest.class);
            TableUtils.createTable(connectionSource, PlayedVote.class);
            TableUtils.createTable(connectionSource, SongFilter.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, SongFilter.class, false);
            TableUtils.dropTable(connectionSource, PlayRequest.class, false);
            TableUtils.dropTable(connectionSource, PlayedVote.class, false);
            TableUtils.dropTable(connectionSource, Guest.class, false);
            TableUtils.dropTable(connectionSource, Party.class, false);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Party, Integer> getPartyDao() throws SQLException {
        if (partyDao == null) {
            partyDao = getDao(Party.class);
        }
        return partyDao;
    }

    public Dao<Guest, Integer> getGuestDao() throws SQLException {
        if (guestDao == null) {
            guestDao = getDao(Guest.class);
        }
        return guestDao;
    }

    public Dao<PlayRequest, Integer> getPlayRequestDao() throws SQLException {
        if (playRequestDao == null) {
            playRequestDao = getDao(PlayRequest.class);
        }
        return playRequestDao;
    }

    public Dao<PlayedVote, Integer> getPlayedVoteDao() throws SQLException {
        if (playedVoteDao == null) {
            playedVoteDao = getDao(PlayedVote.class);
        }
        return playedVoteDao;
    }

    public Dao<SongFilter, Integer> getSongFilterDao() throws SQLException {
        if (songFilterDao == null) {
            songFilterDao = getDao(SongFilter.class);
        }
        return songFilterDao;
    }
}

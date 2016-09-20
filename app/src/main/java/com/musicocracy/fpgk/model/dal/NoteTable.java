package com.musicocracy.fpgk.model.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.musicocracy.fpgk.musicocracy.R;

import java.sql.SQLException;

public class NoteTable extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "TestDatabase";
    private static final int DATABASE_VERSION = 1;
    private Dao<Note, Long> noteDao;

    public NoteTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Note.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Note.class, false);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Note, Long> getDao() throws SQLException {
        if (noteDao == null) {
            noteDao = getDao(Note.class);
        }
        return noteDao;
    }
}

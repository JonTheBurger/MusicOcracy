package com.musicocracy.fpgk.model.dal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.musicocracy.fpgk.musicocracy.R;

import java.sql.SQLException;

public class PlayRequestTable extends OrmLiteSqliteOpenHelper {
    private Dao<PlayRequest, Long> dao;

    public PlayRequestTable(Context context) {
        this(context,
              context.getResources().getString(R.string.database_name),
              null,
              context.getResources().getInteger(R.integer.database_version),
              R.raw.ormlite_config);
    }

    public PlayRequestTable(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion, int configFileId) {
        super(context, databaseName, factory, databaseVersion, configFileId);
    }

    public static PlayRequestTable InMemory(Context context) {
        return new PlayRequestTable(context, null, null, context.getResources().getInteger(R.integer.database_version), R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, PlayRequest.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, PlayRequest.class, false);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<PlayRequest, Long> getDao() throws SQLException {
        if (dao == null) {
            dao = getDao(PlayRequest.class);
        }
        return dao;
    }
}

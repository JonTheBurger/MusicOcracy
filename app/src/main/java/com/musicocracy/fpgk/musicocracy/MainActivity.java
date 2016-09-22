package com.musicocracy.fpgk.musicocracy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.model.dal.Note;
import com.musicocracy.fpgk.model.dal.NoteTable;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            testOutOrmLiteDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void testOutOrmLiteDatabase() throws SQLException {
        NoteTable noteTable = OpenHelperManager.getHelper(this, NoteTable.class);

        Dao<Note, Long> noteDao = noteTable.getDao();

        Date created = new Date(010156000);

        noteDao.create(new Note("note Example 1", "note Example 1 Description", created));
        noteDao.create(new Note("note Example 2", "note Example 2 Description", created));
        noteDao.create(new Note("note Example 3", "note Example 3 Description", created));

        List<Note> notes = noteDao.queryForAll();
    }

    public void launchDbActivity(View view) {
        //Intent databaseIntent = new Intent(MainActivity.this, DatabaseActivity.class);
        //MainActivity.this.startActivity(databaseIntent);
    }
}

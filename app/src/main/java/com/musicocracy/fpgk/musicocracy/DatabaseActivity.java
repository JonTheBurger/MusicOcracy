package com.musicocracy.fpgk.musicocracy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.model.dal.MusicService;
import com.musicocracy.fpgk.model.dal.PlayRequest;
import com.musicocracy.fpgk.model.dal.PlayRequestTable;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity {
    private PlayRequestTable table;
    private Dao<PlayRequest, Long> dao;
    private ArrayList<PlayRequest> requests = new ArrayList<>();
    private ArrayAdapter<PlayRequest> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requests);
        ListView list = (ListView)findViewById(R.id.request_list);
        list.setAdapter(adapter);

        table = OpenHelperManager.getHelper(this, PlayRequestTable.class);
        try {
            dao = table.getDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRequests(View view) {
        requests.add(new PlayRequest(MusicService.SPOTIFY, "song_id_1", 10, Timestamp.valueOf("2016-08-28 01:12:03")));
        requests.add(new PlayRequest(MusicService.SPOTIFY, "song_id_2", 11, Timestamp.valueOf("2016-08-28 11:45:22")));
        requests.add(new PlayRequest(MusicService.SPOTIFY, "song_id_3", 10, Timestamp.valueOf("2016-08-28 21:58:45")));
        adapter.notifyDataSetChanged();
    }

    public void clearRequests(View view) {
        try {
            dao.delete(dao.queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        requests.clear();
        adapter.notifyDataSetChanged();
    }

    public void saveRequests(View view) {
        try {
            for(PlayRequest r : requests) {
                dao.createOrUpdate(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadRequests(View view) {
        try {
            List<PlayRequest> loaded = new ArrayList<>(dao.queryForAll());
            requests.addAll(loaded);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }
}

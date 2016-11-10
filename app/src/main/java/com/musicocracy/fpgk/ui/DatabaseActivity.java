package com.musicocracy.fpgk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.model.dal.Database;
import com.musicocracy.fpgk.model.dal.FilterMode;
import com.musicocracy.fpgk.model.dal.Guest;
import com.musicocracy.fpgk.model.dal.MusicService;
import com.musicocracy.fpgk.model.dal.Party;
import com.musicocracy.fpgk.model.dal.PlayRequest;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity {
    private Database database;
    private Dao<PlayRequest, Integer> dao;
    private ArrayList<PlayRequest> requests = new ArrayList<>();
    private ArrayAdapter<PlayRequest> adapter;
    private Party party;
    private Guest guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        Log.i("Life", "onCreate");

        database = OpenHelperManager.getHelper(this, Database.class);
        initParty();
        initGuest();
        try {
            dao = database.getPlayRequestDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requests);
        ListView list = (ListView)findViewById(R.id.request_list);
        list.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Life", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Life", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Life", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Life", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Life", "onDestroy");
    }

    private void initParty() {
        party = new Party("MySweetParty", "#Party", new Timestamp((int)System.currentTimeMillis() - 1000000), null, FilterMode.NONE, true);

        try {
            Dao<Party, Integer> partyDao = database.getPartyDao();
            partyDao.createOrUpdate(party);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initGuest() {
        guest = new Guest(party, "Bob", "74:29:20:05:12", new Timestamp((int)System.currentTimeMillis() - 10000), false);
        try {
            Dao<Guest, Integer> guestDao = database.getGuestDao();
            guestDao.createOrUpdate(guest);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRequests(View view) {
        requests.add(new PlayRequest(party, guest, MusicService.SPOTIFY, "Don't Stop Me Now", new Timestamp((int)System.currentTimeMillis() - 5000)));
        requests.add(new PlayRequest(party, guest, MusicService.SPOTIFY, "Bicycle",  new Timestamp((int)System.currentTimeMillis() - 2500)));
        requests.add(new PlayRequest(party, guest, MusicService.SPOTIFY, "Killer Queen",  new Timestamp((int)System.currentTimeMillis())));
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

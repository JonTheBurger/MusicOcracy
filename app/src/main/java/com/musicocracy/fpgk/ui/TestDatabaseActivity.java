package com.musicocracy.fpgk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.dal.PlayRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class TestDatabaseActivity extends AppCompatActivity {
    private Database database;
    private Dao<Party, Integer> partyDao;
    private Dao<Guest, Integer> guestDao;
    private Dao<PlayRequest, Integer> playRequestDao;
    private List<String> tableList = Arrays.asList(PARTY, GUEST, PLAYREQUEST);
    private ArrayAdapter<Object> adapter;
    private static final String PARTY = "Party";
    private static final String GUEST = "Guest";
    private static final String PLAYREQUEST = "PlayRequest";
    private String currentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_database);
        ButterKnife.bind(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        // Add the tables that can be viewed from the test activity
        tableList = new ArrayList<>();
        tableList.add(PARTY);
        tableList.add(GUEST);
        tableList.add(PLAYREQUEST);

        tableSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tableList));

        database = OpenHelperManager.getHelper(this, Database.class);
        try {
            partyDao = database.getPartyDao();
            guestDao = database.getGuestDao();
            playRequestDao = database.getPlayRequestDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @OnItemSelected(R.id.tableSpinner)
    void setCurrentTable(int position) {
        currentTable = tableList.get(position);
        loadTable();
    }

    @BindView(R.id.request_list)
    public ListView listView;

    @BindView(R.id.tableSpinner)
    public Spinner tableSpinner;

    @OnClick(R.id.clear_table)
    public void clearTable() {
        try {
            switch(currentTable) {
                case PARTY :
                    partyDao.delete(partyDao.queryForAll());
                    break;

                case GUEST :
                    guestDao.delete(guestDao.queryForAll());
                    break;

                case PLAYREQUEST :
                    playRequestDao.delete(playRequestDao.queryForAll());
                    break;
                default :
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadTable();
    }

    public void loadTable() {
        adapter.clear();
        List<? extends Object> loaded = new ArrayList<>();
        try{
            switch(currentTable) {
                case PARTY :
                    loaded = new ArrayList<>(partyDao.queryForAll());
                    break;

                case GUEST :
                    loaded = guestDao.queryForAll();
                    break;

                case PLAYREQUEST :
                    loaded = new ArrayList<>(playRequestDao.queryForAll());
                    break;
                default :
                    break;
            }
            adapter.addAll(loaded);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

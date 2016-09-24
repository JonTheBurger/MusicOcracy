package com.musicocracy.fpgk.musicocracy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import com.musicocracy.fpgk.model.dal.MusicService;
import com.musicocracy.fpgk.model.dal.PlayRequest;
import com.musicocracy.fpgk.model.dal.PlayRequestTable;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity {
    private PlayRequestTable table;
    private Dao<PlayRequest, Long> dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        table = OpenHelperManager.getHelper(this, PlayRequestTable.class);
        try {
            dao = table.getDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addRandomPlayRequest() throws SQLException {
        PlayRequest request1 = new PlayRequest(MusicService.SPOTIFY, "song_id_1", 10, Date.valueOf("2016-08-28 21:58:52"));
        PlayRequest request2 = new PlayRequest(MusicService.SPOTIFY, "song_id_2", 11, Date.valueOf("2016-08-28 22:00:33"));
        PlayRequest request3 = new PlayRequest(MusicService.SPOTIFY, "song_id_3", 10, Date.valueOf("2016-08-28 22:03:12"));

        dao.create(request1);
        dao.create(request2);
        dao.create(request3);
    }

    private void deleteAllPlayRequests() {
    }

    private List<String> getAllRequestIds() throws SQLException {
        List<String> ids = new ArrayList<>();
        List<PlayRequest> requests = dao.queryForAll();
        for(PlayRequest r : requests) {
            ids.add(r.getSongId());
        }
        return ids;
    }
}

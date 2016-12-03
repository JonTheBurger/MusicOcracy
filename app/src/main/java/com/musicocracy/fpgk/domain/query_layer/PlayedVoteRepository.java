package com.musicocracy.fpgk.domain.query_layer;

import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.PlayedVote;
import com.musicocracy.fpgk.domain.util.ValueComparator;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class PlayedVoteRepository {
    private Database database;
    private Dao<PlayedVote, Integer> dao;
    private SongFilterRepository songFilterRepository;
    private List<String> lastVotableSongIds;
    private final Random random;

    public PlayedVoteRepository(Database database) {
        this.database = database;
        lastVotableSongIds = new ArrayList<>();
        random = new Random();
    }

    public void add(PlayedVote playedVote) {
        try {
            dao = database.getPlayedVoteDao();
            dao.createOrUpdate(playedVote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PlayedVote> getAllPlayedVotes() {
        List<PlayedVote> returnList = new ArrayList<>();
        try {
            dao = database.getPlayedVoteDao();
            returnList = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }

    public List<String> getAllPlayedVoteSongIds() {
        List<PlayedVote> playedVoteList = getAllPlayedVotes();
        List<String> returnList = new ArrayList<>();

        for(PlayedVote playedVote : playedVoteList) {
            returnList.add(playedVote.getSongId());
        }
        return returnList;
    }



}



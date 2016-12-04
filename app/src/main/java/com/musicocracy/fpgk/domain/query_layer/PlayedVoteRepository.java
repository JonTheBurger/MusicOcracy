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

    public PlayedVoteRepository(Database database) {
        this.database = database;
    }

    public void add(PlayedVote playedVote) {
        try {
            dao = database.getPlayedVoteDao();
            dao.createOrUpdate(playedVote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getMillisSinceTimestamp(Timestamp timestamp) {
        long timestampMillis = timestamp.getTime();
        long currTimeMillis = now().getTime();
        return (currTimeMillis - timestampMillis);
    }

    public long getMillisSincePlayedVoteSongId(String songId) {
        Timestamp timestamp = getLatestTimestampOfPlayedVoteBySongId(songId);
        return getMillisSinceTimestamp(timestamp);
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

    public List<PlayedVote> getPlayedVoteBySongId(String songId){
        List<PlayedVote> returnList = new ArrayList<>();
        try {
            dao = database.getPlayedVoteDao();

            returnList =
                    dao.query(
                            dao.queryBuilder().where()
                                    .eq(PlayedVote.SONG_ID_COL_NAME, songId)
                                    .prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println(returnList.size());
            return returnList;
        }
    }

    public Timestamp getLatestTimestampOfPlayedVoteBySongId(String songId) {
        System.out.println("In PlayedVoteRepo\n" + songId);
        List<PlayedVote> playedVoteList = getPlayedVoteBySongId(songId);
        Timestamp latest = new Timestamp(0);
        Timestamp newStamp;
        for(PlayedVote playedVote : playedVoteList) {
            newStamp = playedVote.getVoteTime();
            System.out.println(latest.getTime());
            System.out.println(newStamp.getTime());
            if(newStamp.after(latest)) {
                latest = newStamp;
            }
        }
        return latest;
    }

    // TODO Extract all now() methods from all classes
    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

}



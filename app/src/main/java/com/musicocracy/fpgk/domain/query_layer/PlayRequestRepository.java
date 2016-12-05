package com.musicocracy.fpgk.domain.query_layer;

import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.Database;
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

public class PlayRequestRepository {
    private Database database;
    private Dao<PlayRequest, Integer> dao;
    private SongFilterRepository songFilterRepository;
    private PlayedVoteRepository playedVoteRepository;
    private long delayMillis;
    private final long DEFAULT_DELAY = 3600000;
    private final Random random;

    public PlayRequestRepository(Database database) {
        this.database = database;
        songFilterRepository = new SongFilterRepository(database);
        playedVoteRepository = new PlayedVoteRepository(database);
        random = new Random();
        delayMillis = DEFAULT_DELAY;
    }

    public PlayRequestRepository(Database database, SongFilterRepository songFilterRepository, PlayedVoteRepository playedVoteRepository) {
        this.database = database;
        this.songFilterRepository = songFilterRepository;
        this.playedVoteRepository = playedVoteRepository;
        random = new Random();
        delayMillis = DEFAULT_DELAY;
    }

    public PlayRequestRepository(Database database, SongFilterRepository songFilterRepository, PlayedVoteRepository playedVoteRepository, long delayMillis) {
        this.database = database;
        this.songFilterRepository = songFilterRepository;
        this.playedVoteRepository = playedVoteRepository;
        random = new Random();
        this.delayMillis = delayMillis;
    }

    public void add(PlayRequest playRequest) {
        addWithFilterAndDelay(playRequest, FilterMode.NONE, delayMillis);
    }

    public void addWithDelay(PlayRequest playRequest, long delayMillis) {
        addWithFilterAndDelay(playRequest, FilterMode.NONE, delayMillis);
    }

    public void addWithFilter(PlayRequest playRequest, FilterMode filterMode) {
        addWithFilterAndDelay(playRequest, filterMode, delayMillis);
    }

    public void addWithFilterAndDelay(PlayRequest playRequest, FilterMode filterMode, long delayMillis) {
        try {

            String songId = playRequest.getSongId();
            long differenceMillis = playedVoteRepository.getMillisSincePlayedVoteSongId(songId);

            if(differenceMillis < delayMillis) {
                throw new IllegalArgumentException("The requested song has been played too recently.");
            } else {
                if(songFilterRepository.isValidPlayRequest(playRequest, filterMode)){
                    dao = database.getPlayRequestDao();
                    dao.createOrUpdate(playRequest);
                } else {
                    throw new IllegalArgumentException("The party's filter has rejected your song request.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllRequestedSongIds() {
        List<String> returnList = new ArrayList<>();
        try {
            dao = database.getPlayRequestDao();
            List<PlayRequest> playRequestList = dao.queryForAll();
            for(PlayRequest playRequest : playRequestList) {
                returnList.add(playRequest.getSongId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }

    public List<String> getVotableSongIds(int count) {
        List<String> returnList = new ArrayList<>();
        try {
            dao = database.getPlayRequestDao();
            List<String> newestRequestedSongIdsList = getNewestRequestedSongIds(count);
            List<String> oldestRequestedSongIdsList = getOldestRequestedSongIds(count);
            List<String> mostRequestedSongIdsList = getMostRequestedSongIds(count);
            List<String> leastRequestedSongIdsList = getLeastRequestedSongIds(count);

            for(int i = 0; i < count; i++) {
                int listId = random.nextInt(4) + 1;
                int index = random.nextInt(count);
                String nextId = "";
                switch(listId) {
                    case 1:
                        nextId = newestRequestedSongIdsList.get(index);
                        break;
                    case 2:
                        nextId = oldestRequestedSongIdsList.get(index);
                        break;
                    case 3:
                        nextId = mostRequestedSongIdsList.get(index);
                        break;
                    case 4:
                        nextId = leastRequestedSongIdsList.get(index);
                        break;
                }
                returnList.add(nextId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }

    public List<PlayRequest> getRequestsMadeByGuest(Guest guest){
        List<PlayRequest> requestList = new ArrayList<>();
        try {
            dao = database.getPlayRequestDao();

            requestList =
                    dao.query(
                            dao.queryBuilder().where()
                                    .eq("requester", guest)
                                    .prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return requestList;
        }
    }

    public List<String> getMostRequestedSongIds(int count) {
        return getRequestedSongIdsByPopularity(count, 0);
    }

    public List<String> getLeastRequestedSongIds(int count) {
        return getRequestedSongIdsByPopularity(count, 1);
    }

    public List<String> getNewestRequestedSongIds(int count) {
        return getRequestedSongIdsByTime(count, 0);
    }

    public List<String> getOldestRequestedSongIds(int count) {
        return getRequestedSongIdsByTime(count, 1);
    }

    public List<String> getRequestedSongIdsByPopularity(int count, int mode) {
        List<String> returnList = new ArrayList<>();
        try {
            dao = database.getPlayRequestDao();
            Map<String, Integer> requestHistogram = new HashMap<String, Integer>();
            List<PlayRequest> requestList = dao.queryForAll();
            for(PlayRequest playRequest : requestList) {
                String songId = playRequest.getSongId();
                Integer currCount = requestHistogram.get(songId);
                Integer newCount = 0;
                if (currCount != null ) {
                    newCount = currCount + 1;
                }
                requestHistogram.put(songId, newCount);
            }

            Map<String, Integer> sortedHistogram = new LinkedHashMap<>();
            sortedHistogram = sortByValue(requestHistogram);

            Object keySet[] = sortedHistogram.keySet().toArray();
            switch(mode) {
                case 0:
                    for(int i = 0; i < count; i++) {
                        returnList.add(keySet[i].toString());
                    }
                    break;
                case 1:
                    int start = keySet.length - 1;
                    for(int i = start; i > start - count; i--) {
                        returnList.add(keySet[i].toString());
                    }
                    break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
        } finally {
            return returnList;
        }
    }

    public List<String> getRequestedSongIdsByTime(int count, int mode) {
        List<String> returnList = new ArrayList<>();
        try {
            dao = database.getPlayRequestDao();
            Map<String, Timestamp> requestHistogram = new HashMap<>();
            List<PlayRequest> requestList = dao.queryForAll();
            for(PlayRequest playRequest : requestList) {
                String songId = playRequest.getSongId();
                Timestamp requestTime = playRequest.getRequestTime();
                Timestamp currTime = requestHistogram.get(songId);
                if (currTime == null ) {
                    requestHistogram.put(songId, requestTime);
                }

            }

            Map<String, Integer> sortedHistogram = new LinkedHashMap<>();
            sortedHistogram = sortByValue(requestHistogram);

            Object keySet[] = sortedHistogram.keySet().toArray();
            switch(mode) {
                case 0:
                    for(int i = 0; i < count; i++) {
                        returnList.add(keySet[i].toString());
                    }
                    break;
                case 1:
                    int start = keySet.length - 1;
                    for(int i = start; i > start - count; i--) {
                        returnList.add(keySet[i].toString());
                    }
                    break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
        } finally {
            return returnList;
        }
    }

    public static Map sortByValue(Map unsortedMap) {
        Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
        sortedMap.putAll(unsortedMap);
        return sortedMap;
    }



}



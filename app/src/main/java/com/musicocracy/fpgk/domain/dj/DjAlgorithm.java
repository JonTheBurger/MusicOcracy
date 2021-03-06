package com.musicocracy.fpgk.domain.dj;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.SongFilter;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;
import com.musicocracy.fpgk.domain.util.CircularQueue;
import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.domain.util.Tuple;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class DjAlgorithm {
    private static final int BACKUP_SONG_COUNT = 10;
    private final CircularQueue<String> backupSongs = new CircularQueue<>(BACKUP_SONG_COUNT);
    private final Set<String> idsOfVoters = new HashSet<>();  // uniqueIds of guests that have voted for a song this iteration (you can vote for a new song once per song)
    private final Database database;
    private final PartySettings partySettings;
    private final Observable<String> mostRequestedObservable;

    public DjAlgorithm(Database database, PartySettings partySettings) {
        if (database == null || partySettings == null) { throw new IllegalArgumentException("No dependencies may be null"); }
        this.database = database;
        this.partySettings = partySettings;
        this.mostRequestedObservable = getTopObservable()
                .take(BACKUP_SONG_COUNT)
                .subscribeOn(Schedulers.io());
        mostRequestedObservable.subscribe(new Action1<String>() {
            @Override
            public void call(String uri) {
                backupSongs.enqueue(uri);
            }
        });
    }

    public Observable<String> getTopObservable() {
        List<PlayRequest> all;
        try {
            all = database.getPlayRequestDao().queryForAll();
        } catch (SQLException e) {
            return Observable.empty();
        }
        Map<String, Integer> uriByRequests = new HashMap<>();
        for (PlayRequest request : all) {
            String uri = request.getSongId();
            int requests = 1;
            if (uriByRequests.containsKey(uri)) {
                requests = uriByRequests.get(uri) + 1;
            }
            uriByRequests.put(uri, requests);
        }
        return Observable.from(uriByRequests.entrySet())
                .sorted(new Func2<Map.Entry<String, Integer>, Map.Entry<String, Integer>, Integer>() {
                    @Override
                    public Integer call(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs) {
                        return rhs.getValue().compareTo(lhs.getValue());
                    }
                })
                .map(new Func1<Map.Entry<String, Integer>, String>() {
                    @Override
                    public String call(Map.Entry<String, Integer> entry) {
                        return entry.getKey();
                    }
                });
    }

    private static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    public String dequeueNextSongUri() throws SQLException {
        // Get top voted song in database
        List<String> topUriList = getTopObservable().toList().toBlocking().first();
        if (topUriList.size() < 1) {
            mostRequestedObservable.toBlocking().firstOrDefault(null);
            return backupSongs.size() > 0 ? backupSongs.dequeue() : "";
        }
        String topUri = topUriList.get(0);
        backupSongs.enqueue(topUri);

        // Delete requests for top voted song from database
        Dao<PlayRequest, Integer> dao = database.getPlayRequestDao();
        DeleteBuilder<PlayRequest, Integer> del = dao.deleteBuilder();
        del.where().eq(PlayRequest.SONG_ID_COLUMN, topUri);
        del.delete();

        // Re-enable voting rights
        idsOfVoters.clear();
        return topUri;
    }

    public List<String> getVotableSongUris() throws SQLException {
        List<String> raw = getTopObservable().toList().toBlocking().first();
        List<String> valid = Observable
                .from(database.getPlayRequestDao()
                    .queryBuilder()
                    .where()
                    .eq(PlayRequest.PARTY_COLUMN, partySettings.raw())
                    .query())
                .map(new Func1<PlayRequest, String>() {
                    @Override
                    public String call(PlayRequest playRequest) {
                        return playRequest.getSongId();
                    }
                })
                .toList()
                .toBlocking()
                .first();
        List<String> votable = new ArrayList<>();
        for (int i = 0; (votable.size() < 4 && i < raw.size()); i++) {
            if (valid.contains(raw.get(i))) {
                votable.add(raw.get(i));
            }
        }
        return votable;
    }

    public void voteFor(String uri, String requesterId) throws IllegalArgumentException, SQLException {
        if (uri == null || requesterId == null) { throw new IllegalArgumentException("Arguments cannot be null"); }

        if (idsOfVoters.contains(requesterId)) { throw new IllegalArgumentException("You already voted during this song. Please wait until the next song."); }
        if (!getVotableSongUris().contains(uri)) { throw new IllegalArgumentException("The song you requested is not currently up for vote."); }
        Guest guest = getGuest(requesterId);
        idsOfVoters.add(requesterId);
        database.getPlayRequestDao().create(new PlayRequest(partySettings.raw(), guest, MusicService.SPOTIFY, uri, now()));
    }

    private Guest getGuest(String uniqueId) throws IllegalArgumentException, SQLException {
        Dao<Guest, Integer> dao = database.getGuestDao();
        List<Guest> guests = dao.queryForAll();
        for (Guest guest : guests) {
            if (guest.getUniqueId().equals(uniqueId) && !guest.isBanned()) {
                return guest;
            }
        }
        throw new IllegalArgumentException("You do not appear to be on the guest list.");
    }

    public void request(String uri, String requesterId) throws IllegalArgumentException, SQLException {
        if (uri == null || requesterId == null) { throw new IllegalArgumentException("Arguments cannot be null"); }

        // Check if user is allowed to make requests
        Guest guest = getGuest(requesterId);
        List<PlayRequest> requests = database.getPlayRequestDao().queryBuilder()
                .where().eq(PlayRequest.REQUESTER_COLUMN, guest)
                .and().eq(PlayRequest.PARTY_COLUMN, partySettings.raw())
                .query();
        int requestCount = 0;
        for(PlayRequest request : requests) {
            if (now().getTime() - partySettings.getCoinRefillMillis() <= request.getRequestTime().getTime()) {
                requestCount++;
            }
        }
        if (requestCount >= partySettings.getCoinAllowance()) { throw new IllegalArgumentException("You have run out of requests. Please wait for more."); }

        // Check blacklist
        if (!isValidSong(uri)) { throw new IllegalArgumentException("The party's filter has rejected your song request."); }

        // Make request
        database.getPlayRequestDao().create(new PlayRequest(partySettings.raw(), guest, MusicService.SPOTIFY, uri, now()));
    }

    private boolean isValidSong(String uri) throws SQLException {
        FilterMode filter = partySettings.getFilterMode() == null ? FilterMode.NONE : partySettings.getFilterMode();
        if (filter == FilterMode.NONE) { return true; }
        List<String> filterList = Observable    // AKA Blacklist/Whitelist
                .from(database.getSongFilterDao().queryBuilder()
                    .where().eq(SongFilter.PARTY_COLUMN, partySettings.raw())
                    .and().eq(SongFilter.FILTER_MODE_COLUMN, partySettings.getFilterMode())
                    .query())
                .map(new Func1<SongFilter, String>() {
                    @Override
                    public String call(SongFilter songFilter) {
                        return songFilter.getSongId();
                    }
                })
                .toList()
                .toBlocking()
                .first();
        if (filterList.contains(uri)) {
            if (filter == FilterMode.BLACK_LIST) { return false; }
        } else {
            if (filter == FilterMode.WHITE_LIST) { return false; }
        }
        return true;
    }
}

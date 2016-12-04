package com.musicocracy.fpgk.domain.dj;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import kaaes.spotify.webapi.android.models.Track;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class DjAlgorithm {
    private static final int BACKUP_SONG_COUNT = 10;
    private final Queue<String> backupSongs = new LinkedList<>();//EvictingQueue.create(BACKUP_SONG_COUNT); TODO: Implement Circular Queue
    private final Set<String> idsOfVoters = new HashSet<>();  // uniqueIds of guests that have voted for a song this iteration (you can vote for a new song once per song)
    private final Database database;
    private final PlayRequestRepository playRequestRepository;
    private final SongFilterRepository songFilterRepository;
    private final ReadOnlyPartySettings partySettings;

    public DjAlgorithm(Database database, PlayRequestRepository playRequestRepository, SongFilterRepository songFilterRepository, ReadOnlyPartySettings partySettings, final Browser browser) {
        if (database == null || playRequestRepository == null || songFilterRepository == null || partySettings == null) { throw new IllegalArgumentException("No dependencies may be null"); }
        this.database = database;
        this.playRequestRepository = playRequestRepository;
        this.songFilterRepository = songFilterRepository;
        this.partySettings = partySettings;
        Observable.from(DjAlgorithm.this.playRequestRepository.getMostRequestedSongIds(BACKUP_SONG_COUNT))
                .concatWith(
                        Observable.defer(new Func0<Observable<Track>>() {
                            @Override
                            public Observable<Track> call() {
                                return Observable.from(browser.getTopTracks(BACKUP_SONG_COUNT));
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .map(new Func1<Track, String>() {
                            @Override
                            public String call(Track track) {
                                return track.uri;
                            }
                        })
                )
                .take(BACKUP_SONG_COUNT)
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                @Override
                public void call(String uri) {
                    backupSongs.add(uri);
                }
            });
    }

    private Party getParty() throws SQLException {
        Party party;
        Dao<Party, Integer> dao = database.getPartyDao();
        try {
            party = loadPartyFromDatabase(dao);
        } catch (Exception e) {
            party = addPartyToDatabase(dao, partySettings);
        }
        return party;
    }

    private static Party loadPartyFromDatabase(Dao<Party, Integer> dao) throws SQLException, NoSuchElementException {
        List<Party> parties = dao.queryForAll();
        return Observable.from(parties)
                .filter(new Func1<Party, Boolean>() {
                    @Override
                    public Boolean call(Party party) {
                        return party.isHosting() && party.getEndTime() == null;
                    }
                })
                .reduce(new Func2<Party, Party, Party>() {
                    @Override
                    public Party call(Party lhs, Party rhs) {
                        if (lhs.getStartTime().compareTo(rhs.getStartTime()) > 0) {    // if lhs > rhs
                            return lhs;
                        } else {
                            return rhs;
                        }
                    }
                })
                .toBlocking()
                .first();
    }

    private static Party addPartyToDatabase(Dao<Party, Integer> dao, ReadOnlyPartySettings partySettings) throws SQLException {
        return dao.createIfNotExists(new Party(partySettings.getPartyName(), partySettings.getPartyCode(), now(), null, FilterMode.NONE, true));
    }

    private static Timestamp now() {
        return new Timestamp((int)System.currentTimeMillis());
    }

    public String dequeueNextSongUri() throws SQLException {
        // Get top voted song in database
        List<String> topUriList = playRequestRepository.getMostRequestedSongIds(1);
        if (topUriList.size() < 1) { return backupSongs.remove(); }
        String topUri = topUriList.get(0);

        // Delete requests for top voted song from database
        Dao<PlayRequest, Integer> dao = database.getPlayRequestDao();
        DeleteBuilder<PlayRequest, Integer> del = dao.deleteBuilder();
        del.where().eq(PlayRequest.SONG_ID_COL_NAME, topUri);
        del.delete();

        // Re-enable voting rights
        idsOfVoters.clear();
        return topUri;
    }

    public List<String> getVotableSongUris() throws SQLException {
        return playRequestRepository.getMostRequestedSongIds(4);
    }

    public void voteFor(String uri, String requesterId) throws IllegalArgumentException, SQLException {
        if (uri == null || requesterId == null) { throw new IllegalArgumentException("Arguments cannot be null"); }

        if (idsOfVoters.contains(requesterId)) { throw new IllegalArgumentException("You already voted during this song. Please wait until the next song."); }
        if (!getVotableSongUris().contains(uri)) { throw new IllegalArgumentException("The song you requested is not currently up for vote."); }
        Guest guest = getGuest(requesterId);
        idsOfVoters.add(requesterId);
        playRequestRepository.add(new PlayRequest(getParty(), guest, MusicService.SPOTIFY, uri, now()));
    }

    private Guest getGuest(String uniqueId) throws IllegalArgumentException, SQLException {
        Dao<Guest, Integer> dao = database.getGuestDao();
        List<Guest> guests = dao.queryForAll();
        for (Guest guest : guests) {
            if (guest.getUniqueId().equals(uniqueId)) {
                return guest;
            }
        }
        throw new IllegalArgumentException("You do not appear to be on the guest list.");
    }

    public void request(String uri, String requesterId) throws IllegalArgumentException, SQLException {
        if (uri == null || requesterId == null) { throw new IllegalArgumentException("Arguments cannot be null"); }

        // Check if user is allowed to make requests
        Guest guest = getGuest(requesterId);
        List<PlayRequest> requests = playRequestRepository.getRequestsMadeByGuest(guest);
        int requestCount = 0;
        for(PlayRequest request : requests) {
            if (now().getTime() - partySettings.getCoinRefillMillis() >= request.getRequestTime().getTime()) {
                requestCount++;
            }
        }
        if (requestCount >= partySettings.getCoinAllowance()) { throw new IllegalArgumentException("You have run out of requests. Please wait for more."); }

        // Check blacklist
        Party party = getParty();
        FilterMode filter = party.getFilterMode() == null ? FilterMode.NONE : party.getFilterMode();
        if (!songFilterRepository.isValidSongId(uri, filter)) { throw new IllegalArgumentException("The party's filter has rejected your song request."); }

        // Make request
        playRequestRepository.add(new PlayRequest(getParty(), guest, MusicService.SPOTIFY, uri, now()));
    }
}

package com.musicocracy.fpgk.domain.dj;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.PlayedVote;
import com.musicocracy.fpgk.domain.dal.SongFilter;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.PlayedVoteRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class DjAlgorithm {
    private final Set<String> idsOfVoters = new HashSet<>();  // uniqueIds of guests that have voted for a song this iteration (you can vote for a new song once per song)
    private final Database database;
    private final PlayRequestRepository playRequestRepository;
    private final SongFilterRepository songFilterRepository;
    private final PlayedVoteRepository playedVoteRepository;
    private final ReadOnlyPartySettings partySettings;

    public DjAlgorithm(Database database, PlayRequestRepository playRequestRepository, PlayedVoteRepository playedVoteRepository, SongFilterRepository songFilterRepository, ReadOnlyPartySettings partySettings) {
        if (database == null || playRequestRepository == null || playedVoteRepository == null || songFilterRepository == null || partySettings == null) { throw new IllegalArgumentException("No dependencies may be null"); }
        this.database = database;
        this.playRequestRepository = playRequestRepository;
        this.playedVoteRepository = playedVoteRepository;
        this.songFilterRepository = songFilterRepository;
        this.partySettings = partySettings;
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
        String topUri = playRequestRepository.getMostRequestedSongIds(1).get(0);
        PlayedVote playedVote = new PlayedVote(getParty(), MusicService.SPOTIFY, topUri, now());
        playedVoteRepository.add(playedVote);

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
        // Check if user is allowed to make requests
        Guest guest = getGuest(requesterId);
        List<PlayRequest> requests = playRequestRepository.getRequestsMadeByGuest(guest);
        int requestCount = 0;
        for(PlayRequest request : requests) {
            if (now().getTime() - partySettings.getTokenRefillMillis() >= request.getRequestTime().getTime()) {
                requestCount++;
            }
        }
        if (requestCount >= partySettings.getTokens()) { throw new IllegalArgumentException("You have run out of requests. Please wait for more."); }

        // Check blacklist
        Party party = getParty();
        FilterMode filter = party.getFilterMode() == null ? FilterMode.NONE : party.getFilterMode();
        if (songFilterRepository.isValidSongId(uri, filter)) {
            playRequestRepository.add(new PlayRequest(getParty(), guest, MusicService.SPOTIFY, uri, now()));
        } else {
            throw new IllegalArgumentException("The party's filter has rejected your song request.");
        }
    }
}

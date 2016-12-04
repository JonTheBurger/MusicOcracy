import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
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

import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class PlayRequestRepositoryTests {
    private Party party = new Party("MySweetParty", "#Party",fakeTimestamp(getHoursInMillis(5)), null, FilterMode.NONE, true);
    private Guest guest = new Guest(party, "Bob", "74:29:20:05:12", fakeTimestamp(getHoursInMillis(5)), false);
    private PlayRequest pr1 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Bicycle",  fakeTimestamp(getHoursInMillis(3)));
    private PlayRequest pr2 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Bicycle",  fakeTimestamp(getHoursInMillis(2)));
    private PlayRequest pr3 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Don't Stop Me Now", fakeTimestamp(getHoursInMillis(1)));
    private PlayRequest pr4 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Don't Stop Believing", now());
    private PlayRequest pr5 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Jump", now());
    private PlayedVote pv1 = new PlayedVote(party, MusicService.SPOTIFY, "Jump", fakeTimestamp(getHoursInMillis(2)));
    private PlayedVote pv2 = new PlayedVote(party, MusicService.SPOTIFY, "Don't Stop Believing", fakeTimestamp(getHoursInMillis(1) - 100000));

    private Database databaseMock = mock(Database.class);

    private PlayRequestRepository setUpMocks() throws SQLException {
        // Setup mocks
        System.out.println("\nEVENT: Initializing mocks...");
        Dao<PlayRequest, Integer> playRequestDaoMock = mock(Dao.class);
        Dao<PlayedVote, Integer> playedVoteDaoMock = mock(Dao.class);
        Dao<SongFilter,Integer> songFilterDaoMock = mock(Dao.class);
        QueryBuilder<PlayedVote, Integer> playedVoteQueryBuilderMock = mock(QueryBuilder.class);
        SongFilterRepository songFilterRepository = new SongFilterRepository(databaseMock);
        PlayedVoteRepository playedVoteRepository = mock(PlayedVoteRepository.class);
        System.out.println("SUCCESS: Mocks initialized.\n");

        // "Mock" for dao query
        List<PlayRequest> playRequestList = new ArrayList<>();
        playRequestList.add(pr1);
        playRequestList.add(pr2);
        playRequestList.add(pr3);
        playRequestList.add(pr5);

        List<PlayedVote> playedVoteList = new ArrayList<>();
        playedVoteList.add(pv1);
        playedVoteList.add(pv2);

        // Setup custom behaviour
        System.out.println("EVENT: Teaching custom mock behaviour...");
        when(databaseMock.getPlayRequestDao()).thenReturn(playRequestDaoMock);
        when(databaseMock.getPlayedVoteDao()).thenReturn(playedVoteDaoMock);
        when(databaseMock.getSongFilterDao()).thenReturn(songFilterDaoMock);
        when(playRequestDaoMock.queryForAll()).thenReturn(playRequestList);
        when(playedVoteDaoMock.queryForAll()).thenReturn(playedVoteList);
        when(songFilterDaoMock.queryForAll()).thenReturn(new ArrayList<SongFilter>());
        when(playedVoteRepository.getAllPlayedVotes()).thenReturn(playedVoteList);
        when(playedVoteRepository.getLatestTimestampOfPlayedVoteBySongId("Don't Stop Believing")).thenReturn(pv2.getVoteTime());
        when(playedVoteDaoMock.queryBuilder()).thenReturn(playedVoteQueryBuilderMock);
        when(playedVoteDaoMock.query(playedVoteQueryBuilderMock.prepare())).thenReturn(playedVoteList);
        when(playedVoteRepository.getMillisSincePlayedVoteSongId(pv2.getSongId())).thenReturn(now().getTime() - pv2.getVoteTime().getTime());
        when(playedVoteRepository.getMillisSincePlayedVoteSongId(pv1.getSongId())).thenReturn(now().getTime() - pv1.getVoteTime().getTime());
        System.out.println("SUCCESS: Custom mock behaviour taught.\n");

        // Setup class to be tested
        PlayRequestRepository playRequestRepository = new PlayRequestRepository(databaseMock, songFilterRepository, playedVoteRepository);
        return playRequestRepository;
    }

    private Timestamp now() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return now;
    }

    private Timestamp fakeTimestamp(long millisOffset) {
        Timestamp now = new Timestamp(System.currentTimeMillis() - millisOffset);
        return now;
    }

    private Timestamp hourBefore() {
        return fakeTimestamp(3600000);
    }

    private long getHoursInMillis(long hours) {
        return hours * 3600000;
    }

    @Test (expected=IllegalArgumentException.class)
    public void testRepositoryBlocksPlayedVotePlayRequest() throws SQLException {

        String fakeSongId = "Don't Stop Believing";
        long fakeDelayMillis = 3600000;

        PlayRequestRepository playRequestRepository = setUpMocks();


        // Perform test
        System.out.println("EVENT: Running test...");
        // Checks if played within last hour before adding.
        playRequestRepository.addWithDelay(pr4, fakeDelayMillis);
        // Exception thrown on add. Anything below does not get run.
        // Test passes because of expected key/value pair in decorator.
         assertFalse(playRequestRepository.getAllRequestedSongIds().contains(fakeSongId));
         System.out.println("SUCCESS: Test complete.");
    }

    @Test
    public void testRepositoryAllowsPlayedVotePlayRequest() throws SQLException {

        String fakeSongId = "Jump";
        long fakeDelayMillis = 3600000;

        PlayRequestRepository playRequestRepository = setUpMocks();


        // Perform test
        System.out.println("EVENT: Running test...");
        // Checks if played within last hour before adding.
        playRequestRepository.addWithDelay(pr5, fakeDelayMillis);
        // Exception thrown on add. Anything below does not get run.
        // Test passes because of expected key/value pair in decorator.
        assertTrue(playRequestRepository.getAllRequestedSongIds().contains(fakeSongId));
        System.out.println("SUCCESS: Test complete.");
    }

    @Test
    public void testRepositoryReturnsCorrectMostRequestList() throws SQLException {

        String fakeSongId = "Bicycle";

        PlayRequestRepository playRequestRepository = setUpMocks();

        // Perform test
        System.out.println("EVENT: Running test...");
        List<String> topRequestedPlayRequests = playRequestRepository.getMostRequestedSongIds(1);
        assertEquals(fakeSongId, topRequestedPlayRequests.get(0));
        System.out.println("SUCCESS: Test complete.");
    }

    @Test
    public void testRepositoryReturnsCorrectLeastRequestList() throws SQLException {

        String fakeSongId = "Don't Stop Me Now";

        PlayRequestRepository playRequestRepository = setUpMocks();

        // Perform test
        System.out.println("EVENT: Running test...");
        List<String> topRequestedPlayRequests = playRequestRepository.getLeastRequestedSongIds(1);
        assertEquals(fakeSongId, topRequestedPlayRequests.get(0));
        System.out.println("SUCCESS: Test complete.");
    }

    @Test
    public void testRepositoryReturnsCorrectNewestRequestList() throws SQLException {
        String fakeSongId = "Jump";

        PlayRequestRepository playRequestRepository = setUpMocks();

        System.out.println("EVENT: Running test...");
        List<String> topRequestedPlayRequests = playRequestRepository.getNewestRequestedSongIds(1);
        assertEquals(fakeSongId, topRequestedPlayRequests.get(0));
        System.out.println("SUCCESS: Test complete.");
    }

    @Test
    public void testRepositoryReturnsCorrectOldestRequestList() throws SQLException {
        String fakeSongId = "Bicycle";

        PlayRequestRepository playRequestRepository = setUpMocks();

        System.out.println("EVENT: Running test...");
        List<String> topRequestedPlayRequests = playRequestRepository.getOldestRequestedSongIds(1);
        assertEquals(fakeSongId, topRequestedPlayRequests.get(0));
        System.out.println("SUCCESS: Test complete.");
    }
}

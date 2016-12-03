import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.PlayedVote;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.PlayedVoteRepository;

import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class PlayRequestRepositoryTests {
    private Party party = new Party("MySweetParty", "#Party", new Timestamp((int)System.currentTimeMillis() - 1000000), null, FilterMode.NONE, true);
    private Guest guest = new Guest(party, "Bob", "74:29:20:05:12", new Timestamp((int)System.currentTimeMillis() - 10000), false);
    private PlayRequest pr1 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Don't Stop Me Now", new Timestamp((int)System.currentTimeMillis()));
    private PlayRequest pr2 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Bicycle",  new Timestamp((int)System.currentTimeMillis() - 25000));
    private PlayRequest pr3 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Bicycle",  new Timestamp((int)System.currentTimeMillis() - 10000));
    private PlayRequest pr4 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Don't Stop Believing",  new Timestamp((int)System.currentTimeMillis() - 100));
    private PlayedVote pv1 = new PlayedVote(party, MusicService.SPOTIFY, "Don't Stop Believing", new Timestamp((int)System.currentTimeMillis() - 15000));
    private PlayRequestRepository setUpMocks() throws SQLException {
        // Setup mocks
        System.out.println("\nEVENT: Initializing mocks...");
        Database databaseMock = mock(Database.class);
        Dao<PlayRequest, Integer> playRequestDaoMock = mock(Dao.class);
        Dao<PlayedVote, Integer> playedVoteDaoMock = mock(Dao.class);
        System.out.println("SUCCESS: Mocks initialized.\n");

        // "Mock" for dao query
        List<PlayRequest> playRequestList = new ArrayList<PlayRequest>();
        playRequestList.add(pr1);
        playRequestList.add(pr2);
        playRequestList.add(pr3);

        List<PlayedVote> playedVoteList = new ArrayList<>();
        playedVoteList.add(pv1);

        // Setup custom behaviour
        System.out.println("EVENT: Teaching custom mock behaviour...");
        when(databaseMock.getPlayRequestDao()).thenReturn(playRequestDaoMock);
        when(playRequestDaoMock.queryForAll()).thenReturn(playRequestList);
        when(databaseMock.getPlayedVoteDao()).thenReturn(playedVoteDaoMock);
        when(playedVoteDaoMock.queryForAll()).thenReturn(playedVoteList);
        System.out.println("SUCCESS: Custom mock behaviour taught.\n");

        // Setup class to be tested
        return new PlayRequestRepository(databaseMock);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRepositoryBlocksPlayedVotePlayRequest() throws SQLException {

        String fakeSongId = "Don't Stop Believing";

        PlayRequestRepository playRequestRepository = setUpMocks();

        // Perform test
        System.out.println("EVENT: Running test...");
        playRequestRepository.add(pr4);
        // Exception thrown on add. Anything below does not get run.
        // Test passes because of expected key/value pair in decorator.
         assertFalse(playRequestRepository.getAllRequestedSongIds().contains(fakeSongId));
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
        String fakeSongId = "Don't Stop Me Now";

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

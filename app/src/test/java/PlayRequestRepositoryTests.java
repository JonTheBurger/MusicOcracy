import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.model.dal.Database;
import com.musicocracy.fpgk.model.dal.FilterMode;
import com.musicocracy.fpgk.model.dal.Guest;
import com.musicocracy.fpgk.model.dal.MusicService;
import com.musicocracy.fpgk.model.dal.Party;
import com.musicocracy.fpgk.model.dal.PlayRequest;
import com.musicocracy.fpgk.model.query_layer.PlayRequestRepository;

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
    private PlayRequest pr2 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Bicycle",  new Timestamp((int)System.currentTimeMillis() - 2500));
    private PlayRequest pr3 = new PlayRequest(party, guest, MusicService.SPOTIFY, "Bicycle",  new Timestamp((int)System.currentTimeMillis() - 1));


    private PlayRequestRepository setUpMocks() throws SQLException {
        // Setup mocks
        System.out.println("\nEVENT: Initializing mocks...");
        Database databaseMock = mock(Database.class);
        Dao<PlayRequest, Integer> daoMock = mock(Dao.class);
        PlayRequest playRequestMock = mock(PlayRequest.class);
        System.out.println("SUCCESS: Mocks initialized.\n");

        // "Mock" for dao query
        List<PlayRequest> playRequestList = new ArrayList<PlayRequest>();
        playRequestList.add(pr1);
        playRequestList.add(pr2);
        playRequestList.add(pr3);

        // Setup custom behaviour
        System.out.println("EVENT: Teaching custom mock behaviour...");
        when(databaseMock.getPlayRequestDao()).thenReturn(daoMock);
        when(daoMock.queryForAll()).thenReturn(playRequestList);
        System.out.println("SUCCESS: Custom mock behaviour taught.\n");

        // Setup class to be tested
        return new PlayRequestRepository(databaseMock);
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

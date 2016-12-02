import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.SongFilter;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;

import org.junit.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class SongFilterRepositoryTests {
    private Party party = new Party("MySweetParty", "#Party", new Timestamp((int)System.currentTimeMillis() - 1000000), null, FilterMode.NONE, true);
    private Guest guest = new Guest(party, "Bob", "74:29:20:05:12", new Timestamp((int)System.currentTimeMillis() - 10000), false);
    private PlayRequest pr1 = new PlayRequest(party, guest, MusicService.SPOTIFY, "HAMSTERDANCE", new Timestamp((int)System.currentTimeMillis()));
    private PlayRequest pr2 = new PlayRequest(party, guest, MusicService.SPOTIFY, "BICYCLE", new Timestamp((int)System.currentTimeMillis()));
    private SongFilter sf1 = new SongFilter(MusicService.SPOTIFY, "Never Gonna Give You Up", party, FilterMode.BLACK_LIST);
    private SongFilter sf2 = new SongFilter(MusicService.SPOTIFY, "Hamsterdance",  party, FilterMode.BLACK_LIST);
    private SongFilter sf3 = new SongFilter(MusicService.SPOTIFY, "Bicycle",  party, FilterMode.WHITE_LIST);


    private SongFilterRepository setUpMocks() throws SQLException {
        // Setup mocks
        System.out.println("\nEVENT: Initializing mocks...");
        Database databaseMock = mock(Database.class);
        Dao<SongFilter, Integer> daoMock = mock(Dao.class);
        System.out.println("SUCCESS: Mocks initialized.\n");

        // "Mock" for dao query
        List<SongFilter> songFilterList = new ArrayList<>();
        songFilterList.add(sf1);
        songFilterList.add(sf2);
        songFilterList.add(sf3);

        // Setup custom behaviour
        System.out.println("EVENT: Teaching custom mock behaviour...");
        when(databaseMock.getSongFilterDao()).thenReturn(daoMock);
        when(daoMock.queryForAll()).thenReturn(songFilterList);
        System.out.println("SUCCESS: Custom mock behaviour taught.\n");

        // Setup class to be tested
        return new SongFilterRepository(databaseMock);
    }

    @Test
    public void testRepositoryBlocksInvalidSongId() throws SQLException {

        String fakeSongId = "Never Gonna Give You Up";

        SongFilterRepository songFilterRepository = setUpMocks();

        // Perform test
        System.out.println("EVENT: Running test...");
        assertFalse(songFilterRepository.isValidSongId(fakeSongId, FilterMode.BLACK_LIST));
        System.out.println("SUCCESS: Test complete.");
    }

    @Test
    public void testRepositoryBlocksInvalidPlayRequest() throws SQLException {

        SongFilterRepository songFilterRepository = setUpMocks();

        // Perform test
        System.out.println("EVENT: Running test...");
        assertFalse(songFilterRepository.isValidPlayRequest(pr1, FilterMode.BLACK_LIST));
        System.out.println("SUCCESS: Test complete.");
    }

    @Test
    public void testRepositoryAllowsValidSongId() throws SQLException {

        String fakeSongId = "Bicycle";

        SongFilterRepository songFilterRepository = setUpMocks();

        // Perform test
        System.out.println("EVENT: Running test...");
        boolean valid = songFilterRepository.isValidSongId(fakeSongId, FilterMode.WHITE_LIST);
        System.out.println(valid);
        assertTrue(valid);
        System.out.println("SUCCESS: Test complete.");
    }

    @Test
    public void testRepositoryAllowsValidPlayRequest() throws SQLException {

        SongFilterRepository songFilterRepository = setUpMocks();

        // Perform test
        System.out.println("EVENT: Running test...");
        assertTrue(songFilterRepository.isValidPlayRequest(pr2, FilterMode.WHITE_LIST));
        System.out.println("SUCCESS: Test complete.");
    }


}

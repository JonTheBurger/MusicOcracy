package domain.dj;

import android.support.test.runner.AndroidJUnit4;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;
import com.musicocracy.fpgk.domain.spotify.Browser;
import com.musicocracy.fpgk.domain.util.PartySettings;
import com.musicocracy.fpgk.domain.util.ReadOnlyPartySettings;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class DjAlgorithmTests {
    private static final Timestamp nowMinus(long offsetMillis) {
        return new Timestamp((int)System.currentTimeMillis() - offsetMillis);
    }

    // region Test Database objects
    private final Party oldParty1 = new Party("name", "pass", nowMinus(20000), nowMinus(18000), FilterMode.NONE, true);
    private final Party oldParty2 = new Party("name", "pass", nowMinus(17000), nowMinus(15000), FilterMode.NONE, true);
    private final Party oldBlackListParty = new Party("name", "pass", nowMinus(14000), nowMinus(11000), FilterMode.BLACK_LIST, true);
    private final Party oldWhiteListParty = new Party("name", "pass", nowMinus(10000), nowMinus(1000), FilterMode.WHITE_LIST, true);
    private final Party currentParty = new Party("name", "pass", nowMinus(0), null, FilterMode.NONE, true);
    private final Party currentBlackListParty = new Party("name", "pass", nowMinus(0), null, FilterMode.BLACK_LIST, true);
    private final Party currentWhiteListParty = new Party("name", "pass", nowMinus(0), null, FilterMode.WHITE_LIST, true);

    private final Guest validGuest = new Guest(currentParty, "name", "1", nowMinus(1000), false);
    private final Guest bannedGuest = new Guest(currentParty, "name", "2", nowMinus(1000), true);
    private final Guest oldGuest = new Guest(oldParty1, "name", "1", nowMinus(11000), false);

    private final PlayRequest oldRequest1Song1 = new PlayRequest(oldParty1, oldGuest, MusicService.SPOTIFY, "1", nowMinus(1200));
    private final PlayRequest oldRequest2Song1 = new PlayRequest(oldParty1, oldGuest, MusicService.SPOTIFY, "1", nowMinus(1100));
    private final PlayRequest oldRequest1Song2 = new PlayRequest(oldParty1, oldGuest, MusicService.SPOTIFY, "2", nowMinus(1200));

    private final PlayRequest newRequest1Song3 = new PlayRequest(currentParty, validGuest, MusicService.SPOTIFY, "3", nowMinus(120));
    private final PlayRequest newRequest2Song3 = new PlayRequest(currentParty, validGuest, MusicService.SPOTIFY, "3", nowMinus(110));
    private final PlayRequest newRequest3Song4 = new PlayRequest(currentParty, validGuest, MusicService.SPOTIFY, "4", nowMinus(100));
    private final PlayRequest newRequest4Song4 = new PlayRequest(currentParty, validGuest, MusicService.SPOTIFY, "4", nowMinus(90));
    private final PlayRequest newRequest5Song4 = new PlayRequest(currentParty, validGuest, MusicService.SPOTIFY, "4", nowMinus(80));
    // endregion Test Database objects

    // region Mock Factories
    private Database mockDb(List<Party> parties, List<Guest> guests, List<PlayRequest> playRequests) throws SQLException {
        Database db = mock(Database.class);
        when(db.getPartyDao()).thenReturn(mockDao(parties));
        when(db.getGuestDao()).thenReturn(mockDao(guests));
        when(db.getPlayRequestDao()).thenReturn(mockDao(playRequests));
        return db;
    }

    private <T> Dao<T, Integer> mockDao(List<T> entries) throws SQLException {
        Dao<T, Integer> dao = mock(Dao.class);
        when(dao.queryForAll()).thenReturn(entries);
        DeleteBuilder del = mock(DeleteBuilder.class);
        when(dao.deleteBuilder()).thenReturn(del);
        return dao;
    }

    private PlayRequestRepository mockPlayRequestRepo() {
        PlayRequestRepository playRepo = mock(PlayRequestRepository.class);
        return playRepo;
    }

    private SongFilterRepository mockFilterRepo() {
        SongFilterRepository filterRepo = mock(SongFilterRepository.class);
        return filterRepo;
    }

    private Browser mockBrowser() {
        Browser browser = mock(Browser.class);
        return browser;
    }

    ReadOnlyPartySettings mockSettings(int coins, long refillMillis) {
        return new PartySettings().setPartyName("name").setPartyCode("code").setSpotifyToken("token").setCoinAllowance(coins).setCoinRefillMillis(refillMillis);
    }
    // endregion Mock Factories

    // region dequeueNextSongUri
    @Test
    public void DjAlgorithm_dequeueNextSongUri_emptyDb_returnsBackupTrack() throws InterruptedException {

    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_emptyDbMultipleTries_returnsBackupTrackQueue() {

    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_dequeueTwo_givesTopTwo() {

    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_emptyDb_backupTrackQueueContainsPreviouslyRequestedUri() {

    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_voteBeforeAndAfter_reEnablesVoting() {

    }
    // endregion dequeueNextSongUri

    // region getVotableSongUris
    @Test
    public void DjAlgorithm_getVotableSongUris_returnsCorrect() {

    }

    @Test
    public void DjAlgorithm_getVotableSongUris_emptyDb_returnsEmptyList() {

    }
    // endregion getVotableSongUris

    // region voteFor
    @Test
    public void DjAlgorithm_voteFor_nullUri_throwsIllegalArgument() {

    }

    @Test
    public void DjAlgorithm_voteFor_nullRequesterId_throwsIllegalArgument() {

    }

    @Test
    public void DjAlgorithm_voteFor_requesterAlreadyVoted_throwsIllegalArgument() {

    }

    @Test
    public void DjAlgorithm_voteFor_uriNotVotable_throwsIllegalArgument() {

    }

    @Test
    public void DjAlgorithm_voteFor_requesterNotFound_throwsIllegalArgument() {

    }

    @Test
    public void DjAlgorithm_voteFor_validArgs_inserts() {

    }
    // endregion voteFor

    // region request
    @Test
    public void DjAlgorithm_request_validArgs_inserts() throws SQLException {
        Database db = mockDb(new ArrayList<Party>(), new ArrayList<Guest>(), new ArrayList<PlayRequest>());
        //DjAlgorithm dj = new DjAlgorithm(db, playRepo, filterRepo, settings, browser);
    }

    @Test
    public void DjAlgorithm_request_nullUri_throwsIllegalArgument() {

    }

    @Test
    public void DjAlgorithm_request_nullRequester_throwsIllegalArgument() {

    }

    @Test
    public void DjAlgorithm_request_requesterAtRequestLimit_throwsIllegalArgument() {

    }

    @Test
    public void DjAlgorithm_request_requesterAboveRequestLimit_throwsIllegalArgument() {

    }

    @Test
    public void DjAlgorithm_request_requesterOneBelowRequestLimit_success() {

    }

    @Test
    public void DjAlgorithm_request_requesterNotFound_throwsIllegalArgument() {

    }
    // endregion request
}

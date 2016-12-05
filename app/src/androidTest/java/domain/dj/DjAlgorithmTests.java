package domain.dj;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.SongFilter;
import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.query_layer.PlayRequestRepository;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;
import com.musicocracy.fpgk.domain.util.PartySettings;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class DjAlgorithmTests {
    // WARNING: Database values are generated with a stale party.
    private final Context context = InstrumentationRegistry.getTargetContext();

    // region Test objects
    private final Party oldParty1 = new Party("name", "pass", nowMinus(20000), nowMinus(18000), FilterMode.NONE, true);
    private final Party oldParty2 = new Party("name", "pass", nowMinus(17000), nowMinus(15000), FilterMode.NONE, true);
    private final Party oldBlackListParty = new Party("name", "pass", nowMinus(14000), nowMinus(11000), FilterMode.BLACK_LIST, true);
    private final Party oldWhiteListParty = new Party("name", "pass", nowMinus(10000), nowMinus(1000), FilterMode.WHITE_LIST, true);
    private final Party currentParty = new Party("name", "pass", nowMinus(0), null, FilterMode.NONE, true);
    private final Party currentBlackListParty = new Party("name", "pass", nowMinus(0), null, FilterMode.BLACK_LIST, true);
    private final Party currentWhiteListParty = new Party("name", "pass", nowMinus(0), null, FilterMode.WHITE_LIST, true);

    private final Guest validGuest = new Guest(currentParty, "name", "1", nowMinus(1000), false);
    private final Guest voteLimitedGuest = new Guest(currentParty, "name", "3", nowMinus(1000), false);
    private final Guest bannedGuest = new Guest(currentParty, "name", "2", nowMinus(1000), true);
    private final Guest oldGuest = new Guest(oldParty1, "name", "1", nowMinus(11000), false);

    private final PlayRequest oldRequest1Song1 = new PlayRequest(oldParty1, oldGuest, MusicService.SPOTIFY, "1", nowMinus(1200));
    private final PlayRequest oldRequest2Song1 = new PlayRequest(oldParty1, oldGuest, MusicService.SPOTIFY, "1", nowMinus(1100));
    private final PlayRequest oldRequest1Song2 = new PlayRequest(oldParty1, oldGuest, MusicService.SPOTIFY, "2", nowMinus(1200));

    private final PlayRequest newRequest1Song3 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "3", nowMinus(120));
    private final PlayRequest newRequest2Song3 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "3", nowMinus(110));
    private final PlayRequest newRequest3Song4 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "4", nowMinus(100));
    private final PlayRequest newRequest4Song4 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "4", nowMinus(90));
    private final PlayRequest newRequest5Song4 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "4", nowMinus(80));
    private final PlayRequest newRequest6Song5 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "5", nowMinus(70));
    private final PlayRequest newRequest7Song6 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "6", nowMinus(60));
    private final PlayRequest newRequest8Song7 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "7", nowMinus(50));
    private final PlayRequest newRequest9Song6 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "6", nowMinus(40));
    private final PlayRequest newRequest10Song7 = new PlayRequest(currentParty, voteLimitedGuest, MusicService.SPOTIFY, "7", nowMinus(30));

    private final SongFilter song6BlackFilter = new SongFilter(MusicService.SPOTIFY, "6", currentParty, FilterMode.BLACK_LIST);
    private final SongFilter song7WhiteFilter = new SongFilter(MusicService.SPOTIFY, "7", currentParty, FilterMode.WHITE_LIST);
    // endregion Test objects

    // region Mock Factories
    private Database mockDb(List<Party> parties, List<Guest> guests, List<PlayRequest> playRequests) throws SQLException {
        Database db = mock(Database.class);
        when(db.getPartyDao()).thenReturn(mockDao(parties));
        when(db.getGuestDao()).thenReturn(mockDao(guests));
        when(db.getPlayRequestDao()).thenReturn(mockDao(playRequests));
        return db;
    }

    private <T> Dao<T, Integer> mockDao(final List<T> entries) throws SQLException {
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
    // endregion Mock Factories

    // region Factories
    private static Timestamp nowMinus(long offsetMillis) {
        return new Timestamp(System.currentTimeMillis() - offsetMillis);
    }

    private Track trackWith(String uri) {
        Track track = new Track();
        track.uri = uri;
        return track;
    }

    private PartySettings createPartySettings(Database db, int coins, long refillMillis) throws SQLException {
        return new PartySettings(db.getPartyDao()).setPartyName("name").setPartyCode("code").setSpotifyToken("token").setCoinAllowance(coins).setCoinRefillMillis(refillMillis);
    }

    private static Guest guestFromExisting(Guest guest, Party dbParty) {
        return new Guest(dbParty, guest.getName(), guest.getUniqueId(), guest.getJoinTime(), guest.isBanned());
    }

    private static PlayRequest playRequestFromExisting(PlayRequest playRequest, Party dbParty, Guest dbGuest) {
        return new PlayRequest(dbParty, dbGuest, playRequest.getService(), playRequest.getSongId(), playRequest.getRequestTime());
    }

    private static SongFilter songFilterFromExisting(SongFilter filter, Party party) {
        return new SongFilter(filter.getService(), filter.getSongId(), party, filter.getFilterMode());
    }

    private Database dbWithCurrentData(FilterMode currentFilter) throws SQLException {
        Database db = Database.InMemory(context);
        Party party = currentParty;
        if (currentFilter == FilterMode.BLACK_LIST) {
            party = currentBlackListParty;
        } else if (currentFilter == FilterMode.WHITE_LIST) {
            party = currentWhiteListParty;
        }
        db.getPartyDao().create(party);
        db.getGuestDao().create(Arrays.asList(
                guestFromExisting(validGuest, party),
                guestFromExisting(bannedGuest, party)
        ));
        db.getGuestDao().create(guestFromExisting(voteLimitedGuest, party));
        db.getPlayRequestDao().create(Arrays.asList(
                playRequestFromExisting(newRequest1Song3, party, voteLimitedGuest),
                playRequestFromExisting(newRequest2Song3, party, voteLimitedGuest),
                playRequestFromExisting(newRequest3Song4, party, voteLimitedGuest),
                playRequestFromExisting(newRequest4Song4, party, voteLimitedGuest),
                playRequestFromExisting(newRequest5Song4, party, voteLimitedGuest),
                playRequestFromExisting(newRequest6Song5, party, voteLimitedGuest),
                playRequestFromExisting(newRequest7Song6, party, voteLimitedGuest),
                playRequestFromExisting(newRequest8Song7, party, voteLimitedGuest),
                playRequestFromExisting(newRequest9Song6, party, voteLimitedGuest),
                playRequestFromExisting(newRequest10Song7, party, voteLimitedGuest)
        ));
        db.getSongFilterDao().create(Arrays.asList(
                songFilterFromExisting(song6BlackFilter, party),
                songFilterFromExisting(song7WhiteFilter, party)
        ));
        return db;
    }

    private Database dbWithCurrentAndOldData(FilterMode currentFilter) throws SQLException {
        Database db = dbWithCurrentData(currentFilter);
        db.getPartyDao().create(Arrays.asList(
                oldParty2,
                oldBlackListParty,
                oldWhiteListParty
        ));
        db.getPartyDao().create(oldParty1);
        db.getGuestDao().create(guestFromExisting(oldGuest, oldParty1));
        db.getPlayRequestDao().create(Arrays.asList(
                playRequestFromExisting(oldRequest1Song1, oldParty1, oldGuest),
                playRequestFromExisting(oldRequest1Song2, oldParty1, oldGuest),
                playRequestFromExisting(oldRequest2Song1, oldParty1, oldGuest)
        ));
        return db;
    }
    // endregion Factories

    // region dequeueNextSongUri
    @Test
    public void DjAlgorithm_dequeueNextSongUri_emptyDb_returnsEmptyString() throws InterruptedException, SQLException {
        Database db = Database.InMemory(context);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        String song = dj.dequeueNextSongUri();

        assertEquals("", song);
    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_emptyDb_backupTrackQueueContainsPreviouslyRequestedUri() throws SQLException {
        Database db = dbWithCurrentAndOldData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        String uri = dj.dequeueNextSongUri();

        assertEquals("4", uri);
    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_voteBeforeAndAfter_reEnablesVoting() throws SQLException {
        Database db = dbWithCurrentAndOldData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));
        String song = dj.getVotableSongUris().get(0);
        dj.voteFor(song, validGuest.getUniqueId());
        dj.dequeueNextSongUri();
        song = dj.getVotableSongUris().get(0);

        dj.voteFor(song, validGuest.getUniqueId());
        // Pass if no throw
    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_returnsMaxVotes() throws SQLException {
        Database db = dbWithCurrentAndOldData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        String uri = dj.dequeueNextSongUri();

        assertEquals("4", uri);
    }
    // endregion dequeueNextSongUri

    // region getVotableSongUris
    @Test
    public void DjAlgorithm_getVotableSongUris_returnsCorrect() throws SQLException {
        Database db = Database.InMemory(context);
        PartySettings settings = createPartySettings(db, 3, 1000);
        Party party = settings.raw();
        db.getPartyDao().create(oldParty1);
        Guest guest = new Guest(party, "", "", nowMinus(0), false);
        db.getGuestDao().create(guest);
        PlayRequest oldRequest1Song1 = new PlayRequest(oldParty1, guest, MusicService.SPOTIFY, "1", nowMinus(1200));
        PlayRequest oldRequest2Song1 = new PlayRequest(oldParty1, guest, MusicService.SPOTIFY, "1", nowMinus(1100));
        PlayRequest oldRequest1Song2 = new PlayRequest(oldParty1, guest, MusicService.SPOTIFY, "2", nowMinus(1200));
        PlayRequest newRequest1Song3 = new PlayRequest(party, guest, MusicService.SPOTIFY, "3", nowMinus(120));
        PlayRequest newRequest2Song3 = new PlayRequest(party, guest, MusicService.SPOTIFY, "3", nowMinus(110));
        PlayRequest newRequest3Song4 = new PlayRequest(party, guest, MusicService.SPOTIFY, "4", nowMinus(100));
        PlayRequest newRequest4Song4 = new PlayRequest(party, guest, MusicService.SPOTIFY, "4", nowMinus(90));
        PlayRequest newRequest5Song4 = new PlayRequest(party, guest, MusicService.SPOTIFY, "4", nowMinus(80));
        PlayRequest newRequest6Song5 = new PlayRequest(party, guest, MusicService.SPOTIFY, "5", nowMinus(70));
        PlayRequest newRequest7Song6 = new PlayRequest(party, guest, MusicService.SPOTIFY, "6", nowMinus(60));
        PlayRequest newRequest8Song7 = new PlayRequest(party, guest, MusicService.SPOTIFY, "7", nowMinus(50));
        PlayRequest newRequest9Song6 = new PlayRequest(party, guest, MusicService.SPOTIFY, "6", nowMinus(40));
        PlayRequest newRequest10Song7 = new PlayRequest(party, guest, MusicService.SPOTIFY, "7", nowMinus(30));
        db.getPlayRequestDao().createOrUpdate(oldRequest1Song1);
        db.getPlayRequestDao().createOrUpdate(oldRequest2Song1);
        db.getPlayRequestDao().createOrUpdate(oldRequest1Song2);
        db.getPlayRequestDao().createOrUpdate(newRequest1Song3);
        db.getPlayRequestDao().createOrUpdate(newRequest2Song3);
        db.getPlayRequestDao().createOrUpdate(newRequest3Song4);
        db.getPlayRequestDao().createOrUpdate(newRequest4Song4);
        db.getPlayRequestDao().createOrUpdate(newRequest5Song4);
        db.getPlayRequestDao().createOrUpdate(newRequest6Song5);
        db.getPlayRequestDao().createOrUpdate(newRequest7Song6);
        db.getPlayRequestDao().createOrUpdate(newRequest8Song7);
        db.getPlayRequestDao().createOrUpdate(newRequest9Song6);
        db.getPlayRequestDao().createOrUpdate(newRequest10Song7);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), settings);
        List<String> unexpected = Arrays.asList("1", "2", "5");

        List<PlayRequest> all = db.getPlayRequestDao().queryForAll();

        List<String> votableSongs = dj.getVotableSongUris();

        for (int i = 0; i < votableSongs.size(); i++) {
            assertNotEquals(unexpected.get(i), votableSongs.get(i));
        }
    }

    @Test
    public void DjAlgorithm_getVotableSongUris_emptyDb_returnsEmptyList() throws SQLException {
        Database db = Database.InMemory(context);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        assertEquals(0, dj.getVotableSongUris().size());
    }
    // endregion getVotableSongUris

    // region voteFor
    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_nullUri_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);

        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));
        dj.voteFor(null, validGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_nullRequesterId_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        dj.voteFor(newRequest1Song3.getSongId(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_requesterAlreadyVoted_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        try {
            dj.voteFor(dj.getVotableSongUris().get(0), validGuest.getUniqueId());
        } catch (IllegalArgumentException e) {
            fail("Voting for a valid song once should not throw");
        }
        dj.voteFor(newRequest1Song3.getSongId(), validGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_uriNotVotable_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        dj.voteFor("56456465", validGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_requesterNotFound_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        dj.voteFor(newRequest1Song3.getSongId(), "68451313");
    }

    @Test
    public void DjAlgorithm_voteFor_validArgs_inserts() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));
        long count = db.getPlayRequestDao().countOf();

        dj.voteFor(dj.getVotableSongUris().get(0), validGuest.getUniqueId());

        assertEquals(count + 1, db.getPlayRequestDao().countOf());
    }
    // endregion voteFor

    // region request
    @Test
    public void DjAlgorithm_request_validArgs_inserts() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));
        long count = db.getPlayRequestDao().countOf();

        dj.request(newRequest1Song3.getSongId(), validGuest.getUniqueId());

        assertEquals(count + 1, db.getPlayRequestDao().countOf());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_nullUri_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        dj.request(null, validGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_nullRequester_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        dj.request(newRequest1Song3.getSongId(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_blacklistedSongRequest_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.BLACK_LIST);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        dj.request(song6BlackFilter.getSongId(), validGuest.getUniqueId());
    }

    @Test
    public void DjAlgorithm_request_blacklistValidRequest_inserted() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.BLACK_LIST);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));
        long count = db.getPlayRequestDao().countOf();

        dj.request(song7WhiteFilter.getSongId(), validGuest.getUniqueId());

        assertEquals(count + 1, db.getPlayRequestDao().countOf());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_whitelistedSongRequest_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.WHITE_LIST);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        dj.request(song6BlackFilter.getSongId(), validGuest.getUniqueId());
    }

    @Test
    public void DjAlgorithm_request_whitelistValidRequest_inserted() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.WHITE_LIST);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));
        long count = db.getPlayRequestDao().countOf();

        dj.request(song7WhiteFilter.getSongId(), validGuest.getUniqueId());

        assertEquals(count + 1, db.getPlayRequestDao().countOf());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_requesterAtRequestLimit_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        PartySettings settings = createPartySettings(db, 3, 1000000);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), settings);
        Guest guest = new Guest(settings.raw(), "a", "2435423", nowMinus(0), false);
        db.getGuestDao().create(guest);
        db.getPlayRequestDao().createOrUpdate(new PlayRequest(settings.raw(), guest, MusicService.SPOTIFY, "123", nowMinus(0)));
        db.getPlayRequestDao().createOrUpdate(new PlayRequest(settings.raw(), guest, MusicService.SPOTIFY, "123", nowMinus(0)));
        db.getPlayRequestDao().createOrUpdate(new PlayRequest(settings.raw(), guest, MusicService.SPOTIFY, "123", nowMinus(0)));

        dj.request(newRequest1Song3.getSongId(), guest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_bannedGuest_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        dj.request(newRequest1Song3.getSongId(), bannedGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_requesterNotFound_throwsIllegalArgument() throws SQLException {
        Database db = dbWithCurrentData(FilterMode.NONE);
        DjAlgorithm dj = new DjAlgorithm(db, new PlayRequestRepository(db), new SongFilterRepository(db), createPartySettings(db, 3, 1000));

        dj.request(newRequest1Song3.getSongId(),"89456");
    }
    // endregion request
}

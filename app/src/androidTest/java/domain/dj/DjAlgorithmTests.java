package domain.dj;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.musicocracy.fpgk.domain.dal.Database;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Guest;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.dal.PlayRequest;
import com.musicocracy.fpgk.domain.dal.SongFilter;
import com.musicocracy.fpgk.domain.dj.DjAlgorithm;
import com.musicocracy.fpgk.domain.util.PartySettings;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class DjAlgorithmTests {
    private final Context context = InstrumentationRegistry.getTargetContext();

    private Database db;
    private PartySettings partySettings;
    private Party party;
    private Guest validGuest;
    private Guest requestLimitedGuest;
    private Guest bannedGuest;
    private DjAlgorithm dj;

    private static Timestamp nowMinus(long offsetMillis) {
        return new Timestamp(System.currentTimeMillis() - offsetMillis);
    }

    private TestConfigurator newTest() {
        return new TestConfigurator();
    }

    private class TestConfigurator {
        TestConfigurator withDatabase() {
            db = Database.InMemory(context);
            return this;
        }

        TestConfigurator withOldData() throws SQLException {
            if (db == null) { throw new IllegalStateException("Database must be initialized before it is configured"); }

            Party p0 = new Party("p0", "pc0", nowMinus(20000), nowMinus(18000), FilterMode.NONE, true);
            Party p1 = new Party("p1", "pc1", nowMinus(17000), nowMinus(15000), FilterMode.NONE, true);
            db.getPartyDao().create(Arrays.asList(
                    p0, p1
            ));

            Guest g0 = new Guest(p0, "g0", "g0_id", nowMinus(19000), false);
            Guest g1 = new Guest(p0, "g1", "g1_id", nowMinus(18500), false);
            Guest g2 = new Guest(p1, "g2", "g2_id", nowMinus(16000), false);
            db.getGuestDao().create(Arrays.asList(
                    g0, g1, g2
            ));

            PlayRequest r0 = new PlayRequest(p0, g0, MusicService.SPOTIFY, "uri_1", nowMinus(18500));
            PlayRequest r1 = new PlayRequest(p0, g0, MusicService.SPOTIFY, "uri_1", nowMinus(18450));
            PlayRequest r2 = new PlayRequest(p1, g2, MusicService.SPOTIFY, "uri_2", nowMinus(15500));
            db.getPlayRequestDao().create(Arrays.asList(
                    r0, r1, r2
            ));

            return this;
        }

        TestConfigurator startNewParty() throws SQLException {
            partySettings = new PartySettings(db.getPartyDao()).setPartyName("P0").setPartyCode("PC0").setCoinAllowance(3).setCoinRefillMillis(1000000);
            party = partySettings.raw();

            validGuest = new Guest(party, "G0", "G0_ID", nowMinus(0), false);
            db.getGuestDao().create(validGuest);

            return this;
        }

        TestConfigurator withNewData() throws SQLException {
            requestLimitedGuest = new Guest(party, "G1", "G1_ID", nowMinus(0), false);
            bannedGuest = new Guest(party, "G2", "G2_ID", nowMinus(0), true);
            db.getGuestDao().create(Arrays.asList(
                    requestLimitedGuest, bannedGuest
            ));

            PlayRequest r0 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_3", nowMinus(0));
            PlayRequest r1 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_3", nowMinus(0));
            PlayRequest r2 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_4", nowMinus(0));
            PlayRequest r3 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_4", nowMinus(0));
            PlayRequest r4 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_4", nowMinus(0));
            PlayRequest r5 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_5", nowMinus(0));
            PlayRequest r6 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_6", nowMinus(0));
            PlayRequest r7 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_6", nowMinus(0));
            PlayRequest r8 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_7", nowMinus(0));
            PlayRequest r9 = new PlayRequest(party, requestLimitedGuest, MusicService.SPOTIFY, "URI_7", nowMinus(0));
            db.getPlayRequestDao().create(Arrays.asList(
                    r0, r1, r2, r3, r4, r5, r6, r7, r8, r9
            ));

            return this;
        }

        TestConfigurator addFilter(FilterMode filterMode) throws SQLException {
            if (partySettings == null) { throw new IllegalStateException("New party must be started before adding a filter"); }

            partySettings.setFilterMode(filterMode);

            if (filterMode == FilterMode.BLACK_LIST) {
                db.getSongFilterDao().create(new SongFilter(MusicService.SPOTIFY, "URI_777", party, FilterMode.BLACK_LIST));
            } else if (filterMode == FilterMode.WHITE_LIST) {
                db.getSongFilterDao().create(new SongFilter(MusicService.SPOTIFY, "URI_777", party, FilterMode.WHITE_LIST));
            }

            return this;
        }

        void build() {
            dj = new DjAlgorithm(db, partySettings);
        }
    }

    // region dequeueNextSongUri
    @Test
    public void DjAlgorithm_dequeueNextSongUri_emptyDb_returnsEmptyString() throws InterruptedException, SQLException {
        newTest().withDatabase().startNewParty().build();

        String song = dj.dequeueNextSongUri();

        assertEquals("", song);
    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_oldDb_backupTrackQueueContainsPreviouslyRequestedUri() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().build();

        String uri = dj.dequeueNextSongUri();

        assertEquals("uri_1", uri);
    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_voteBeforeAndAfter_reEnablesVoting() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        String song = dj.getVotableSongUris().get(0);
        dj.voteFor(song, validGuest.getUniqueId());

        dj.dequeueNextSongUri();

        song = dj.getVotableSongUris().get(0);
        dj.voteFor(song, validGuest.getUniqueId());
        // Pass if no throw
    }

    @Test
    public void DjAlgorithm_dequeueNextSongUri_returnsMaxVotes() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        String uri = dj.dequeueNextSongUri();

        assertEquals("URI_4", uri);
    }
    // endregion dequeueNextSongUri

    // region getVotableSongUris
    @Test
    public void DjAlgorithm_getVotableSongUris_returnsCorrect() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();
        List<String> expected = Arrays.asList("URI_4", "URI_3", "URI_6", "URI_7");

        List<String> votableSongs = dj.getVotableSongUris();

        assertTrue(expected.containsAll(votableSongs));
    }

    @Test
    public void DjAlgorithm_getVotableSongUris_emptyDb_returnsEmptyList() throws SQLException {
        newTest().withDatabase().startNewParty().build();

        assertEquals(0, dj.getVotableSongUris().size());
    }
    // endregion getVotableSongUris

    // region voteFor
    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_nullUri_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        dj.voteFor(null, validGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_nullRequesterId_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        dj.voteFor(dj.getVotableSongUris().get(0), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_requesterAlreadyVoted_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        try {
            dj.voteFor(dj.getVotableSongUris().get(0), validGuest.getUniqueId());
        } catch (IllegalArgumentException e) {
            fail("Voting for a valid song once should not throw");
        }
        dj.voteFor(dj.getVotableSongUris().get(0), validGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_uriNotVotable_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        dj.voteFor("56456465", validGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_voteFor_requesterNotFound_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        dj.voteFor(dj.getVotableSongUris().get(0), "68451313");
    }

    @Test
    public void DjAlgorithm_voteFor_validArgs_inserts() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();
        long count = db.getPlayRequestDao().countOf();

        dj.voteFor(dj.getVotableSongUris().get(0), validGuest.getUniqueId());

        assertEquals(count + 1, db.getPlayRequestDao().countOf());
    }
    // endregion voteFor

    // region request
    @Test
    public void DjAlgorithm_request_validArgs_inserts() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();
        long count = db.getPlayRequestDao().countOf();

        dj.request("URI_100", validGuest.getUniqueId());

        assertEquals(count + 1, db.getPlayRequestDao().countOf());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_nullUri_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        dj.request(null, validGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_nullRequester_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        dj.request(dj.getVotableSongUris().get(0), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_blacklistedSongRequest_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().addFilter(FilterMode.BLACK_LIST).build();

        dj.request("URI_777", validGuest.getUniqueId());
    }

    @Test
    public void DjAlgorithm_request_blacklistValidRequest_inserted() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().addFilter(FilterMode.BLACK_LIST).build();
        long count = db.getPlayRequestDao().countOf();

        dj.request("URI_100", validGuest.getUniqueId());

        assertEquals(count + 1, db.getPlayRequestDao().countOf());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_whitelistedSongRequest_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().addFilter(FilterMode.WHITE_LIST).build();

        dj.request("URI_100", validGuest.getUniqueId());
    }

    @Test
    public void DjAlgorithm_request_whitelistValidRequest_inserted() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().addFilter(FilterMode.WHITE_LIST).build();
        long count = db.getPlayRequestDao().countOf();

        dj.request("URI_777", validGuest.getUniqueId());

        assertEquals(count + 1, db.getPlayRequestDao().countOf());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_requesterAtRequestLimit_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        dj.request("URI_100", requestLimitedGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_bannedGuest_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        dj.request("URI_100", bannedGuest.getUniqueId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void DjAlgorithm_request_requesterNotFound_throwsIllegalArgument() throws SQLException {
        newTest().withDatabase().withOldData().startNewParty().withNewData().build();

        dj.request("URI_100", "68451313");
    }
    // endregion request
}

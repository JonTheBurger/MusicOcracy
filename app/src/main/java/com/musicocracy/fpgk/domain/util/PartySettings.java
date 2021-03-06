package com.musicocracy.fpgk.domain.util;

import com.j256.ormlite.dao.Dao;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.Party;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PartySettings implements ReadOnlyPartySettings {
    private static final String TAG = "PartySettings";
    private final Dao<Party, Integer> dao;
    private final Logger log;
    private Observable<Party> insertObservable;
    private final Party persistent = new Party("", "", new Timestamp(System.currentTimeMillis()), null, FilterMode.NONE, true);
    private String SpotifyToken = "";
    private int coins;
    private long coinRefillMillis;

    public PartySettings() {
        this(null, new NullLogger()); // Disable persistent database storage
    }

    public PartySettings(final Dao<Party, Integer> dao, Logger log) {
        this.dao = dao;
        this.log = log;
        if (dao != null) {
            this.insertObservable = Observable.just(persistent)
                    .doOnNext(new Action1<Party>() {
                        @Override
                        public void call(Party party) {
                            try {
                                List<Party> invalidParties = dao.queryBuilder().where().isNull(Party.END_TIME_COLUMN).query();
                                for (Party p : invalidParties) {        // For each party that has not ended
                                    p.setEndTime(party.getStartTime()); // End it now.
                                    dao.update(p);
                                }
                                dao.createOrUpdate(party);
                            } catch (SQLException e) {
                                e.printStackTrace();    // TODO: Handle
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io());
        }
    }

    public Party raw() {
        if (dao != null) {
            insertObservable.toBlocking().firstOrDefault(null);
        }
        return persistent;
    }

    private AtomicBoolean isUpdating = new AtomicBoolean(false);
    private AtomicBoolean updateRequested = new AtomicBoolean(false);
    private void updateAsync() {
        if (dao == null) { return; }
        if (!isUpdating.getAndSet(true)) {
            do {
                Observable.just(persistent)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Action1<Party>() {
                            @Override
                            public void call(Party party) {
                                try {
                                    dao.update(party);
                                } catch (SQLException e) {
                                    log.warning(TAG, e.toString());
                                }
                            }
                        });
            } while(updateRequested.getAndSet(false));
            isUpdating.set(false);
        } else {
            updateRequested.set(true);
        }
    }

    @Override
    public int dbId() {
        return persistent.getId();
    }

    @Override
    public String getPartyCode() {
        return persistent.getPassword();
    }

    public PartySettings setPartyCode(String code) {
        persistent.setPassword(code);
        updateAsync();
        return this;
    }

    @Override
    public String getPartyName() {
        return persistent.getName();
    }

    public PartySettings setPartyName(String partyName) {
        persistent.setName(partyName);
        updateAsync();
        return this;
    }

    @Override
    public FilterMode getFilterMode() {
        return persistent.getFilterMode();
    }

    public PartySettings setFilterMode(FilterMode filterMode) {
        persistent.setFilterMode(filterMode);
        updateAsync();
        return this;
    }

    @Override
    public boolean isHosting() {
        return persistent.isHosting();
    }

    public PartySettings setIsHosting(boolean isHosting) {
        persistent.setIsHosting(isHosting);
        updateAsync();
        return this;
    }

    @Override
    public int getCoinAllowance() {
        return coins;
    }

    public PartySettings setCoinAllowance(int coins) {
        this.coins = coins;
        return this;
    }

    @Override
    public long getCoinRefillMillis() {
        return coinRefillMillis;
    }

    public PartySettings setCoinRefillMillis(long coinRefillMillis) {
        this.coinRefillMillis = coinRefillMillis;
        return this;
    }

    @Override
    public String getSpotifyToken() {
        return SpotifyToken;
    }

    public PartySettings setSpotifyToken(String token) {
        this.SpotifyToken = token;
        return this;
    }
}

package com.musicocracy.fpgk.domain.util;

import java.sql.Timestamp;

public class Timestamper {
    public Timestamper() {}

    public Timestamp now() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return now;
    }

    public Timestamp fakeTimestamp(long millisOffset) {
        Timestamp now = new Timestamp(System.currentTimeMillis() - millisOffset);
        return now;
    }

    public Timestamp hourBefore() {
        return fakeTimestamp(3600000);
    }

    public long getHoursInMillis(long hours) {
        return hours * 3600000;
    }

    public long getMillisSinceTimestamp(Timestamp timestamp) {
        long timestampMillis = timestamp.getTime();
        long currTimeMillis = now().getTime();
        return (currTimeMillis - timestampMillis);
    }
}

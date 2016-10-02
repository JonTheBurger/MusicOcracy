package com.musicocracy.fpgk.model.dal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "Guest")
public class Guest {
    Guest() {}   // OrmLite requires a default constructor

    public Guest(Party party, String name, String uniqueId, Timestamp joinTime, boolean isBanned) {
        this.party = party;
        this.name = name;
        this.uniqueId = uniqueId;
        this.joinTime = joinTime;
        this.isBanned = isBanned;
    }

    public int getId() {
        return id;
    }

    public Party getParty() {
        return party;
    }

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Timestamp getJoinTime() {
        return joinTime;
    }

    public boolean isBanned() {
        return isBanned;
    }

    @DatabaseField(generatedId = true) private int id;
    @DatabaseField(foreign = true) private Party party;
    @DatabaseField private String name;
    @DatabaseField private String uniqueId;
    @DatabaseField private Timestamp joinTime;
    @DatabaseField private boolean isBanned;
}

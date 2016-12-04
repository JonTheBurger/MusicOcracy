package com.musicocracy.fpgk.domain.dal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "Party")
public class Party {
    Party() {}    // OrmLite requires a default constructor

    public Party(String name, String password, Timestamp startTime, Timestamp endTime, FilterMode filterMode, boolean isHosting) {
        this.name = name;
        this.password = password;
        this.startTime = startTime;
        this.endTime = endTime;
        this.filterMode = filterMode;
        this.isHosting = isHosting;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public boolean isHosting() {
        return isHosting;
    }

    @Override
    public String toString() {
        return getId() + ": Party: " + getName() + ", PartyId: "  + getPassword() + ", Started at " + getStartTime().toString();
    }

    @DatabaseField(generatedId = true) private int id;
    @DatabaseField private String name;
    @DatabaseField private String password;
    @DatabaseField private Timestamp startTime;
    @DatabaseField private Timestamp endTime;
    @DatabaseField private FilterMode filterMode;
    @DatabaseField private boolean isHosting;
}

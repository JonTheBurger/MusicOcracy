package com.musicocracy.fpgk.domain.dal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "Party")
public class Party {
    public static final String END_TIME_COLUMN = "endTime";
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

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    public boolean isHosting() {
        return isHosting;
    }

    public void setIsHosting(boolean isHosting) {
        this.isHosting = isHosting;
    }

    @DatabaseField(generatedId = true) private int id;
    @DatabaseField private String name;
    @DatabaseField private String password;
    @DatabaseField private Timestamp startTime;
    @DatabaseField(columnName = END_TIME_COLUMN) private Timestamp endTime;
    @DatabaseField private FilterMode filterMode;
    @DatabaseField private boolean isHosting;
}

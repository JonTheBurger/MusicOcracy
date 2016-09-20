package com.musicocracy.fpgk.model.dal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;

@DatabaseTable(tableName = "Note")
public class Note {
    // OrmLite requires default constructor
    public Note() {}

    public Note(String title, String description, Date created) {
        this.title = title;
        this.description = description;
        this.dateCreated = created;
    }

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String description;

    @DatabaseField
    private Date dateCreated;
}

package com.musicocracy.fpgk.model.dal;

import com.j256.ormlite.field.DatabaseField;

public enum FilterMode {
    @DatabaseField(unknownEnumName = "NONE")
    NONE,
    BLACK_LIST,
    WHITE_LIST,
}

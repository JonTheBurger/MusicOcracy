package com.musicocracy.fpgk.model.dal;

import com.j256.ormlite.field.DatabaseField;

public enum MusicService {
    @DatabaseField(unknownEnumName = "UNKNOWN")
    UNKNOWN,
    SPOTIFY,
    GOOGLE_PLAY,
    LOCAL_FILE,
}

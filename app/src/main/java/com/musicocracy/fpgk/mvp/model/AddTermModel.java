package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.domain.dal.Party;
import com.musicocracy.fpgk.domain.dal.SongFilter;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;

public class AddTermModel {
    private SongFilterRepository songFilterRepository;

    public AddTermModel(SongFilterRepository songFilterRepository) {
        this.songFilterRepository = songFilterRepository;
    }

    public void addSongFilter(MusicService service, String songId, Party party, FilterMode filterMode) {
        SongFilter songFilter = new SongFilter(service, songId, party, filterMode);
        songFilterRepository.add(songFilter);
    }
}

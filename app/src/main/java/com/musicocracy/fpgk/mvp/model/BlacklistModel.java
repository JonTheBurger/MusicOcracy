package com.musicocracy.fpgk.mvp.model;

import com.musicocracy.fpgk.domain.dal.SongFilter;
import com.musicocracy.fpgk.domain.query_layer.SongFilterRepository;

import java.util.ArrayList;
import java.util.List;

public class BlacklistModel {
    private SongFilterRepository songFilterRepository;
    public BlacklistModel(SongFilterRepository songFilterRepository) {
        this.songFilterRepository = songFilterRepository;
    }

    public List<SongFilter> getAllBlacklistedSongFilters() {
        return songFilterRepository.getAllBlacklistedSongFilters();
    }

    public List<String> getAllBlacklistedSongIds() {
        List<String> songIdList = new ArrayList<>();
        List<SongFilter> songFilterList = getAllBlacklistedSongFilters();
        for(SongFilter songFilter : songFilterList) {
            songIdList.add(songFilter.getSongId());
        }
        return songIdList;
    }
}

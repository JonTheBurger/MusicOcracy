package com.musicocracy.fpgk.mvp.view;

import java.util.List;

public interface SongSelectView {
    void updateBrowseSongs(List<String> songs);
    void updateVotableSongs(List<String> songs);
}

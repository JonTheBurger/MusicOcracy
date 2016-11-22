package com.musicocracy.fpgk.view;

import java.util.List;

public interface NowPlayingView {
    void updateArtist(String artistName);
    void updateSong(String songName);
    void updateVotableSongs(List<String> votableSongs);
}

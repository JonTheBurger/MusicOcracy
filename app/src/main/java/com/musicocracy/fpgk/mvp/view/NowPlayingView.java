package com.musicocracy.fpgk.mvp.view;

import java.util.List;

public interface NowPlayingView {
    void updateArtist(String artistName);
    void updateSong(String songName);
    void updateVotableSongs(List<String> votableSongs);
    void updatePartyCode(String partyCode);
    void updatePartyName(String partyName);
}

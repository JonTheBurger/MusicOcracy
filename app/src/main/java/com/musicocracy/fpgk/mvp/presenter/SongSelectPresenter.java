package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.mvp.model.SongSelectModel;
import com.musicocracy.fpgk.mvp.view.SongSelectView;

import java.util.ArrayList;

public class SongSelectPresenter implements Presenter<SongSelectView> {
    private final SongSelectModel model;
    private SongSelectView view;

    public SongSelectPresenter(SongSelectModel model) {
        this.model = model;
    }

    public void populateBrowseSongs(String requestString) {
                

        ArrayList<String> testList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testList.add(requestString + " " + i);
        }
        view.updateSongs(testList);
    }

    public void populateVoteSongs() {
        ArrayList<String> testList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            testList.add("vote song " + i);
        }
        view.updateSongs(testList);
    }

    @Override
    public void setView(SongSelectView view) {
        this.view = view;
    }
}

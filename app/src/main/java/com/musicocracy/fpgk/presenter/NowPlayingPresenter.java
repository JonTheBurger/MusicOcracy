package com.musicocracy.fpgk.presenter;

import com.musicocracy.fpgk.model.NowPlayingModel;
import com.musicocracy.fpgk.view.NowPlayingView;

public class NowPlayingPresenter implements Presenter<NowPlayingView> {
    private final NowPlayingModel model;
    private NowPlayingView view;

    public NowPlayingPresenter(NowPlayingModel model) {
        this.model = model;
    }

    @Override
    public void setView(NowPlayingView view) {
        this.view = view;
    }
}

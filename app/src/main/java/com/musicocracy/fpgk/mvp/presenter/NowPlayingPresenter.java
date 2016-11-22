package com.musicocracy.fpgk.mvp.presenter;

import com.musicocracy.fpgk.mvp.model.NowPlayingModel;
import com.musicocracy.fpgk.mvp.view.NowPlayingView;

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

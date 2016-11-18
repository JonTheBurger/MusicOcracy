package com.musicocracy.fpgk.ui;

import android.os.Bundle;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.presenter.Presenter;
import com.musicocracy.fpgk.presenter.SongSelectPresenter;
import com.musicocracy.fpgk.view.SongSelectView;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class SongSelectActivity extends ActivityBase<SongSelectView> implements SongSelectView {
    private static final String TAG = "SongSelectActivity";
    @Inject SongSelectPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_song_select, this);
    }

    @Override
    protected Presenter<SongSelectView> getPresenter() {
        return presenter;
    }

    @Override
    protected void butterKnifeBind() {
        ButterKnife.bind(this);
    }

    @Override
    protected void daggerInject() {
        CyberJukeboxApplication.getComponent(this).inject(this);
    }
}

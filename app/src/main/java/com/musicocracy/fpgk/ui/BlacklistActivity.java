package com.musicocracy.fpgk.ui;

import android.os.Bundle;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.presenter.BlacklistPresenter;
import com.musicocracy.fpgk.presenter.Presenter;
import com.musicocracy.fpgk.view.BlacklistView;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class BlacklistActivity extends ActivityBase<BlacklistView> implements BlacklistView {
    private static final String TAG = "BlacklistActivity";
    @Inject BlacklistPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_blacklist, this);
    }

    @Override
    protected Presenter<BlacklistView> getPresenter() {
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

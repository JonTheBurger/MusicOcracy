package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.presenter.SelectionPresenter;
import com.musicocracy.fpgk.mvp.view.SelectionView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectionActivity extends ActivityBase<SelectionView> implements SelectionView {
    private static final String TAG = "SelectionActivity";
    @Inject SelectionPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_selection, this);
    }

    @OnClick(R.id.hostButton)
    public void hostClick() {
        Intent intent = new Intent(this, PartyConfigActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.guestButton)
    public void guestClick() {
        Intent intent = new Intent(this, ConnectActivity.class);
        startActivity(intent);
    }

    private int debugClicks = 0;
    @OnClick(R.id.selectWelcomeTextView)
    public void welcomeClick() {
        debugClicks++;
        if (debugClicks >= 3) {
            Intent intent = new Intent(this, TestMainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected Presenter<SelectionView> getPresenter() {
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

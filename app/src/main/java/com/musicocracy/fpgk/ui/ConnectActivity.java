package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.presenter.ConnectPresenter;
import com.musicocracy.fpgk.presenter.Presenter;
import com.musicocracy.fpgk.view.ConnectView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConnectActivity extends ActivityBase<ConnectView> implements ConnectView {
    private static final String TAG = "ConnectActivity";
    @Inject ConnectPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_connect, this);
    }

    @OnClick(R.id.connectBackButton)
    public void backClick() {
        onBackPressed();
    }

    @OnClick(R.id.connectForwardButton)
    public void forwardClick() {
        // TODO: Validate
        Intent intent = new Intent(ConnectActivity.this, RequestActivity.class);
        ConnectActivity.this.startActivity(intent);
    }

    @Override
    protected Presenter<ConnectView> getPresenter() {
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

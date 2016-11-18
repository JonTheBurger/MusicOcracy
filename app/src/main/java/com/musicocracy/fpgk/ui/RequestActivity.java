package com.musicocracy.fpgk.ui;

import android.os.Bundle;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.presenter.Presenter;
import com.musicocracy.fpgk.presenter.RequestPresenter;
import com.musicocracy.fpgk.view.RequestView;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class RequestActivity extends ActivityBase<RequestView> implements RequestView {
    private static final String TAG = "RequestActivity";
    @Inject RequestPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_request, this);
    }

    @Override
    protected Presenter<RequestView> getPresenter() {
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

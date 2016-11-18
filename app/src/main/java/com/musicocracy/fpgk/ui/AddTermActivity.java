package com.musicocracy.fpgk.ui;

import android.os.Bundle;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.presenter.AddTermPresenter;
import com.musicocracy.fpgk.presenter.Presenter;
import com.musicocracy.fpgk.view.AddTermView;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class AddTermActivity extends ActivityBase<AddTermView> implements AddTermView {
    private static final String TAG = "AddTermActivity";
    @Inject AddTermPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_add_term, this);
    }

    @Override
    protected Presenter<AddTermView> getPresenter() {
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

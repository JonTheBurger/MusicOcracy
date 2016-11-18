package com.musicocracy.fpgk.ui;

import android.os.Bundle;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.presenter.Presenter;
import com.musicocracy.fpgk.presenter.SelectionPresenter;
import com.musicocracy.fpgk.view.SelectionView;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class SelectionActivity extends ActivityBase<SelectionView> implements SelectionView {
    private static final String TAG = "SelectionActivity";
    @Inject SelectionPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_selection, this);
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

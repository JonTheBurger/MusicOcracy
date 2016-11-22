package com.musicocracy.fpgk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.musicocracy.fpgk.mvp.presenter.Presenter;

public abstract class ActivityBase<TView> extends AppCompatActivity {
    protected abstract Presenter<TView> getPresenter();
    protected abstract void butterKnifeBind();
    protected abstract void daggerInject();
    private boolean isCreated = false;

    protected void onCreate(Bundle savedInstanceState, int layout, TView view) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        daggerInject();
        butterKnifeBind();
        getPresenter().setView(view);
        isCreated = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isCreated) {
            throw new IllegalStateException("ERROR: ActivityBase.onCreate(Bundle savedInstanceState, int layout, TView view) was not called!");
        }
    }
}

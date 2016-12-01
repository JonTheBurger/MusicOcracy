package com.musicocracy.fpgk.ui;

import android.os.Bundle;
import android.widget.EditText;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.mvp.presenter.AddTermPresenter;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.view.AddTermView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTermActivity extends ActivityBase<AddTermView> implements AddTermView {
    private static final String TAG = "AddTermActivity";
    @Inject AddTermPresenter presenter;

    @BindView(R.id.add_term_text) EditText addTermText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_add_term, this);
    }

    @OnClick(R.id.use_list_button)
    public void useListClick() {
        // TODO: Create base list
    }

    @OnClick(R.id.add_term_back_button)
    public void backClick() {
        onBackPressed();
    }

    @OnClick(R.id.add_term_forward_button)
    public void forwardClick() {
        String newTerm = addTermText.getText().toString().trim();
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

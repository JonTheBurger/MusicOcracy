package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.domain.dal.FilterMode;
import com.musicocracy.fpgk.domain.dal.MusicService;
import com.musicocracy.fpgk.mvp.presenter.AddTermPresenter;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.view.AddTermView;

import java.util.ArrayList;
import java.util.List;

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
        Intent intent = new Intent(this, BlacklistActivity.class);
        List<String> defaultBlacklistedSongIds = new ArrayList<>();
        defaultBlacklistedSongIds.add("Never Gonna Give You Up");
        defaultBlacklistedSongIds.add("Call Me Maybe");
        defaultBlacklistedSongIds.add("Gangnam Style");

        for(String songId : defaultBlacklistedSongIds) {
            addSongFilter(songId);
        }

        AddTermActivity.this.startActivity(intent);
    }

    @OnClick(R.id.add_term_back_button)
    public void backClick() {
        onBackPressed();
    }

    @OnClick(R.id.add_term_forward_button)
    public void forwardClick() {
        Intent intent = new Intent(this, BlacklistActivity.class);
        String addTermString = getAddTermString();
        addSongFilter(addTermString);

        AddTermActivity.this.startActivity(intent);
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

    @Override
    public void addSongFilter(String songId) {
        presenter.addSongFilter(MusicService.SPOTIFY, songId, null, FilterMode.BLACK_LIST);
    }

    @Override
    public String getAddTermString() {
        return addTermText.getEditableText().toString().trim();
    }

}


package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.presenter.SongSelectPresenter;
import com.musicocracy.fpgk.mvp.view.SongSelectView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SongSelectActivity extends ActivityBase<SongSelectView> implements SongSelectView {
    private static final String TAG = "SongSelectActivity";
    private ArrayAdapter<String> adapter;

    @Inject SongSelectPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_song_select, this);

        Bundle requestBundle = getIntent().getExtras();

        // Data received from parent activity (Browse Request)
        if (requestBundle != null) {
            String requestString = requestBundle.getString(getString(R.string.request_string));
            presenter.populateBrowseSongs(requestString);
        } else { // No data received from parent activity (Vote Request)
            presenter.populateVoteSongs();
        }
    }

    @Override
    public void updateSongs(List<String> songs) {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(adapter);
    }

    @OnClick(R.id.selectBackButton)
    public void backClick() {
        onBackPressed();
    }

    @BindView(R.id.selectListView)
    public ListView listView;

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

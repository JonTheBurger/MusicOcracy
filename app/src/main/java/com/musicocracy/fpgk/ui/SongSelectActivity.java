package com.musicocracy.fpgk.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.model.dal.PlayRequest;
import com.musicocracy.fpgk.presenter.Presenter;
import com.musicocracy.fpgk.presenter.SongSelectPresenter;
import com.musicocracy.fpgk.view.SongSelectView;

import java.util.ArrayList;
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
        presenter.populateSongs();
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

package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.presenter.SongSelectPresenter;
import com.musicocracy.fpgk.mvp.view.SongSelectView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class SongSelectActivity extends ActivityBase<SongSelectView> implements SongSelectView {
    private static final String TAG = "SongSelectActivity";
    private static final int AUTHENTICATION_REQUEST_CODE = 1001;
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
    public void updateBrowseSongs(final List<String> songs) {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(adapter);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                presenter.playRequest(position);
            }
        };
        listView.setOnItemClickListener(listener);
    }

    @Override
    public void updateVotableSongs(List<String> songs) {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(adapter);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                presenter.voteRequest(position);
            }
        };
        listView.setOnItemClickListener(listener);
    }

    @Override
    public void onPlayRequestSuccess() {
        onBackPressed();
    }

    @Override
    public void onPlayRequestError(String error) {
        CharSequence text = "Play Request Failed: " + error;
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVoteRequestSuccess() {
        onBackPressed();
    }

    @Override
    public void onVoteRequestError(String error) {
        CharSequence text = "Vote Request Failed: " + error;
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}

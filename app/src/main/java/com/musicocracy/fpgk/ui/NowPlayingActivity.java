package com.musicocracy.fpgk.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.mvp.presenter.NowPlayingPresenter;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.view.NowPlayingView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NowPlayingActivity extends ActivityBase<NowPlayingView> implements NowPlayingView {
    private static final String TAG = "NowPlayingActivity";
    private ArrayAdapter<String> adapter;

    @Inject NowPlayingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_now_playing, this);

        presenter.updatePartyParameters();
        // Needs to be called after activity layout has been set
        presenter.updateCurrentPlayingTrack();
    }

    @Override
    protected Presenter<NowPlayingView> getPresenter() {
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

    @BindView(R.id.currentArtistTextView)
    public TextView artistText;

    @BindView(R.id.currentSongTextView)
    public TextView songText;

    @BindView(R.id.nowPlayingPartyCode)
    public TextView partyCodeText;

    @BindView(R.id.nowPlayingPartyName)
    public TextView partyNameText;

    @BindView(R.id.voteableSongsListView)
    public ListView listView;

    @Override
    public void updateArtist(String artistName) {
        artistText.setText(artistName);
    }

    @Override
    public void updateSong(String songName) {
        songText.setText(songName);
    }

    @Override
    public void updateVotableSongs(List<String> votableSongs) {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, votableSongs);
        listView.setAdapter(adapter);
    }

    @Override
    public void updatePartyCode(String partyCode) {
        partyCodeText.setText("Party Code: " + partyCode);
    }

    @Override
    public void updatePartyName(String partyName) {
        partyNameText.setText("Party Name: " + partyName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}

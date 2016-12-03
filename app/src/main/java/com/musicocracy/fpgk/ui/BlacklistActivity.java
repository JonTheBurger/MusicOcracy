package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.domain.dal.SongFilter;
import com.musicocracy.fpgk.mvp.presenter.BlacklistPresenter;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.view.BlacklistView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlacklistActivity extends ActivityBase<BlacklistView> implements BlacklistView {
    private static final String TAG = "BlacklistActivity";
    @Inject BlacklistPresenter presenter;

    @BindView(R.id.add_term_text) ListView filterListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_blacklist, this);

        //Get all blacklisted songs and display
        List<String> songFilterList = presenter.getAllBlacklistedSongIds();

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                songFilterList
        );

        filterListView.setAdapter(arrayAdapter);
    }

    @OnClick(R.id.filter_add_btn)
    public void addFilterClick() {
        Intent intent = new Intent(this, AddTermActivity.class);
        startActivity(intent);
    }

    @Override
    protected Presenter<BlacklistView> getPresenter() {
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

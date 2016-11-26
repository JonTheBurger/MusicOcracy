package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.domain.net.NetworkUtils;
import com.musicocracy.fpgk.mvp.presenter.PartyConfigPresenter;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.view.PartyConfigView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PartyConfigActivity extends ActivityBase<PartyConfigView> implements PartyConfigView {
    private static final String TAG = "PartyConfigActivity";
    @Inject PartyConfigPresenter presenter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.party_code_text_view) TextView partyCode;
    @BindView(R.id.party_name_edit_text) EditText partyName;
    @BindView(R.id.token_count_picker) EditText tokenCount;
    @BindView(R.id.token_refill_minute_picker) EditText tokenMinutes;
    @BindView(R.id.token_refill_second_picker) EditText tokenSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_party_config, this);
        initMenu();
        initNumberEdits();
        presenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            presenter.onBack();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }

    @OnClick(R.id.config_backward_btn)
    public void backClick() {
        onBackPressed();
    }

    @OnClick(R.id.config_forward_btn)
    public void forwardClick() {
        presenter.confirmSettings();
        Intent intent = new Intent(this, NowPlayingActivity.class);
        startActivity(intent);
    }

    //region View Implementation
    @Override
    public String getPartyCode() {
        return partyCode.getText().toString();
    }

    @Override
    public void setPartyCode(String code) {
        partyCode.setText(code);
        Log.i(TAG, "Code: " + code + " : " + NetworkUtils.base36ToIpAddress(code));
    }

    @Override
    public String getPartyName() {
        return partyName.getText().toString();
    }

    @Override
    public int getTokenCount() {
        return Integer.parseInt(tokenCount.getText().toString());
    }

    @Override
    public int getTokenRefillMinutes() {
        return Integer.parseInt(tokenMinutes.getText().toString());
    }

    @Override
    public int getTokenRefillSeconds() {
        return Integer.parseInt(tokenSeconds.getText().toString());
    }
    //endregion View Implementation

    //region IOC Boilerplate
    @Override
    protected Presenter<PartyConfigView> getPresenter() {
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
    //endregion IOC Boilerplate

    //region Menu Bar Setup
    private void initMenu() {
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.party_config_menu);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Options");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.party_config_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_open_filter:
                Intent intent = new Intent(this, BlacklistActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //endregion Menu Bar Setup

    //region Number Edit Setup
    private void initNumberEdits() {
        tokenCount.setFilters(new InputFilter[] { new InputFilterMinMax(1, 99) });
        InputFilter[] timeFilter = new InputFilter[] { new InputFilterMinMax(0, 59) };
        tokenMinutes.setFilters(timeFilter);
        tokenSeconds.setFilters(timeFilter);
    }

    // http://stackoverflow.com/questions/14212518/is-there-a-way-to-define-a-min-and-max-value-for-edittext-in-android
    private class InputFilterMinMax implements InputFilter {
        private final int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                // Remove the string out of destination that is to be replaced
                String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
                // Add the new string in
                newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
                int input = Integer.parseInt(newVal);
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }
    //endregion Number Edit Setup
}

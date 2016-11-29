package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.presenter.SelectionPresenter;
import com.musicocracy.fpgk.mvp.view.SelectionView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectionActivity extends ActivityBase<SelectionView> implements SelectionView {
    private static final String TAG = "SelectionActivity";
    private static final int AUTHENTICATION_REQUEST_CODE = 1001;
    @Inject SelectionPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_selection, this);
    }

    @OnClick(R.id.hostButton)
    public void hostClick() {
        // Get an access token
        Intent authIntent = new Intent(this, AuthenticationActivity.class);
        startActivityForResult(authIntent, AUTHENTICATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == AUTHENTICATION_REQUEST_CODE && resultCode == RESULT_OK) {
            //Use Data to get string
            Bundle authBundle = intent.getExtras();
            String token = authBundle.getString(getString(R.string.result_string));

            presenter.setSpotifyToken(token);

            Intent partyConfigIntent = new Intent(this, PartyConfigActivity.class);
            startActivity(partyConfigIntent);
        }
    }

    @OnClick(R.id.guestButton)
    public void guestClick() {
        Intent intent = new Intent(this, ConnectActivity.class);
        startActivity(intent);
    }

    private int debugClicks = 0;
    @OnClick(R.id.selectWelcomeTextView)
    public void welcomeClick() {
        debugClicks++;
        if (debugClicks >= 3) {
            Intent intent = new Intent(this, TestMainActivity.class);
            startActivity(intent);
        }
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

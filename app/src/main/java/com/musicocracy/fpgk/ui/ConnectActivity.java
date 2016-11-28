package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.domain.util.AndroidViewUtils;
import com.musicocracy.fpgk.mvp.presenter.ConnectPresenter;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.view.ConnectView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class ConnectActivity extends ActivityBase<ConnectView> implements ConnectView {
    private static final String TAG = "ConnectActivity";
    @Inject ConnectPresenter presenter;
    @BindView(R.id.party_code_edit_text) EditText partyCode;
    @BindView(R.id.party_name_edit_text) EditText partyName;
    @BindView(R.id.connect_forward_btn) ImageButton forwardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_connect, this);
        AndroidViewUtils.setImgBtnEnabled(forwardBtn, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @OnTextChanged(value = {R.id.party_code_edit_text, R.id.party_name_edit_text}, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChanged() {
        if (partyCode.toString().trim().length() == 0 || partyName.toString().trim().length() == 0) {
            AndroidViewUtils.setImgBtnEnabled(forwardBtn, false);
        } else {
            AndroidViewUtils.setImgBtnEnabled(forwardBtn, true);
        }
    }

    @OnClick(R.id.connect_back_btn)
    public void backClick() {
        super.onBackPressed();
        presenter.leaveParty();
    }

    @OnClick(R.id.connect_forward_btn)
    public void forwardClick() {
        presenter.joinParty();
    }

    //region View Implementation
    @Override
    public String getPartyCode() {
        return partyCode.getText().toString();
    }

    @Override
    public String getPartyName() {
        return partyName.getText().toString();
    }

    @Override
    public void onJoinSuccess() {
        Intent intent = new Intent(ConnectActivity.this, RequestActivity.class);
        startActivity(intent);
        presenter.onDestroy();
    }

    @Override
    public void onJoinError(String error) {
        CharSequence text = "Connection Failed: " + error;
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    //endregion View Implementation

    //region IOC Boilerplate
    @Override
    protected Presenter<ConnectView> getPresenter() {
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
}

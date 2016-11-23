package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.musicocracy.fpgk.CyberJukeboxApplication;
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
        setImageBtnEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @OnTextChanged(value = {R.id.party_code_edit_text, R.id.party_name_edit_text}, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onTextChanged() {
        if (partyCode.toString().trim().length() == 0 || partyName.toString().trim().length() == 0) {
            setImageBtnEnabled(false);
        } else {
            setImageBtnEnabled(true);
        }
    }

    @OnClick(R.id.connect_back_btn)
    public void backClick() {
        presenter.stopClient();
        onBackPressed();
    }

    @OnClick(R.id.connect_forward_btn)
    public void forwardClick() {
        if (presenter.startClient()) {
            Intent intent = new Intent(ConnectActivity.this, RequestActivity.class);
            ConnectActivity.this.startActivity(intent);
        } else {
            CharSequence text = "Connection Failed.";
            Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
    }

    private void setImageBtnEnabled(boolean enabled) {
        forwardBtn.setEnabled(enabled);
        forwardBtn.setClickable(enabled);
        forwardBtn.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
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

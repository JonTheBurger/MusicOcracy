package com.musicocracy.fpgk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.mvp.presenter.Presenter;
import com.musicocracy.fpgk.mvp.presenter.RequestPresenter;
import com.musicocracy.fpgk.mvp.view.RequestView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RequestActivity extends ActivityBase<RequestView> implements RequestView {
    private static final String TAG = "RequestActivity";
    @Inject RequestPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_request, this);
    }

    @Override
    public void onBackPressed() {
        presenter.stopClient();
        super.onBackPressed();
    }

    @OnClick(R.id.requestButton)
    public void requestClick() {
        Intent intent = new Intent(this, SongSelectActivity.class);
        Bundle requestData = new Bundle();
        // Load the string to be browsed for into the bundle with the request_string identifier
        String requestString = getRequestString();
        requestData.putString(getString(R.string.request_string), requestString);

        intent.putExtras(requestData);

        RequestActivity.this.startActivity(intent);
    }

    @OnClick(R.id.voteButton)
    public void voteClick() {
        Intent intent = new Intent(this, SongSelectActivity.class);
        RequestActivity.this.startActivity(intent);
    }

    @OnClick(R.id.requestBackButton)
    public void backClick() {
        onBackPressed();
    }

    @BindView(R.id.requestText)
    public TextView requestText;

    @Override
    protected Presenter<RequestView> getPresenter() {
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
    public String getRequestString() {
        return requestText.getEditableText().toString();
    }
}

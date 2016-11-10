package com.musicocracy.fpgk.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import com.musicocracy.fpgk.CyberJukeboxApplication;
import com.musicocracy.fpgk.presenter.NetworkTestPresenter;
import com.musicocracy.fpgk.view.NetworkTestView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NetworkTestActivity extends AppCompatActivity implements NetworkTestView {
    private static final String TAG = "NetworkTestActivity";

    @BindView(R.id.client_switch) Switch clientSwitch;
    @BindView(R.id.use_local_client_switch) Switch isClientLocalSwitch;
    @BindView(R.id.server_switch) Switch serverSwitch;
    @BindView(R.id.client_send_btn) Button clientSendBtn;
    @BindView(R.id.server_send_btn) Button serverSendBtn;
    @BindView(R.id.ip_text_edit) EditText ipEditText;
    @BindView(R.id.port_text_edit) EditText portEditText;
    @BindView(R.id.client_listview) ListView clientListView;
    @BindView(R.id.server_listview) ListView serverListView;

    @Inject NetworkTestPresenter presenter;
    private ArrayAdapter<String> clientLogAdapter;
    private ArrayAdapter<String> serverLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_test);
        ButterKnife.bind(this);
        CyberJukeboxApplication.getComponent(this).inject(this);
        presenter.setView(this);

        initWidgets();
        initAdapters();
    }

    private void initWidgets() {
        ipEditText.setText("192.168.0.101");
        portEditText.setText("2025");
    }

    private void initAdapters() {
        clientLogAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        serverLogAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        clientListView.setAdapter(clientLogAdapter);
        serverListView.setAdapter(serverLogAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            presenter.destroy();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }

    @OnClick(R.id.client_switch)
    public void onClientSwitch(View v) {
        presenter.clientToggle();
    }

    @OnClick(R.id.use_local_client_switch)
    public void onIsClientLocalSwitch(View v) {
        presenter.localHostToggle();
    }

    @OnClick(R.id.server_switch)
    public void onServerSwitch(View v) {
        try {
            presenter.serverToggle();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }

    @OnClick(R.id.client_send_btn)
    public void onClientSendBtn(View v) {
        presenter.clientSend();
    }

    @OnClick(R.id.server_send_btn)
    public void onServerSendBtn(View v) {
        presenter.serverSend();
    }

    @Override
    public String getIpText() {
        return ipEditText.getText().toString();
    }

    @Override
    public String getPortText() {
        return portEditText.getText().toString();
    }

    @Override
    public boolean getServerToggle() {
        return serverSwitch.isChecked();
    }

    @Override
    public boolean getClientToggle() {
        return clientSwitch.isChecked();
    }

    @Override
    public boolean getLocalHostToggle() {
        return isClientLocalSwitch.isChecked();
    }

    @Override
    public void setServerRunning(boolean isConnected) {
        serverSwitch.setChecked(isConnected);
        serverSendBtn.setEnabled(isConnected);
        setLocalHost(isConnected);
    }

    @Override
    public void setClientRunning(boolean isConnected) {
        clientSwitch.setChecked(isConnected);
        clientSendBtn.setEnabled(isConnected);
    }

    @Override
    public void setLocalHost(boolean isLocal) {
        isClientLocalSwitch.setChecked(isLocal);
        ipEditText.setEnabled(!isLocal);
    }

    @Override
    public void logServerEvent(String event) {
        Log.i(TAG, "Server: " + event);
        serverLogAdapter.add(event);
    }

    @Override
    public void logClientEvent(String event) {
        Log.i(TAG, "Client: " + event);
        clientLogAdapter.add(event);
    }
}

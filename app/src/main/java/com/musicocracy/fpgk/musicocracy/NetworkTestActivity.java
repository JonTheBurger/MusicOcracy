package com.musicocracy.fpgk.musicocracy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import com.musicocracy.fpgk.model.NetworkTestModel;
import com.musicocracy.fpgk.presenter.NetworkTestPresenter;
import com.musicocracy.fpgk.view.NetworkTestView;

public class NetworkTestActivity extends AppCompatActivity implements NetworkTestView {
    private static final String TAG = "NetworkTestActivity";

    private Switch clientSwitch;
    private Switch isClientLocalSwitch;
    private Switch serverSwitch;
    private Button clientSendBtn;
    private Button serverSendBtn;
    private EditText ipEditText;
    private EditText portEditText;
    private ListView clientListView;
    private ListView serverListView;

    private NetworkTestPresenter presenter;
    private ArrayAdapter<String> clientLogAdapter;
    private ArrayAdapter<String> serverLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_test);
        initWidgets();
        initAdapters();
        presenter = new NetworkTestPresenter(this, new NetworkTestModel());
    }

    private void initWidgets() {
        clientSwitch =      (Switch)findViewById(R.id.client_switch);
        isClientLocalSwitch=(Switch)findViewById(R.id.use_local_client_switch);
        serverSwitch =      (Switch)findViewById(R.id.server_switch);
        clientSendBtn =     (Button)findViewById(R.id.client_send_btn);
        serverSendBtn =     (Button)findViewById(R.id.server_send_btn);
        ipEditText =        (EditText)findViewById(R.id.ip_text_edit);
        portEditText =      (EditText)findViewById(R.id.port_text_edit);
        clientListView =    (ListView)findViewById(R.id.client_listview);
        serverListView =    (ListView)findViewById(R.id.server_listview);

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

    public void onClientSwitch(View v) {
        presenter.clientToggle();
    }

    public void onIsClientLocalSwitch(View v) {
        presenter.clientLocalToggle();
    }

    public void onServerSwitch(View v) {
        try {
            presenter.serverToggle();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void onClientSendBtn(View v) {
        presenter.clientSend();
    }

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
    public boolean getClientLocalToggle() {
        return isClientLocalSwitch.isChecked();
    }

    @Override
    public void setServerConnected(boolean isConnected) {
        serverSwitch.setChecked(isConnected);
        serverSendBtn.setEnabled(isConnected);
        setClientLocal(isConnected);
    }

    @Override
    public void setClientConnected(boolean isConnected) {
        clientSwitch.setChecked(isConnected);
        clientSendBtn.setEnabled(isConnected);
    }

    @Override
    public void setClientLocal(boolean isLocal) {
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

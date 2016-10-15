package com.musicocracy.fpgk.musicocracy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.musicocracy.fpgk.model.dal.Browser;
import com.musicocracy.fpgk.model.dal.ResultsListener;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastReceiverActivity extends AppCompatActivity implements ResultsListener{

    private static final String TAG = "BroadcastReceiver";
    private static final int PORT = 2562;
    private static final int AUTHENTICATION_REQUEST_CODE = 1001;

    private DatagramSocket socket;
    private TextView tViewRequests;
    private String token;
    private ReceiveThread rt;
    private Browser browser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_receiver);

        initSocks();
    }

    public void initSocks() {
        try {
            tViewRequests = (TextView) findViewById(R.id.tViewRequests);
            socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
        } catch (IOException e) {
            Log.i(TAG, e.toString());
        }
    }

    public void receiveBroadcast(View view) {
        ToggleButton togDiscovery = (ToggleButton) findViewById(R.id.togDiscovery);

        if (togDiscovery.isChecked()) {
            // Get an access token
            Intent authIntent = new Intent(this, AuthenticationActivity.class);
            if (browser == null) {
                startActivityForResult(authIntent, AUTHENTICATION_REQUEST_CODE);
            } else { //already have a token
                rt = new ReceiveThread(token, socket, this);
                rt.execute();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == AUTHENTICATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Use Data to get string
                Bundle authBundle = intent.getExtras();
                token = authBundle.getString(getString(R.string.result_string));

                browser = new Browser(token, this);

                rt = new ReceiveThread(token, socket, this);
                rt.execute();

                //final Browser browser = new Browser(token);
            }
        }
    }

    @Override
    public void onResultsSucceeded(String result) {
        Log.i(TAG, "Message Received: " + result);

        browser.browseTracks(result);

        ToggleButton togDiscovery = (ToggleButton) findViewById(R.id.togDiscovery);

        // Kill AsyncTask if receive broadcast is no longer toggled
        if (!togDiscovery.isChecked()) {
            rt.cancel(true);
        }
    }
}
package com.musicocracy.fpgk.musicocracy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.musicocracy.fpgk.model.dal.Browser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BroadcastReceiverActivity extends AppCompatActivity {

    private static final String TAG = "BroadcastReceiver";
    private static final int PORT = 2562;
    private static final int AUTHENTICATION_REQUEST_CODE = 1001;

    private DatagramSocket socket;
    private TextView tViewRequests;

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
            startActivityForResult(authIntent, AUTHENTICATION_REQUEST_CODE);
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
                String token = authBundle.getString(getString(R.string.result_string));
                ToggleButton togDiscovery = (ToggleButton) findViewById(R.id.togDiscovery);

                ExecutorService executor = Executors.newSingleThreadExecutor();

                Callable<String> callable = new ReceiveThread(TAG, token, socket, togDiscovery.isChecked());

                final Future<String> future = executor.submit(callable);

                while(togDiscovery.isChecked()) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            String returnedString = null;
                            try {
                                returnedString = future.get(1, TimeUnit.SECONDS);
                                ((TextView)findViewById(R.id.tViewRequests)).setText(returnedString);
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                // No Data Received
                            }
                        }
                    });
                }

                //Thread receiveThread = new Thread(new ReceiveThread(TAG, token, socket, togDiscovery.isChecked()));

                // Need to destroy to prevent leaking a thread
                //receiveThread.start();


//                    public void run() {
//                        int i = 0;
//                        while (i++ < 1000) {
//                            try {
//                                runOnUiThread(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//                                        ToggleButton togDiscovery = (ToggleButton) findViewById(R.id.togDiscovery);
//                                        try {
//                                            if (togDiscovery.isChecked()) {
//                                                Log.i(TAG, "Ready to receive broadcast packets!");
//
//                                                byte[] buf = new byte[15000];
//                                                DatagramPacket packet = new DatagramPacket(buf, buf.length);
//                                                socket.receive(packet);
//                                                Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
//                                                String data = new String(packet.getData()).trim();
//                                                Log.i(TAG, "Packet data: " + data);
//                                                //String tViewData = tViewRequests.getText().toString();
//                                                tViewRequests.setText(data);
//
//                                                if (token != null) {
//                                                    //browser.browseTracks(data);
//                                                    Log.d(TAG, "Token: " + token);
//                                                }
//
//                                            }
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//                                Thread.sleep(300);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }.start();

                //final Browser browser = new Browser(token);
            }
        }
    }
}
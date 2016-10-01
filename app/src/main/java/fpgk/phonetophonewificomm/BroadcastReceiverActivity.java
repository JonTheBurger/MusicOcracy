package fpgk.phonetophonewificomm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastReceiverActivity extends AppCompatActivity {

    private static final String TAG = "BroadcastReceiver";
    private static final int PORT = 2562;

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
        new Thread() {
            public void run() {
                int i = 0;
                while (i++ < 1000) {
                    try {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                ToggleButton togDiscovery = (ToggleButton) findViewById(R.id.togDiscovery);
                                try {
                                    if (togDiscovery.isChecked()) {
                                        Log.i(TAG, "Ready to receive broadcast packets!");

                                        byte[] buf = new byte[15000];
                                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                                        socket.receive(packet);
                                        Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
                                        String data = new String(packet.getData()).trim();
                                        Log.i(TAG, "Packet data: " + data);
                                        //String tViewData = tViewRequests.getText().toString();
                                        tViewRequests.setText(data);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}

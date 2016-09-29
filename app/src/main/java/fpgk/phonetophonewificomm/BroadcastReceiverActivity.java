package fpgk.phonetophonewificomm;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_receiver);
    }

    public void receiveBroadcast(View view) {
        try {
            ToggleButton tB = (ToggleButton) findViewById(R.id.togDiscovery);
            boolean done = !tB.isChecked();
            DatagramSocket socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
            TextView tViewRequests = (TextView)findViewById(R.id.tViewRequests);

            while (!done) {
                done = !tB.isChecked();
                Log.i(TAG,"Ready to receive broadcast packets!");

                byte[] buf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
                String data = new String(packet.getData()).trim();
                Log.i(TAG, "Packet data: " + data);

                if(data.length() > 0) {
                    tViewRequests.setText(data);
                } else {
                    tViewRequests.setText("");
                }


            }
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
    }
}

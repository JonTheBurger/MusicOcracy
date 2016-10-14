package com.musicocracy.fpgk.musicocracy;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Callable;

public class ReceiveThread implements Callable<String> {
    private volatile String token;
    private volatile String TAG;
    private volatile DatagramSocket socket;
    private volatile boolean toggleStatus;

    public ReceiveThread(String debugTAG, String newToken, DatagramSocket newSocket, boolean toggle){
        this.TAG = debugTAG;
        this.token = newToken;
        this.socket = newSocket;
        this.toggleStatus = toggle;
    }

    public String call(){
        String returnString = null;
        try {
            if (toggleStatus) {
                Log.i(TAG, "Ready to receive broadcast packets!");

                byte[] buf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                socket.receive(packet);
                Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
                String data = new String(packet.getData()).trim();
                Log.i(TAG, "Packet data: " + data);
                //String tViewData = tViewRequests.getText().toString();
                //tViewRequests.setText(data);

                returnString = data;

                if (token != null) {
                    //browser.browseTracks(data);
                    Log.d(TAG, "Token: " + token);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnString;
    }
}

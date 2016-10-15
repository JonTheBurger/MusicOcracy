package com.musicocracy.fpgk.musicocracy;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ToggleButton;

import com.musicocracy.fpgk.model.dal.ResultsListener;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Callable;

public class ReceiveThread extends AsyncTask<Void, String, String> {
    private String TAG = "Receive Thread";
    private String token;
    private DatagramSocket socket;
    private boolean toggleStatus;
    private ResultsListener listener;

    public ReceiveThread(String newToken, DatagramSocket newSocket, ResultsListener parentListener){
        this.token = newToken;
        this.socket = newSocket;
        this.listener = parentListener;
    }

    @Override
    protected String doInBackground(Void... params) {
        String returnString = null;
        try {
            Log.i(TAG, "Ready to receive broadcast packets!");

            byte[] buf = new byte[15000];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while (!this.isCancelled()) {
                socket.receive(packet);
                Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
                // Trim the string to the actual length of the received message
                String data = new String(packet.getData(), 0, packet.getLength()).trim();
                Log.i(TAG, "Packet data: " + data);
                returnString = data;
                publishProgress(returnString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnString;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        for (int i = 0; i < values.length; i++) {
            listener.onResultsSucceeded(values[i]);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onResultsSucceeded(result);
    }
}

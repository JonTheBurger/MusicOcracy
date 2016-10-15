package com.musicocracy.fpgk.musicocracy;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.musicocracy.fpgk.model.dal.ResultsListener;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Callable;

public class ReceiveThread extends AsyncTask<Void, Void, String> {
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
            socket.receive(packet);
            Log.i(TAG, "Packet received from: " + packet.getAddress().getHostAddress());
            String data = new String(packet.getData()).trim();
            Log.i(TAG, "Packet data: " + data);
            returnString = data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnString;
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onResultsSucceeded(result);
    }
}

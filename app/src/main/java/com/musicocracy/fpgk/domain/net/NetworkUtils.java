package com.musicocracy.fpgk.domain.net;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;

import static android.content.Context.WIFI_SERVICE;

public class NetworkUtils {
    //region IP Address
    public static String ipAddressToBase36(String ipAddress) {
        byte[] ipBytes = ipv4ToBytes(ipAddress);
        return Long.toString(bytesToLong(ipBytes), 36);
    }

    private static byte[] ipv4ToBytes(String ip) {
        String[] bStr = ip.split("\\.");    // If ip string does not have a length of 4, we throw
        byte[] b = new byte[8];
        for (int i = 4; i < 8; i++) {
            b[i] = (byte)((0xFF) & Integer.parseInt(bStr[i - 4]));
        }
        return b;
    }

    private static long bytesToLong(byte[] b) {  // MSB first
        return ByteBuffer.wrap(b).getLong();
    }

    public static String base36ToIpAddress(String base36) {
        long addressAsLong = Long.parseLong(base36, 36);
        byte[] bytes = longToBytes(addressAsLong);
        return ipv4FromBytes(bytes);
    }

    private static byte[] longToBytes(long l) {  // MSB first
        return ByteBuffer.allocate(8).putLong(l).array();
    }

    private static String ipv4FromBytes(byte[] b) { // MSB first
        return "" + ((0xFF) & b[b.length - 4]) + '.' + ((0xFF) & b[b.length - 3]) + '.' + ((0xFF) & b[b.length - 2]) + '.' + ((0xFF) & b[b.length - 1]);
    }

    public static String getLocalIpAddress(Context context) {
        WifiManager wifi = (WifiManager)context.getSystemService(WIFI_SERVICE);
        int raw = wifi.getConnectionInfo().getIpAddress();
        byte[] bytes = intToBytes(raw);
        return "" + bytes[3] + '.' + bytes[2] + '.' + bytes[1] + '.' + bytes[0];
    }

    private static byte[] intToBytes(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public static String getPublicIpAddress() {
        try {
            URLConnection conn = new URL("http://api.ipify.org").openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String ip = in.readLine();
            in.close();
            return ip;
        } catch (IOException e) {
            return "";
        }
    }
    //endregion IP Address
}

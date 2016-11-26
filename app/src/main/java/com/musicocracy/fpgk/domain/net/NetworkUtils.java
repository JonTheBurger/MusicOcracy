package com.musicocracy.fpgk.domain.net;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.google.common.net.InetAddresses;
import com.google.protobuf.MessageLite;
import com.musicocracy.fpgk.net.proto.Envelope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import static android.content.Context.WIFI_SERVICE;

public class NetworkUtils {
    //region IP Address
    public static String ipAddressToBase36(String ipAddress) {
        InetAddress inetAddress = InetAddresses.forString(ipAddress);
        return inetAddressToBase36(inetAddress);
    }

    public static String inetAddressToBase36(InetAddress inetAddress) {
        int addressAsInt = InetAddresses.coerceToInteger(inetAddress);
        return Integer.toString(addressAsInt, 36);
    }

    public static String base36ToIpAddress(String base36) {
        return base36ToInetAddress(base36.toLowerCase()).getHostAddress();
    }

    public static InetAddress base36ToInetAddress(String base36) {
        int addressAsInt = Integer.parseInt(base36, 36);
        return InetAddresses.fromInteger(addressAsInt);
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

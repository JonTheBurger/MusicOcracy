package com.musicocracy.fpgk.domain.net;

import com.google.common.net.InetAddresses;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class NetworkUtils {
    //region IP Address
    public static String ipAddressToBase36(String ipAddress) {
        InetAddress inetAddress = InetAddresses.forString(ipAddress);
        return inetAddressToBase36(inetAddress);
    }

    public static String inetAddressToBase36(InetAddress inetAddress) {
        int addressAsInt = InetAddresses.coerceToInteger(inetAddress);
        byte[] intBytes = Ints.toByteArray(addressAsInt);
        byte[] bytes = new byte[8];
        for (int i = 4; i < 8; i++) {
            bytes[i] = intBytes[i - 4];
        }
        return Long.toString(Longs.fromByteArray(bytes), 36);
    }

    public static String base36ToIpAddress(String base36) {
        return base36ToInetAddress(base36.toLowerCase()).getHostAddress();
    }

    public static InetAddress base36ToInetAddress(String base36) {
        long addressAsLong = Long.parseLong(base36, 36);
        byte[] bytes = Longs.toByteArray(addressAsLong);
        int addressAsInt = Ints.fromByteArray(Arrays.copyOfRange(bytes, 4, 8));
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

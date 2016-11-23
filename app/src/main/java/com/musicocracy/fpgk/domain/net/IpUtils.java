package com.musicocracy.fpgk.domain.net;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.google.common.net.InetAddresses;

import java.net.InetAddress;

import static android.content.Context.WIFI_SERVICE;

public class IpUtils {
    public static final int DEFAULT_PORT = 2025;

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

    public static String getMyIpAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int addressAsInt = wm.getConnectionInfo().getIpAddress();
        InetAddress inetAddress = InetAddresses.fromInteger(addressAsInt);
        return inetAddress.getHostAddress();
    }
}

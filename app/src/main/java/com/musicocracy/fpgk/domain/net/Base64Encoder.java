package com.musicocracy.fpgk.domain.net;

import android.util.Base64;

public class Base64Encoder {
    public byte[] base64ToBytes(String string) {
        return Base64.decode(string, Base64.DEFAULT);
    }

    public String bytesToBase64(byte[] raw) {
        return Base64.encodeToString(raw, Base64.DEFAULT);
    }
}

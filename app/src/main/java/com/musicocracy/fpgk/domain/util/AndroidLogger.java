package com.musicocracy.fpgk.domain.util;

import android.util.Log;

public class AndroidLogger implements Logger {
    @Override
    public void verbose(String tag, String msg) {
        Log.v(tag, msg);
    }

    @Override
    public void info(String tag, String msg) {
        Log.i(tag, msg);
    }

    @Override
    public void debug(String tag, String msg) {
        Log.d(tag, msg);
    }

    @Override
    public void warning(String tag, String msg) {
        Log.w(tag, msg);
    }

    @Override
    public void error(String tag, String msg) {
        Log.e(tag, msg);
    }

    @Override
    public void critical(String tag, String msg, Throwable throwable) {
        Log.wtf(tag, msg, throwable);
    }
}

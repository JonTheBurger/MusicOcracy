package com.musicocracy.fpgk.domain.util;

public class NullLogger implements Logger {
    @Override
    public void verbose(String tag, String msg) {}

    @Override
    public void info(String tag, String msg) {}

    @Override
    public void debug(String tag, String msg) {}

    @Override
    public void warning(String tag, String msg) {}

    @Override
    public void error(String tag, String msg) {}

    @Override
    public void critical(String tag, String msg, Throwable throwable) {}
}

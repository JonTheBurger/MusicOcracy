package com.musicocracy.fpgk.domain.util;

public interface Logger {
    void verbose(String tag, String msg);
    void info(String tag, String msg);
    void debug(String tag, String msg);
    void warning(String tag, String msg);
    void error(String tag, String msg);
    void critical(String tag, String msg, Throwable throwable);
}

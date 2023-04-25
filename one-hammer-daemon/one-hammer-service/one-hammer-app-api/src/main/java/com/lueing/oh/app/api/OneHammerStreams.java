package com.lueing.oh.app.api;

import com.lueing.oh.pojo.OneHammerStream;

public interface OneHammerStreams {
    void start(OneHammerStream stream) throws OneHammerException;
    void stop(OneHammerStream stream) throws OneHammerException;
}

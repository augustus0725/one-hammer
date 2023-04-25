package com.lueing.oh.app.api;

import com.lueing.oh.pojo.OneHammerJob;
import com.lueing.oh.pojo.OneHammerStream;

public interface OneHammerStreams {
    void start(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException;

    void stop(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException;

    void runOnce(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException;

    String logs(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException;
}

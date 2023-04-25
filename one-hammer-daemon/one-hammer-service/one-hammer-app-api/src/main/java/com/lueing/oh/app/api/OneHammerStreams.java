package com.lueing.oh.app.api;

import com.lueing.oh.pojo.OneHammerJob;
import com.lueing.oh.pojo.OneHammerStream;

public interface OneHammerStreams {
    void start(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException;

    void stop(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException;

    void createIfNotExists(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException;

    void deleteIfExists(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException;
}

package com.lueing.oh.app.v1;

import com.lueing.oh.app.api.OneHammerException;
import com.lueing.oh.app.api.OneHammerStreams;
import com.lueing.oh.pojo.OneHammerJob;
import com.lueing.oh.pojo.OneHammerStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
@Slf4j
public class OneHammerStreamsImpl implements OneHammerStreams {
    @Override
    public void start(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException {

    }

    @Override
    public void stop(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException {

    }

    @Override
    public void createIfNotExists(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException {

    }

    @Override
    public void deleteIfExists(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException {

    }
}

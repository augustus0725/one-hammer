package com.lueing.oh.app.v1;

import com.lueing.oh.app.api.OneHammerException;
import com.lueing.oh.app.api.OneHammerStreams;
import com.lueing.oh.app.v1.cmd.PodCmd;
import com.lueing.oh.commons.os.Os;
import com.lueing.oh.pojo.OneHammerJob;
import com.lueing.oh.pojo.OneHammerStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
@Slf4j
public class OneHammerStreamsImpl implements OneHammerStreams {
    @Override
    public void start(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException {
        try {
            Os.shell(CommandLine.parse(PodCmd.createStartPodCmdFrom(hammerJob.getMetadata().getName(), stream)), null);
        } catch (IOException e) {
            throw new OneHammerException(e);
        }
    }

    @Override
    public void stop(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException {
        try {
            Os.shell(CommandLine.parse(PodCmd.createStopPodCmdFrom(hammerJob.getMetadata().getName(), stream)), null);
        } catch (IOException e) {
            throw new OneHammerException(e);
        }
    }

    @Override
    public void runOnce(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException {
        try {
            Os.shell(CommandLine.parse(PodCmd.createRunPodOnceCmdFrom(hammerJob.getMetadata().getName(), stream)), null);
        } catch (IOException e) {
            throw new OneHammerException(e);
        }
    }

    @Override
    public String logs(OneHammerJob hammerJob, OneHammerStream stream) throws OneHammerException {
        String log;
        try {
            log = Os.shellWithResult(CommandLine.parse(PodCmd.createLogsPodCmdFrom(hammerJob.getMetadata().getNamespace(), stream)), null);
        } catch (IOException e) {
            throw new OneHammerException(e);
        }
        return log;
    }
}

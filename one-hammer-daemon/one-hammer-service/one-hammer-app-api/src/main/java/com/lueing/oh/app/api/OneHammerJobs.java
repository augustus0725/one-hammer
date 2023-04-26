package com.lueing.oh.app.api;

import com.lueing.oh.app.api.vo.OneHammerJobStatus;
import com.lueing.oh.app.api.vo.OneHammerJobVO;
import com.lueing.oh.pojo.OneHammerJob;

import java.util.List;

public interface OneHammerJobs {
    String createOneHammerJob(String yamlJob) throws OneHammerException;

    List<OneHammerJobVO> oneHammerJobs() throws OneHammerException;

    void start(OneHammerJob hammerJob) throws OneHammerException;

    void stop(OneHammerJob hammerJob) throws OneHammerException;

    OneHammerJobStatus query(String hammerId) throws OneHammerException;
}

package com.lueing.oh.app.api;

import com.lueing.oh.app.api.vo.OneHammerJobStatus;
import com.lueing.oh.app.api.vo.OneHammerJobVO;
import com.lueing.oh.pojo.OneHammerJob;

import java.util.List;

public interface OneHammerJobs {
    void createOneHammerJob(String yamlJob);

    List<OneHammerJobVO> oneHammerJobs();

    void start(OneHammerJob hammerId) throws OneHammerException;

    void stop(OneHammerJob hammerId) throws OneHammerException;

    OneHammerJobStatus query(String hammerId);
}

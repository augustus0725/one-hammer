package com.lueing.oh.app.api;

import com.lueing.oh.app.api.vo.OneHammerJobStatus;
import com.lueing.oh.app.api.vo.OneHammerJobVO;

import java.util.List;

public interface OneHammerJobs {
    void createOneHammerJob(String yamlJob);

    List<OneHammerJobVO> oneHammerJobs();

    void start(String hammerId);

    void stop(String hammerId);

    OneHammerJobStatus query(String hammerId);
}

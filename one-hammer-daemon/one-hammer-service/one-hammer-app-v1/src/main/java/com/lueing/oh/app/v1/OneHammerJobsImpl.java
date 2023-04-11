package com.lueing.oh.app.v1;

import com.lueing.oh.app.api.OneHammerJobs;
import com.lueing.oh.app.api.vo.OneHammerJobStatus;
import com.lueing.oh.app.api.vo.OneHammerJobVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OneHammerJobsImpl implements OneHammerJobs {
    @Override
    public void createOneHammerJob(String yamlJob) {

    }

    @Override
    public List<OneHammerJobVO> oneHammerJobs() {
        return null;
    }

    @Override
    public void start(String hammerId) {

    }

    @Override
    public void stop(String hammerId) {

    }

    @Override
    public OneHammerJobStatus query(String hammerId) {
        return null;
    }
}

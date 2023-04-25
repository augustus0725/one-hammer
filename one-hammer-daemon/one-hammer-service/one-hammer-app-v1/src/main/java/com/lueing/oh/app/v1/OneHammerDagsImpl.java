package com.lueing.oh.app.v1;

import com.google.common.base.Strings;
import com.lueing.oh.app.api.OneHammerDags;
import com.lueing.oh.app.api.OneHammerException;
import com.lueing.oh.dag.OneHammerDagException;
import com.lueing.oh.pojo.OneHammerDag;
import com.lueing.oh.pojo.OneHammerJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
@Slf4j
public class OneHammerDagsImpl implements OneHammerDags {
    private final com.lueing.oh.dag.OneHammerDag oneHammerDag;
    @Override
    public void start(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException {
        try {
            if (Strings.isNullOrEmpty(dag.getSchedule())) {
                oneHammerDag.startDagOnce(hammerJob.getMetadata().getNamespace(), dag.getDagId());
            } else {
                oneHammerDag.beginSchedule(hammerJob.getMetadata().getNamespace(), dag.getDagId());
            }
        } catch (OneHammerDagException e) {
            throw new OneHammerException(e);
        }
    }

    @Override
    public void stop(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException {
        if (!Strings.isNullOrEmpty(dag.getSchedule())) {
            try {
                oneHammerDag.stopSchedule(hammerJob.getMetadata().getNamespace(), dag.getDagId());
            } catch (OneHammerDagException e) {
                throw new OneHammerException(e);
            }
        }
    }

    @Override
    public void createIfNotExists(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException {
        if (Strings.isNullOrEmpty(dag.getDagId())) {
            String dagId = null;
            try {
                dagId = oneHammerDag.createDag(hammerJob.getMetadata().getNamespace(),
                        dag.getTemplate(), dag.getConfig());
                // schedule
                if (Strings.isNullOrEmpty(dag.getSchedule())) {
                    oneHammerDag.updateDagSchedule(hammerJob.getMetadata().getNamespace(), dagId, dag.getSchedule());
                }
            } catch (OneHammerDagException e) {
                throw new OneHammerException(e);
            }
            dag.setDagId(dagId);
        }
    }

    @Override
    public void deleteIfExists(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException {
        if (!Strings.isNullOrEmpty(dag.getDagId())) {
            try {
                oneHammerDag.deleteDag(hammerJob.getMetadata().getNamespace(), dag.getDagId());
                dag.setDagId(null);
            } catch (OneHammerDagException e) {
                throw new OneHammerException(e);
            }
        }
    }
}

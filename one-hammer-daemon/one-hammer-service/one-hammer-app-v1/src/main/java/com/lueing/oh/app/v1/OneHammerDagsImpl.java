package com.lueing.oh.app.v1;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lueing.oh.app.api.OneHammerDags;
import com.lueing.oh.app.api.OneHammerException;
import com.lueing.oh.dag.DagSnapshot;
import com.lueing.oh.dag.OneHammerDagException;
import com.lueing.oh.pojo.OneHammerDag;
import com.lueing.oh.pojo.OneHammerJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
            deleteIfExists(hammerJob, dag);
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
                if (!Strings.isNullOrEmpty(dag.getSchedule())) {
                    oneHammerDag.updateDagSchedule(hammerJob.getMetadata().getNamespace(), dagId, dag.getSchedule());
                }
            } catch (OneHammerDagException e) {
                throw new OneHammerException(e);
            }
            dag.setDagId(dagId);
        }
    }

    @Override
    public String logs(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException {
        return null;
    }

    @Override
    public String status(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException {
        try {
            List<DagSnapshot> latestSnapshot = oneHammerDag.displayDagSnapshots(
                    hammerJob.getMetadata().getNamespace(), dag.getDagId(), 1, 1);
            if (!latestSnapshot.isEmpty()) {
                DagSnapshot latest = latestSnapshot.get(0);
                Joiner joiner = Joiner.on('\t');

                return joiner.join(ImmutableList.of("ID", "NAME", "STATE", "START_TIME", "END_TIME", "DURATION", "\n")) // header
                        + joiner.join(ImmutableList.of(latest.getDagId(), latest.getName(), latest.getState(), latest.getStartTime(), // data
                        latest.getEndTime(), latest.getDuration(), "\n"));
            }
        } catch (OneHammerDagException e) {
            throw new OneHammerException(e);
        }
        return "";
    }

    private void deleteIfExists(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException {
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

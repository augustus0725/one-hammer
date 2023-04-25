package com.lueing.oh.app.api;

import com.lueing.oh.pojo.OneHammerDag;
import com.lueing.oh.pojo.OneHammerJob;

public interface OneHammerDags {
    void start(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException;

    void stop(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException;

    void createIfNotExists(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException;

    String logs(OneHammerJob hammerJob, OneHammerDag dag) throws OneHammerException;
}

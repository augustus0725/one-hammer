package com.lueing.oh.dag;

import java.util.List;
import java.util.Map;

public interface OneHammerDag {
    void createNamespace(String namespace, String description) throws OneHammerDagException;
    List<String> namespaces() throws OneHammerDagException;
    void deleteNamespace(String namespace) throws OneHammerDagException;
    String loadDagTemplate(String namespace, String templateName, String path, String description) throws OneHammerDagException;
    void deleteDagTemplate(String namespace, String templateName) throws OneHammerDagException;
    List<DagTemplate> templates(String namespace) throws OneHammerDagException;
    String createDag(String namespace, String templateName, Map<String, String> config) throws OneHammerDagException;
    void updateDagSchedule(String namespace, String dagId, String cron) throws OneHammerDagException;
    void beginSchedule(String namespace, String dagId) throws OneHammerDagException;
    void stopSchedule(String namespace, String dagId) throws OneHammerDagException;
    void deleteDag(String namespace, String dagId) throws OneHammerDagException;
    String startDagOnce(String namespace, String dagId) throws OneHammerDagException;
    DagTasksSnapshot displayDagSnapshot(String namespace, String dagId, String snapshotId) throws OneHammerDagException;
    List<DagSnapshot> displayDagSnapshots(String namespace, String dagId, long pageNo, long pageSize) throws OneHammerDagException;
}

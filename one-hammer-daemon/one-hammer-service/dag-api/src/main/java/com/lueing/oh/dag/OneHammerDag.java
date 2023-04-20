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
    String createInstanceFromDagTemplate(String namespace, String templateName, Map<String, String> config) throws OneHammerDagException;
    void updateInstanceSchedule(String namespace, String instanceId, String cron) throws OneHammerDagException;
    void startInstance(String namespace, String instanceId) throws OneHammerDagException;
    void stopInstance(String namespace, String instanceId) throws OneHammerDagException;
    void deleteInstance(String namespace, String instanceId) throws OneHammerDagException;
}

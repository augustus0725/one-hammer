package com.lueing.oh.dag;

import java.util.List;
import java.util.Map;

public interface OneHammerDag {
    void createNamespace(String namespace, String description);
    List<String> namespaces();
    void deleteNamespace(String namespace);
    String loadDagTemplate(String namespace, String templateName, String path, String description);
    String deleteDagTemplate(String namespace, String templateName);
    List<DagTemplate> templates(String namespace);
    String createInstanceFromDagTemplate(String namespace, String templateName, Map<String, String> config);
    void startInstance(String instanceId);
    void stopInstance(String instanceId);
    void deleteInstance(String instanceId);
}

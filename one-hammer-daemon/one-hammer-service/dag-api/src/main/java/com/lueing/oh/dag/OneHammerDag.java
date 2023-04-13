package com.lueing.oh.dag;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface OneHammerDag {
    void createNamespace(String namespace);
    List<String> namespaces();
    void deleteNamespace(String namespace);
    String importDagTemplate(String namespace, String templateName, Path path);
    String deleteDagTemplate(String namespace, String templateName);
    List<String> templates(String namespace);
    String createInstanceFromDagTemplate(String namespace, String templateName, Map<String, String> config);
    void startInstance(String instanceId);
    void stopInstance(String instanceId);
    void deleteInstance(String instanceId);
}

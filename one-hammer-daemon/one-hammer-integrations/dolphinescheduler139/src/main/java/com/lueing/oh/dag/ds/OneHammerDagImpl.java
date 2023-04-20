package com.lueing.oh.dag.ds;

import com.lueing.oh.dag.DagTemplate;
import com.lueing.oh.dag.OneHammerDag;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OneHammerDagImpl implements OneHammerDag {
    @Override
    public void createNamespace(String namespace, String description) {

    }

    @Override
    public List<String> namespaces() {
        return null;
    }

    @Override
    public void deleteNamespace(String namespace) {

    }

    @Override
    public String loadDagTemplate(String namespace, String templateName, String path, String description) {
        return null;
    }

    @Override
    public String deleteDagTemplate(String namespace, String templateName) {
        return null;
    }

    @Override
    public List<DagTemplate> templates(String namespace) {
        return null;
    }

    @Override
    public String createInstanceFromDagTemplate(String namespace, String templateName, Map<String, String> config) {
        return null;
    }

    @Override
    public void startInstance(String instanceId) {

    }

    @Override
    public void stopInstance(String instanceId) {

    }

    @Override
    public void deleteInstance(String instanceId) {

    }
}

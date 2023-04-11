package com.lueing.oh.dag.ds;

import com.lueing.oh.dag.OneHammerDag;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class OneHammerDagImpl implements OneHammerDag {
    @Override
    public String importDagTemplate(String namespace, String templateName, Path path) {
        return null;
    }

    @Override
    public String deleteDagTemplate(String namespace, String templateName) {
        return null;
    }

    @Override
    public List<String> templates(String namespace) {
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

package com.lueing.oh.app.v1.cmd;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.lueing.oh.pojo.OneHammerStream;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;
import java.util.stream.Collectors;

public class PodCmd {
    private final static String POD_RUN = "kubectl run ${podName} -n ${namespace} --env=\"${env}\" --rm --restart=${restartPolicy} --generator=run-pod/v1 --requests ='cpu=${cpu},memory=${memory}' --image=${image} --command -- ${command}";
    private final static String POD_DELETE = "kubectl delete ${podName} -n ${namespace}";
    private final static String POD_LOGS = "kubectl logs ${podName} -n ${namespace}";

    public static String createStopPodCmdFrom(String namespace, OneHammerStream stream) {
        Map<String, String> dataBind = Maps.newHashMap();
        StringSubstitutor substitutor = new StringSubstitutor(dataBind);

        dataBind.put("podName", stream.getName());
        dataBind.put("namespace", namespace);
        return substitutor.replace(POD_DELETE);
    }

    public static String createRunPodOnceCmdFrom(String namespace, OneHammerStream stream) {
        return create(namespace, stream, true);
    }

    public static String createLogsPodCmdFrom(String namespace, OneHammerStream stream) {
        return null;
    }

    public static class RestartPolicy {
        public static String ALWAYS = "Always";
        public static String ON_FAILURE = "OnFailure";
        public static String NEVER = "Never";
    }

    public static String createStartPodCmdFrom(String namespace, OneHammerStream stream) {
        Map<String, String> dataBind = Maps.newHashMap();
        StringSubstitutor substitutor = new StringSubstitutor(dataBind);

        dataBind.put("podName", stream.getName());
        dataBind.put("namespace", namespace);
        return substitutor.replace(POD_LOGS);
    }

    private static String create(String namespace, OneHammerStream stream, boolean runOnce) {
        Map<String, String> dataBind = Maps.newHashMap();
        StringSubstitutor substitutor = new StringSubstitutor(dataBind);

        dataBind.put("podName", stream.getName());
        dataBind.put("namespace", namespace);
        dataBind.put("restartPolicy", runOnce ? RestartPolicy.NEVER : RestartPolicy.ALWAYS);
        dataBind.put("cpu", stream.getResources().getLimits().getCpu());
        dataBind.put("memory", stream.getResources().getLimits().getMemory());
        dataBind.put("image", stream.getTemplate());
        dataBind.put("command", Joiner.on(' ').join(stream.getCommand()) +
                " " + Joiner.on(' ').join(stream.getArgs()));
        dataBind.put("env", Joiner.on(',').join(
                stream.getEnv().stream()
                        .map(pair -> pair.getName() + "=" + pair.getValue()).collect(Collectors.toList())));

        return substitutor.replace(POD_RUN);
    }
}

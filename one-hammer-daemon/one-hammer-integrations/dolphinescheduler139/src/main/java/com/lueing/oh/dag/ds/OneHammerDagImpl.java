package com.lueing.oh.dag.ds;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.lueing.oh.commons.os.Os;
import com.lueing.oh.dag.*;
import com.lueing.oh.dag.ds.feign.Ds139Feign;
import com.lueing.oh.dfs.Dfs;
import com.lueing.oh.jpa.entity.Ds139Dag;
import com.lueing.oh.jpa.entity.Ds139Namespace;
import com.lueing.oh.jpa.entity.Ds139Template;
import com.lueing.oh.jpa.repository.rw.Ds139InstanceRepository;
import com.lueing.oh.jpa.repository.rw.Ds139NamespaceRepository;
import com.lueing.oh.jpa.repository.rw.Ds139TemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
@Slf4j
public class OneHammerDagImpl implements OneHammerDag {
    private final Ds139Feign ds139Feign;
    private final Ds139NamespaceRepository namespaceRepository;
    private final Ds139TemplateRepository templateRepository;
    private final Ds139InstanceRepository instanceRepository;
    private final Dfs dfs;
    private final String DFS_PATH_OF_TEMPLATE_BASE = "vendor/dag/ds139/templates";

    @Value("${vendor.dag.ds139.token:unknown}")
    private String ds139Token;

    public void setDs139Token(String ds139Token) {
        this.ds139Token = ds139Token;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, value = "rwTransactionManager")
    public void createNamespace(String namespace, String description) throws OneHammerDagException {
        Ds139Feign.CreateNamespaceResp resp = ds139Feign.createNamespace(Collections.singletonMap(
                "token",
                ds139Token
        ), namespace, description);

        if (0 != resp.getCode()) {
            throw new OneHammerDagException(resp.getMsg());
        }
        namespaceRepository.save(
                Ds139Namespace.builder()
                        .namespace(namespace)
                        .description(description)
                        .nsId(resp.getData())
                        .build()
        );
    }

    @Override
    public List<String> namespaces() throws OneHammerDagException {
        return namespaceRepository.findAll().stream()
                .map(Ds139Namespace::getNamespace).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, value = "rwTransactionManager")
    public void deleteNamespace(String namespace) throws OneHammerDagException {
        Optional<Ds139Namespace> ds139Namespace = namespaceRepository.findByNamespace(namespace);
        if (!ds139Namespace.isPresent()) {
            log.warn("namespace : {} not exist.", namespace);
            return;
        }
        Ds139Feign.DeleteNamespaceResp resp = ds139Feign.deleteNamespace(Collections.singletonMap(
                "token",
                ds139Token
        ), Collections.singletonMap(
                "projectId", ds139Namespace.get().getNsId()
        ));

        if (0 != resp.getCode()) {
            throw new OneHammerDagException(resp.getMsg());
        }
        namespaceRepository.deleteById(ds139Namespace.get().getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, value = "rwTransactionManager")
    public String loadDagTemplate(String namespace, String templateName, String path, String description)
            throws OneHammerDagException {
        Optional<Ds139Template> ds139Template = templateRepository.findByNamespaceAndName(namespace, templateName);
        if (ds139Template.isPresent()) {
            return String.valueOf(ds139Template.get().getPath());
        }
        String templatePath = DFS_PATH_OF_TEMPLATE_BASE + "/" + namespace + "/" + templateName;
        try {
            // mkdir DFS_PATH_OF_TEMPLATE_BASE if not exists
            Path remote = Paths.get(DFS_PATH_OF_TEMPLATE_BASE, namespace);
            dfs.mkdir(remote);
            // save the template
            dfs.write(Paths.get(path), Paths.get(templatePath));
            templateRepository.save(
                    Ds139Template.builder()
                            .namespace(namespace)
                            .path(templatePath)
                            .description(description)
                            .build()
            );
        } catch (IOException e) {
            throw new OneHammerDagException(e.getMessage());
        }
        return templatePath;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, value = "rwTransactionManager")
    public void deleteDagTemplate(String namespace, String templateName) throws OneHammerDagException {
        Optional<Ds139Template> ds139Template = templateRepository.findByNamespaceAndName(namespace, templateName);
        if (ds139Template.isPresent()) {
            templateRepository.deleteById(ds139Template.get().getId());
            try {
                dfs.rm(Paths.get(ds139Template.get().getPath()));
                log.info("Drop template: {} success.", templateName);
            } catch (IOException e) {
                throw new OneHammerDagException(e.getMessage());
            }
        }
    }

    @Override
    public List<DagTemplate> templates(String namespace) throws OneHammerDagException {
        return templateRepository.findAllByNamespace(namespace).stream()
                .map(template -> DagTemplate.builder()
                        .namespace(namespace)
                        .name(template.getName())
                        .path(template.getPath())
                        .description(template.getDescription())
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, value = "rwTransactionManager")
    public String createDag(String namespace, String templateName, Map<String, String> config)
            throws OneHammerDagException {
        Path templatePath = null;
        Path updateTemplatePath = null;
        String template = DFS_PATH_OF_TEMPLATE_BASE + "/" + namespace + "/" + templateName;
        try {
            templatePath = dfs.read(Paths.get(template));
            updateTemplatePath = updateTemplateParams(templatePath, config);
            Ds139Feign.ImportDefinitionResp resp = ds139Feign.importDefinition(Collections.singletonMap(
                    "token",
                    ds139Token
            ), namespace, updateTemplatePath.toFile());
            if (0 != resp.getCode() && !resp.getData().isEmpty()) {
                throw new OneHammerDagException(resp.getMsg());
            }
            Long dagId = resp.getData().get(0);
            // enable the instance
            Ds139Feign.ToggleDefinitionResp toggleResp = ds139Feign.toggleDefinition(Collections.singletonMap(
                    "token",
                    ds139Token
            ), namespace, dagId, 1);
            if (0 != toggleResp.getCode()) {
                throw new OneHammerDagException(toggleResp.getMsg());
            }
            // save to db
            instanceRepository.save(Ds139Dag.builder()
                    .dagId(dagId).templateId(template).namespace(namespace).scheduleId(-1L).build());
            return String.valueOf(dagId);
        } catch (IOException e) {
            throw new OneHammerDagException(e.getMessage());
        } finally {
            try {
                if (templatePath != null) {
                    FileSystemUtils.deleteRecursively(templatePath.getParent());
                }
                if (updateTemplatePath != null) {
                    Files.deleteIfExists(updateTemplatePath);
                }
            } catch (IOException e) {
                log.error("Delete file : {} fail.", templatePath, e);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, value = "rwTransactionManager")
    public void updateDagSchedule(String namespace, String dagId, String cron) throws OneHammerDagException {
        Optional<Ds139Dag> instance = instanceRepository.findByInstanceId(Long.valueOf(dagId));
        if (!instance.isPresent()) {
            throw new OneHammerDagException("Instance id " + dagId + " not found.");
        }
        if (instance.get().getScheduleId() > 0) {
            throw new OneHammerDagException("This instance is already config, start it first.");
        }

        Ds139Feign.ConfigDefinitionScheduleResp resp = ds139Feign.configDefinitionSchedule(Collections.singletonMap(
                "token",
                ds139Token
        ), namespace, Ds139Feign.DolphinScheduler.builder()
                .schedule(
                        new GsonBuilder().create().toJson(
                                Ds139Feign.ScheduleDetail.builder()
                                        .startTime(
                                                LocalDateTime.now()
                                                        .atZone(ZoneId.of("Asia/Shanghai"))
                                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                        ).endTime(
                                                LocalDateTime.now().plusYears(100)
                                                        .atZone(ZoneId.of("Asia/Shanghai"))
                                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                        ).crontab(cron).build()
                        )
                )
                .failureStrategy("END")
                .warningType("NONE")
                .processInstancePriority("MEDIUM")
                .warningGroupId("0")
                .receivers("")
                .receiversCc("")
                .workerGroup("default")
                .processDefinitionId(dagId)
                .build());
        if (0 != resp.getCode()) {
            throw new OneHammerDagException(resp.getMsg());
        }

        // update instance
        instance.get().setScheduleId(resp.getData());
    }

    private Path updateTemplateParams(Path templatePath, Map<String, String> config) throws IOException {
        String content = Os.cat(templatePath);
        StringSubstitutor substitutor = new StringSubstitutor(config);
        return Os.saveToTmpFile(substitutor.replace(content));
    }

    @Override
    public void beginSchedule(String namespace, String dagId) throws OneHammerDagException {
        Optional<Ds139Dag> instance = instanceRepository.findByInstanceId(Long.valueOf(dagId));
        if (!instance.isPresent()) {
            throw new OneHammerDagException("Dag id " + dagId + " not found.");
        }
        Ds139Feign.ScheduleStatusResp resp = ds139Feign.enbaleSchedule(Collections.singletonMap(
                "token",
                ds139Token
        ), namespace, Collections.singletonMap("id", String.valueOf(instance.get().getScheduleId())));
        if (0 != resp.getCode()) {
            throw new OneHammerDagException(resp.getMsg());
        }
    }

    @Override
    public void stopSchedule(String namespace, String dagId) throws OneHammerDagException {
        Optional<Ds139Dag> instance = instanceRepository.findByInstanceId(Long.valueOf(dagId));
        if (!instance.isPresent()) {
            throw new OneHammerDagException("Instance id " + dagId + " not found.");
        }
        Ds139Feign.ScheduleStatusResp resp = ds139Feign.disableSchedule(Collections.singletonMap(
                "token",
                ds139Token
        ), namespace, Collections.singletonMap("id", String.valueOf(instance.get().getScheduleId())));
        if (0 != resp.getCode()) {
            throw new OneHammerDagException(resp.getMsg());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, value = "rwTransactionManager")
    public void deleteDag(String namespace, String dagId) throws OneHammerDagException {
        stopSchedule(namespace, dagId);
        // disable the instance
        Ds139Feign.ToggleDefinitionResp toggleResp = ds139Feign.toggleDefinition(Collections.singletonMap(
                "token",
                ds139Token
        ), namespace, Long.parseLong(dagId), 0);
        if (0 != toggleResp.getCode()) {
            throw new OneHammerDagException(toggleResp.getMsg());
        }
        // delete the instance
        ds139Feign.deleteDefinition(Collections.singletonMap(
                "token",
                ds139Token
        ), namespace, Collections.singletonMap("processDefinitionId", dagId));
        // update database info
        instanceRepository.deleteByNamespaceAndInstanceId(namespace, Long.parseLong(dagId));
    }

    @Override
    public String startDagOnce(String namespace, String dagId) throws OneHammerDagException {
        // 只启动一次, 启动之后没有快照的id, 这个比较麻烦, 需要反查一下
        Ds139Feign.ScheduleStatusResp resp = ds139Feign.startOnce(
                Collections.singletonMap(
                        "token",
                        ds139Token
                ),
                namespace,
                Ds139Feign.DolphinSchedulerOnce.builder()
                        .processDefinitionId(dagId)
                        .scheduleTime("")
                        .failureStrategy("END")
                        .warningType("NONE")
                        .warningGroupId("0")
                        .execType("")
                        .startNodeList("")
                        .taskDependType("TASK_POST")
                        .runMode("RUN_MODE_SERIAL")
                        .processInstancePriority("MEDIUM")
                        .receivers("")
                        .receiversCc("")
                        .workerGroup("default")
                        .build()
        );
        if (0 != resp.getCode()) {
            throw new OneHammerDagException(resp.getMsg());
        }
        // 查下快照id
        int maxTry = 3;

        while (maxTry-- > 0) {
            Ds139Feign.ListDagSnapshotsResp listDagSnapshotsResp = ds139Feign.listDagSnapshots(Collections.singletonMap(
                            "token",
                            ds139Token
                    ),
                    namespace,
                    ImmutableMap.of(
                            "processDefinitionId", dagId,
                            "pageNo", 1,
                            "pageSize", 1
                    ));
            if (0 != listDagSnapshotsResp.getCode()) {
                throw new OneHammerDagException(listDagSnapshotsResp.getMsg());
            }
            if (!listDagSnapshotsResp.getData().getTotalList().isEmpty()) {
                return String.valueOf(listDagSnapshotsResp.getData().getTotalList().get(0).getId());
            }
        }
        throw new OneHammerDagException("Fail to get snapshot id.");
    }

    @Override
    public DagTasksSnapshot displayDagSnapshot(String namespace, String dagId, String snapshotId) throws OneHammerDagException {
        Ds139Feign.ListDagTasksStatusResp resp = ds139Feign.displayDagSnapshotStatus(Collections.singletonMap(
                        "token",
                        ds139Token
                ),
                namespace,
                Collections.singletonMap(
                        "processInstanceId",
                        snapshotId
                )
        );
        if (0 != resp.getCode()) {
            throw new OneHammerDagException(resp.getMsg());
        }
        return DagTasksSnapshot.builder().dagId(dagId).state(resp.getData().getProcessInstanceState()).taskList(
                resp.getData().getTaskList().stream().map(d ->
                        DagTask.builder()
                                .taskType(d.getTaskType())
                                .taskSuccess(d.isTaskSuccess())
                                .state(d.getState())
                                .submitTime(d.getSubmitTime())
                                .startTime(d.getStartTime())
                                .endTime(d.getEndTime())
                                .name(d.getName())
                                .id(String.valueOf(d.getId()))
                                .build()).collect(Collectors.toList())
        ).build();
    }

    @Override
    public List<DagSnapshot> displayDagSnapshots(String namespace, String dagId, long pageNo, long pageSize) throws OneHammerDagException {
        Ds139Feign.ListDagSnapshotsResp resp = ds139Feign.listDagSnapshots(Collections.singletonMap(
                        "token",
                        ds139Token
                ),
                namespace,
                ImmutableMap.of(
                        "processDefinitionId", dagId,
                        "pageNo", pageNo,
                        "pageSize", pageSize
                )
        );
        return resp.getData().getTotalList().stream().map(
                d -> DagSnapshot.builder()
                        .snapshotId(String.valueOf(d.getId()))
                        .dagId(dagId)
                        .state(d.getState())
                        .startTime(d.getStartTime())
                        .endTime(d.getEndTime())
                        .name(d.getName())
                        .duration(d.getDuration())
                        .build()
        ).collect(Collectors.toList());
    }

}

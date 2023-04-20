package com.lueing.oh.dag.ds;

import com.google.gson.GsonBuilder;
import com.lueing.oh.commons.os.Os;
import com.lueing.oh.dag.DagTemplate;
import com.lueing.oh.dag.OneHammerDag;
import com.lueing.oh.dag.OneHammerDagException;
import com.lueing.oh.dag.ds.feign.Ds139Feign;
import com.lueing.oh.dfs.Dfs;
import com.lueing.oh.jpa.entity.Ds139Instance;
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
    public String createInstanceFromDagTemplate(String namespace, String templateName, Map<String, String> config)
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
            Long instanceId = resp.getData().get(0);
            // enable the instance
            Ds139Feign.ToggleDefinitionResp toggleResp = ds139Feign.toggleDefinition(Collections.singletonMap(
                    "token",
                    ds139Token
            ), namespace, instanceId, 1);
            if (0 != toggleResp.getCode()) {
                throw new OneHammerDagException(toggleResp.getMsg());
            }
            // save to db
            instanceRepository.save(Ds139Instance.builder()
                    .instanceId(instanceId).templateId(template).namespace(namespace).scheduleId(-1L).build());
            return String.valueOf(resp.getData().get(0));
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
    public void updateInstanceSchedule(String namespace, String instanceId, String cron) throws OneHammerDagException {
        Optional<Ds139Instance> instance = instanceRepository.findByInstanceId(Long.valueOf(instanceId));
        if (!instance.isPresent()) {
            throw new OneHammerDagException("Instance id " + instanceId + " not found.");
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
                .processDefinitionId(instanceId)
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
    public void startInstance(String namespace, String instanceId) throws OneHammerDagException {
        Optional<Ds139Instance> instance = instanceRepository.findByInstanceId(Long.valueOf(instanceId));
        if (!instance.isPresent()) {
            throw new OneHammerDagException("Instance id " + instanceId + " not found.");
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
    public void stopInstance(String namespace, String instanceId) throws OneHammerDagException {
        Optional<Ds139Instance> instance = instanceRepository.findByInstanceId(Long.valueOf(instanceId));
        if (!instance.isPresent()) {
            throw new OneHammerDagException("Instance id " + instanceId + " not found.");
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
    public void deleteInstance(String namespace, String instanceId) throws OneHammerDagException {

    }
}

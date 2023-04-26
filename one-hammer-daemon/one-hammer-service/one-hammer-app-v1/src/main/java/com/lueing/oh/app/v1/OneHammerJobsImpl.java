package com.lueing.oh.app.v1;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.lueing.oh.app.api.OneHammerDags;
import com.lueing.oh.app.api.OneHammerException;
import com.lueing.oh.app.api.OneHammerJobs;
import com.lueing.oh.app.api.OneHammerStreams;
import com.lueing.oh.app.api.vo.OneHammerJobStatus;
import com.lueing.oh.app.api.vo.OneHammerJobVO;
import com.lueing.oh.commons.exception.BusinessException;
import com.lueing.oh.commons.os.Os;
import com.lueing.oh.dfs.Dfs;
import com.lueing.oh.jpa.repository.rw.OneHammerJobRepository;
import com.lueing.oh.pojo.OneHammerJob;
import com.lueing.oh.pojo.OneHammerStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
@Slf4j
public class OneHammerJobsImpl implements OneHammerJobs {
    private final Dfs dfs;
    private final OneHammerDags oneHammerDags;
    private final OneHammerStreams oneHammerStreams;
    private final OneHammerJobRepository oneHammerJobRepository;
    private static final Yaml yamlParser = new Yaml();

    @Override
    @Transactional(rollbackFor = Exception.class, value = "rwTransactionManager")
    public String createOneHammerJob(String yamlJob) {
        OneHammerJob oneHammerJob = yamlParser.loadAs(yamlJob, OneHammerJob.class);
        Path yamlOrig = null;
        Path yamlLocalPath = null;
        try {
            // do we should override the hammer
            Optional<com.lueing.oh.jpa.entity.OneHammerJob> hammer
                    = oneHammerJobRepository.findByNamespaceAndName(oneHammerJob.getMetadata().getNamespace(),
                    oneHammerJob.getMetadata().getName());

            if (hammer.isPresent()) {
                yamlOrig = dfs.read(Paths.get(hammer.get().getYaml()));
                OneHammerJob origHammer = yamlParser.loadAs(Os.cat(yamlOrig), OneHammerJob.class);
                this.stop(origHammer);
                oneHammerJobRepository.deleteById(hammer.get().getId());
            }
            this.start(oneHammerJob);

            String remotePath = Joiner.on('/').join(
                    ImmutableList.of(
                            "hammers",
                            oneHammerJob.getMetadata().getNamespace(),
                            oneHammerJob.getMetadata().getName()));
            // save job definition to dfs
            yamlLocalPath = Os.saveToTmpFile(yamlParser.dump(oneHammerJob));
            dfs.mkdir(Paths.get("hammers", oneHammerJob.getMetadata().getNamespace()));
            dfs.write(yamlLocalPath, Paths.get(remotePath));
            // 保存hammer到数据库
            com.lueing.oh.jpa.entity.OneHammerJob savedJob = oneHammerJobRepository.save(com.lueing.oh.jpa.entity.OneHammerJob.builder()
                    .kind(oneHammerJob.getKind())
                    .apiVersion(oneHammerJob.getApiVersion())
                    .description(oneHammerJob.getMetadata().getDescription())
                    .catalog(oneHammerJob.getMetadata().getCatalog())
                    .name(oneHammerJob.getMetadata().getName())
                    .namespace(oneHammerJob.getMetadata().getNamespace())
                    .expectedStatus(OneHammerJob.ExpectedStatus.RUNNING)
                    .yaml(remotePath)
                    .build());
            return savedJob.getId();
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        } finally {
            if (yamlOrig != null) {
                try {
                    Files.deleteIfExists(yamlOrig);
                } catch (IOException ignored) {
                }
            }
            if (yamlLocalPath != null) {
                try {
                    Files.deleteIfExists(yamlLocalPath);
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public List<OneHammerJobVO> oneHammerJobs() {
        return oneHammerJobRepository.findAll().stream().map(v -> OneHammerJobVO
                .builder()
                .apiVersion(v.getApiVersion())
                .expectedStatus(v.getExpectedStatus())
                .kind(v.getKind())
                .createdDate(v.getCreatedDate())
                .description(v.getDescription())
                .lastModifiedDate(v.getLastModifiedDate())
                .name(v.getName())
                .namespace(v.getNamespace())
                .catalog(v.getCatalog())
                .id(v.getId())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public void start(OneHammerJob hammerJob) throws OneHammerException {
        if (null != hammerJob.getSpec().getDags()) {
            for (com.lueing.oh.pojo.OneHammerDag dag : hammerJob.getSpec().getDags()) {
                // create dag task if not exists
                oneHammerDags.createIfNotExists(hammerJob, dag);
                oneHammerDags.start(hammerJob, dag);
            }
        }
        if (null != hammerJob.getSpec().getStreams()) {
            for (OneHammerStream stream : hammerJob.getSpec().getStreams()) {
                oneHammerStreams.start(hammerJob, stream);
            }
        }
    }

    @Override
    public void stop(OneHammerJob hammerJob) throws OneHammerException {
        if (null != hammerJob.getSpec().getDags()) {
            for (com.lueing.oh.pojo.OneHammerDag dag : hammerJob.getSpec().getDags()) {
                oneHammerDags.stop(hammerJob, dag);
                dag.setDagId(null);
            }
        }
        if (hammerJob.getSpec().getStreams() != null) {
            for (OneHammerStream stream : hammerJob.getSpec().getStreams()) {
                oneHammerStreams.stop(hammerJob, stream);
            }
        }
    }

    @Override
    public OneHammerJobStatus query(String hammerId) {
        return null;
    }
}

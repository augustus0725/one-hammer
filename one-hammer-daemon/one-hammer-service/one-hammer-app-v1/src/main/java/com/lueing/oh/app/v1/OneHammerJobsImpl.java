package com.lueing.oh.app.v1;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.lueing.oh.app.api.OneHammerJobs;
import com.lueing.oh.app.api.vo.OneHammerJobStatus;
import com.lueing.oh.app.api.vo.OneHammerJobVO;
import com.lueing.oh.commons.exception.BusinessException;
import com.lueing.oh.commons.os.Os;
import com.lueing.oh.dag.OneHammerDag;
import com.lueing.oh.dfs.Dfs;
import com.lueing.oh.jpa.repository.rw.OneHammerJobRepository;
import com.lueing.oh.pojo.OneHammerJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
@Slf4j
public class OneHammerJobsImpl implements OneHammerJobs {
    private final Dfs dfs;
    private final OneHammerDag oneHammerDag;
    private final OneHammerJobRepository oneHammerJobRepository;
    private static final Yaml yamlParser = new Yaml();

    @Override
    public void createOneHammerJob(String yamlJob) {
        OneHammerJob oneHammerJob = yamlParser.loadAs(yamlJob, OneHammerJob.class);
        try {
            // do we should override the hammer
            Optional<com.lueing.oh.jpa.entity.OneHammerJob> hammer
                    = oneHammerJobRepository.findByNamespaceAndName(oneHammerJob.getMetadata().getNamespace(),
                    oneHammerJob.getMetadata().getName());

            if (hammer.isPresent()) {
                // 判断是不是需要覆盖
                Path yamlOrig = dfs.read(Paths.get(hammer.get().getYaml()));
                OneHammerJob origHammer = yamlParser.loadAs(Os.cat(yamlOrig), OneHammerJob.class);

                if (Objects.equals(origHammer, oneHammerJob)) {
                    log.warn("This job: {} already exist and no changes in the yaml.",
                            origHammer.getMetadata().getName());
                    return;
                }
                // TODO 先做删除hammer, 很多复杂状态的删除, 然后统一到下面的创建工作, 要做到幂等
                oneHammerJobRepository.deleteById(hammer.get().getId());
            }

            String remotePath = Joiner.on('/').join(ImmutableList.of(
                    "hammers",
                    oneHammerJob.getMetadata().getNamespace(),
                    oneHammerJob.getMetadata().getName()));
            if (!hammer.isPresent()) {
                // save job definition to dfs
                Path yamlLocalPath = Os.saveToTmpFile(yamlJob);

                dfs.mkdir(Paths.get("hammers", oneHammerJob.getMetadata().getNamespace()));
                dfs.write(yamlLocalPath, Paths.get(remotePath));
                // TODO 构建hammer的运行时
            }
            // 保存hammer到数据库
            oneHammerJobRepository.save(com.lueing.oh.jpa.entity.OneHammerJob.builder()
                    .kind(oneHammerJob.getKind())
                    .apiVersion(oneHammerJob.getApiVersion())
                    .description(oneHammerJob.getMetadata().getDescription())
                    .catalog(oneHammerJob.getMetadata().getCatalog())
                    .name(oneHammerJob.getMetadata().getName())
                    .namespace(oneHammerJob.getMetadata().getNamespace())
                    .expectedStatus(OneHammerJob.ExpectedStatus.RUNNING)
                    .yaml(remotePath)
                    .build());
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public List<OneHammerJobVO> oneHammerJobs() {
        return null;
    }

    @Override
    public void start(String hammerId) {

    }

    @Override
    public void stop(String hammerId) {

    }

    @Override
    public OneHammerJobStatus query(String hammerId) {
        return null;
    }
}

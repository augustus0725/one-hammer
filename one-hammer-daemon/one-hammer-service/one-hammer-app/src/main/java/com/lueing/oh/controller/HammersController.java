package com.lueing.oh.controller;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.lueing.oh.app.api.OneHammerException;
import com.lueing.oh.app.api.OneHammerJobs;
import com.lueing.oh.app.api.vo.OneHammerJobVO;
import com.lueing.oh.commons.exception.BusinessException;
import com.lueing.oh.commons.os.Os;
import com.lueing.oh.commons.standard.RestResponse;
import com.lueing.oh.dfs.Dfs;
import com.lueing.oh.jpa.entity.OneHammerJob;
import com.lueing.oh.jpa.repository.rw.OneHammerJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hammers")
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
public class HammersController {
    private final OneHammerJobs oneHammerJobs;
    private final OneHammerJobRepository hammerJobRepository;
    private static final Yaml yamlParser = new Yaml();
    private final Dfs dfs;

    @PostMapping("/apply")
    public RestResponse<String> apply(@RequestParam("file") MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            return RestResponse.ok(oneHammerJobs.createOneHammerJob(CharStreams.toString(
                    new InputStreamReader(stream, Charsets.UTF_8))));
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @PutMapping("/start")
    public RestResponse<String> start(@RequestParam String id) {
        Optional<OneHammerJob> optionalJob = hammerJobRepository.findById(id);
        if (optionalJob.isPresent()) {
            Path yaml = null;
            try {
                yaml = dfs.read(Paths.get(optionalJob.get().getYaml()));
                com.lueing.oh.pojo.OneHammerJob oneHammerJob = yamlParser.loadAs(Os.cat(yaml),
                        com.lueing.oh.pojo.OneHammerJob.class);
                oneHammerJobs.start(oneHammerJob);
            } catch (Exception e) {
                throw new BusinessException(e.getMessage());
            }
        }
        return RestResponse.ok("success");
    }

    @PutMapping("/stop")
    public RestResponse<String> stop(@RequestParam String id) {
        Optional<OneHammerJob> optionalJob = hammerJobRepository.findById(id);
        if (optionalJob.isPresent()) {
            Path yaml = null;
            try {
                yaml = dfs.read(Paths.get(optionalJob.get().getYaml()));
                com.lueing.oh.pojo.OneHammerJob oneHammerJob = yamlParser.loadAs(Os.cat(yaml),
                        com.lueing.oh.pojo.OneHammerJob.class);
                oneHammerJobs.stop(oneHammerJob);
            } catch (Exception e) {
                throw new BusinessException(e.getMessage());
            } finally {
                if (yaml != null) {
                    try {
                        FileSystemUtils.deleteRecursively(yaml.getParent());
                    } catch (IOException ignore) {
                    }
                }
            }

        }
        return RestResponse.ok("success");
    }

    @GetMapping("")
    public RestResponse<List<OneHammerJobVO>> hammers() {
        try {
            return RestResponse.ok(oneHammerJobs.oneHammerJobs());
        } catch (OneHammerException e) {
            throw new BusinessException(e.getMessage());
        }
    }
}

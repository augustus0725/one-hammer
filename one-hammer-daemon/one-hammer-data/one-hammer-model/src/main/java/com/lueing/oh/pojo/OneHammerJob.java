package com.lueing.oh.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OneHammerJob {
    private String apiVersion;
    private String kind;
    private Metadata metadata;
    private String expectedStatus;

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class Metadata {
        private String name;
        private String description;
        private String namespace;
        private String catalog;
        private List<String> labels;
        // 用户自定义的信息
        private Map<String, String> custom;
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class Spec {
        // Scheduled(Timer/Cron) DAG Task
        private List<OneHammerBatchTask> dags;
        // Stream Task (Always running...)
        private List<OneHammerStreamTask> streams;
    }

    private Spec spec;
}

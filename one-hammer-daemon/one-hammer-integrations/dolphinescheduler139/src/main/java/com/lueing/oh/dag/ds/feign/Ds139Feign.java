package com.lueing.oh.dag.ds.feign;

import feign.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface Ds139Feign {
    @RequestLine("POST /dolphinscheduler/projects/create")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    CreateNamespaceResp createNamespace(@HeaderMap Map<String, String> headers, @Param("projectName") String namespace,
                               @Param("description") String description);

    @Getter
    @ToString
    class CreateNamespaceResp {
        private Integer code;
        private String msg;
        private Long data;
    }

    @RequestLine("GET /dolphinscheduler/projects/delete")
    DeleteNamespaceResp deleteNamespace(@HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> queryMap);

    @Getter
    @ToString
    class DeleteNamespaceResp {
        private Integer code;
        private String msg;
        private Long data;
    }

    @RequestLine("POST /dolphinscheduler/projects/import-definition")
    @Headers("Content-Type: multipart/form-data")
    ImportDefinitionResp importDefinition(@HeaderMap Map<String, String> headers, @Param("projectName") String namespace,
                                          @Param("file") File definition);

    @Getter
    @ToString
    class ImportDefinitionResp {
        private Integer code;
        private String msg;
        private List<Long> data;
    }

    @RequestLine("POST /dolphinscheduler/projects/{namespace}/process/release")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    ToggleDefinitionResp toggleDefinition(@HeaderMap Map<String, String> headers, @Param("namespace") String namespace,
                              @Param("processId") long definitionId, @Param("releaseState") int status);

    @Getter
    @ToString
    class ToggleDefinitionResp {
        private Integer code;
        private String msg;
        private Integer data;
    }

    @RequestLine("POST /dolphinscheduler/projects/{namespace}/schedule/create")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    ConfigDefinitionScheduleResp configDefinitionSchedule(@HeaderMap Map<String, String> headers, @Param("namespace") String namespace,
                                       DolphinScheduler scheduler);

    @Data
    class ConfigDefinitionScheduleResp {
        private Integer code;
        private String msg;
        private Long data;
    }

    @Data
    @SuperBuilder
    class ScheduleDetail {
        private String startTime;
        private String endTime;
        private String crontab;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    class DolphinScheduler {
        private String schedule;
        private String failureStrategy;
        private String warningType;
        private String processInstancePriority;
        private String warningGroupId;
        private String receivers;
        private String receiversCc;
        private String workerGroup;
        private String processDefinitionId;
    }

    @RequestLine("POST /dolphinscheduler/projects/{namespace}/schedule/online")
    ScheduleStatusResp enbaleSchedule(@HeaderMap Map<String, String> headers, @Param("namespace") String namespace,
                                      @QueryMap Map<String, Object> queryMap);

    @RequestLine("POST /dolphinscheduler/projects/{namespace}/schedule/offline")
    ScheduleStatusResp disableSchedule(@HeaderMap Map<String, String> headers, @Param("namespace") String namespace,
                                       @QueryMap Map<String, Object> queryMap);

    @Data
    class ScheduleStatusResp {
        private Integer code;
        private String msg;
        private Integer data;
    }

    @RequestLine("GET /dolphinscheduler/projects/{namespace}/process/delete")
    DeleteDefinitionResp deleteDefinition(@HeaderMap Map<String, String> headers, @Param("namespace") String namespace,
                                       @QueryMap Map<String, Object> queryMap);

    @Data
    class DeleteDefinitionResp {
        private Integer code;
        private String msg;
        private Integer data;
    }

    @RequestLine("POST /dolphinscheduler/projects/{namespace}/executors/start-process-instance")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    ScheduleStatusResp startOnce(@HeaderMap Map<String, String> headers, @Param("namespace") String namespace,
                                 DolphinSchedulerOnce scheduler);

    @Getter
    @Setter
    @NoArgsConstructor
    @SuperBuilder
    class DolphinSchedulerOnce {
        private String scheduleTime;
        private String failureStrategy;
        private String warningType;
        private String processInstancePriority;
        private String warningGroupId;
        private String receivers;
        private String execType;
        private String startNodeList;
        private String taskDependType;
        private String runMode;
        private String receiversCc;
        private String workerGroup;
        private String processDefinitionId;
    }

    @RequestLine("GET /dolphinscheduler/projects/{namespace}/instance/task-list-by-process-id")
    ListDagTasksStatusResp displayDagSnapshotStatus(@HeaderMap Map<String, String> headers, @Param("namespace") String namespace,
                                                    @QueryMap Map<String, Object> queryMap);

    @Getter
    @ToString
    class ListDagTasksStatusResp {
        private Integer code;
        private String msg;
        private Ds139DagStatus data;

    }

    @Getter
    @ToString
    class Ds139DagStatus {
        private String processInstanceState;
        private List<Ds139TaskStatus> taskList;
    }

    @Getter
    @ToString
    class Ds139TaskStatus {
        private Long id;
        private String name;
        private String taskType;
        private String state;
        private String submitTime;
        private String startTime;
        private String endTime;
        private boolean taskSuccess;
    }

    // ?pageSize=256&pageNo=1
    @RequestLine("GET /dolphinscheduler/projects/{namespace}/instance/list-paging")
    ListDagSnapshotsResp listDagSnapshots(@HeaderMap Map<String, String> headers, @Param("namespace") String namespace,
                                              @QueryMap Map<String, Object> queryMap);

    @Getter
    @ToString
    class ListDagSnapshotsResp {
        private Integer code;
        private String msg;
        private Ds139DagSnapshotsData data;
    }

    @Getter
    @ToString
    class Ds139DagSnapshotsData {
        private List<Ds139DagSnapshot> totalList;
    }

    @Getter
    @ToString
    class Ds139DagSnapshot {
        private Long id;
        private Long processDefinitionId;
        private String state;
        private String startTime;
        private String endTime;
        private String name;
        private String duration;
    }
}

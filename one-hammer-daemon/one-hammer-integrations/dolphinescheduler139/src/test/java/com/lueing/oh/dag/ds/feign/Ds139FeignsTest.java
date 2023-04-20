package com.lueing.oh.dag.ds.feign;

import com.google.gson.GsonBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.*;

@Ignore
public class Ds139FeignsTest {
    private static final String TOKEN = "c533570713bc76a546673aedee5d6c07";

    @Test
    public void testCreateNamespace() {
        Ds139Feign ds139Feign = Ds139Feigns.create("http://192.168.0.16:12345");

        Ds139Feign.CreateNamespaceResp resp = ds139Feign.createNamespace(Collections.singletonMap(
                "token",
                TOKEN
        ), "feign-demo", "it's nice.");

        assertEquals(new Integer(0), resp.getCode());
    }

    @Test
    public void testDeleteNamespace() {
        Ds139Feign ds139Feign = Ds139Feigns.create("http://192.168.0.16:12345");

        Ds139Feign.DeleteNamespaceResp resp = ds139Feign.deleteNamespace(Collections.singletonMap(
                "token",
                TOKEN
        ), Collections.singletonMap(
                "projectId", 2
        ));
        assertEquals(new Integer(0), resp.getCode());
    }

    @Test
    public void testImportDefinition() {
        Ds139Feign ds139Feign = Ds139Feigns.create("http://192.168.0.16:12345");

        Ds139Feign.ImportDefinitionResp resp = ds139Feign.importDefinition(Collections.singletonMap(
                        "token",
                        TOKEN
                ), "feign-demo",
                new File(Objects.requireNonNull(getClass().getClassLoader().getResource("dag01.json")).getFile())
        );
        assertEquals(new Integer(0), resp.getCode());
    }

    @Test
    public void testToggleDefinition() {
        Ds139Feign ds139Feign = Ds139Feigns.create("http://192.168.0.16:12345");

        Ds139Feign.ToggleDefinitionResp resp = ds139Feign.toggleDefinition(Collections.singletonMap(
                "token",
                TOKEN
        ), "feign-demo", 2, 1);
        assertEquals(new Integer(0), resp.getCode());
    }

    @Test
    public void testConfigDefinitionSchedule() {
        Ds139Feign ds139Feign = Ds139Feigns.create("http://192.168.0.16:12345");

        Ds139Feign.ConfigDefinitionScheduleResp resp = ds139Feign.configDefinitionSchedule(Collections.singletonMap(
                "token",
                TOKEN
        ), "feign-demo", Ds139Feign.DolphinScheduler.builder()
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
                                        ).crontab("0 0 * * * ? *").build()
                        )
                )
                .failureStrategy("END")
                .warningType("NONE")
                .processInstancePriority("MEDIUM")
                .warningGroupId("0")
                .receivers("")
                .receiversCc("")
                .workerGroup("default")
                .processDefinitionId("2")
                .build());
        assertEquals(new Integer(0), resp.getCode());
    }

    @Test
    public void testEnableSchedule() {
        Ds139Feign ds139Feign = Ds139Feigns.create("http://192.168.0.16:12345");

        Ds139Feign.ScheduleStatusResp resp = ds139Feign.enbaleSchedule(Collections.singletonMap(
                "token",
                TOKEN
        ), "feign-demo", Collections.singletonMap("id", "1"));
        assertEquals(new Integer(0), resp.getCode());
    }

    @Test
    public void testDisableSchedule() {
        Ds139Feign ds139Feign = Ds139Feigns.create("http://192.168.0.16:12345");

        Ds139Feign.ScheduleStatusResp resp = ds139Feign.disableSchedule(Collections.singletonMap(
                "token",
                TOKEN
        ), "feign-demo", Collections.singletonMap("id", "1"));
        assertEquals(new Integer(0), resp.getCode());
    }

    @Test
    public void testStartDefinitionOnce() {
        Ds139Feign ds139Feign = Ds139Feigns.create("http://192.168.0.16:12345");

        Ds139Feign.ScheduleStatusResp resp = ds139Feign.startOnce(
                Collections.singletonMap(
                        "token",
                        TOKEN
                ),
                "feign-demo",
                Ds139Feign.DolphinSchedulerOnce.builder()
                        .processDefinitionId("2")
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
        assertEquals(new Integer(0), resp.getCode());
    }

}

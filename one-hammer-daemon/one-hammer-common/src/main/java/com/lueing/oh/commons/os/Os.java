package com.lueing.oh.commons.os;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
@Slf4j
public class Os {
    public static final String WINDOWS = "Windows_NT";
    public static final String PROPERTY_OS = "OS";

    private Os() {
        throw new IllegalStateException("Utility class ,not to be instantiated");
    }


    public static void shell(CommandLine cmd, ExecuteWatchdog watchdog) throws IOException {
        execute(cmd, watchdog);
    }

    public static String shellWithResult(CommandLine cmd, ExecuteWatchdog watchdog) throws IOException {
        log.info("execute cmd: {}", cmd.toString());
        ByteArrayOutputStream stdOut = execute(cmd, watchdog);
        return stdOut.toString(StandardCharsets.UTF_8.name());
    }

    private static ByteArrayOutputStream execute(CommandLine cmd, ExecuteWatchdog watchdog) throws IOException {
        DefaultExecutor executor = new DefaultExecutor();
        ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(stdOut);
        executor.setStreamHandler(streamHandler);

        if (null != watchdog) {
            executor.setWatchdog(watchdog);
        }
        try {
            log.info("command : {}", cmd);
            if (WINDOWS.equals(System.getenv().get(PROPERTY_OS))) {
                log.warn("The program may have problems if the platform is not linux");
            }
            executor.execute(cmd);
        } catch (Exception e) {
            log.error("Command: {} execute fail: {}", cmd, stdOut.toString(StandardCharsets.UTF_8.name()), e);
            throw e;
        }
        return stdOut;
    }

    public static void touchWithContent(Path path, String content) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(path.toString())) {
            byte[] strToBytes = content.getBytes(StandardCharsets.UTF_8);
            outputStream.write(strToBytes);
        } catch (IOException e) {
            log.error("touch file failed with reason : {}", e.getMessage());
            throw e;
        }
    }

    public static String cat(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    public static Path saveToTmpFile(String content) throws IOException {
        Path tmpSource;
        tmpSource = Files.createTempFile("my-", ".tmp");
        Files.write(tmpSource, content.getBytes(StandardCharsets.UTF_8));
        return tmpSource;
    }
}

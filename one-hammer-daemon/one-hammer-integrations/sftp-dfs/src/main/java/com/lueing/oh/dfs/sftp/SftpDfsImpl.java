package com.lueing.oh.dfs.sftp;

import com.google.common.base.Strings;
import com.lueing.oh.commons.os.Os;
import com.lueing.oh.dfs.Dfs;
import org.apache.commons.exec.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SftpDfsImpl implements Dfs {
    private final String sshUser;
    private final String sshPass;
    private final String sshHost;
    private final String baseDir;

    public SftpDfsImpl(String sshUser, String sshPass, String sshHost, String baseDir) {
        this.sshUser = sshUser;
        this.sshPass = sshPass;
        this.sshHost = sshHost;
        this.baseDir = baseDir;
        // not support root user for safety
        if (Strings.isNullOrEmpty(sshUser) || Strings.isNullOrEmpty(sshPass) || "root".equals(sshUser)) {
            String message = "root".equals(sshUser) ? "The user should not be root"
                    : "sshUser & sshPass should not be empty or null";
            throw new SftpDfsException(message);
        }
    }

    @Override
    public void mkdir(Path remoteDir) throws IOException {
        Os.shell(CommandLine.parse(
                createSshCommand("mkdir -p " + Paths.get(baseDir, remoteDir.toString()))
        ), null);
    }

    private String createSshCommand(String command) {
        return String.format("sshpass -p '%s' ssh %s@%s -o StrictHostKeyChecking=no %s",
                sshPass,
                sshUser,
                sshHost,
                command);
    }

    private String createScpPush(String source, String dest) {
        return String.format("sshpass -p '%s' scp -o StrictHostKeyChecking=no %s %s@%s:%s",
                sshPass,
                source,
                sshUser,
                sshHost,
                Paths.get(baseDir, dest));
    }

    private String createScpPull(String remote, String local) {
        return String.format("sshpass -p '%s' scp -o StrictHostKeyChecking=no %s@%s:%s %s",
                sshPass,
                sshUser,
                sshHost,
                Paths.get(baseDir, remote),
                local);
    }

    @Override
    public void rm(Path remote) throws IOException {
        Os.shell(CommandLine.parse(
                createSshCommand("rm -rf " + Paths.get(baseDir, remote.toString()))
        ), null);
    }

    @Override
    public void write(Path local, Path remote) throws IOException {
        Os.shell(CommandLine.parse(
                createScpPush(local.toString(), remote.toString())
        ), null);
    }

    @Override
    public Path read(Path remote) throws IOException {
        Path path = Files.createTempDirectory("hammer-");
        Os.shell(CommandLine.parse(
                createScpPull(remote.toString(), path.toString())
        ), null);
        return Paths.get(path.toString(), remote.getFileName().toString());
    }

    @Override
    public Path copy(Path remote, Path local) throws IOException {
        Os.shell(CommandLine.parse(
                createScpCopy(remote.toString(), local.toString())
        ), null);
        return null;
    }

    private String createScpCopy(String remote, String local) {
        return String.format("sshpass -p '%s' scp -r -o StrictHostKeyChecking=no %s@%s:%s %s",
                sshPass,
                sshUser,
                sshHost,
                Paths.get(baseDir, remote),
                local);
    }
}

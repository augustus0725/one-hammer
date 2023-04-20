package com.lueing.oh.dfs.sftp;

import com.lueing.oh.dfs.Dfs;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Ignore
public class SftpDfsImplTest {
    @Test
    public void testPaths() {
        assertEquals(File.separator + "root" + File.separator + "sabo"
                + File.separator + "a" + File.separator + "b", Paths.get("/root/sabo", "a/b").toString());
    }

    @Test
    public void testMkdir() throws IOException {
        Dfs dfs = new SftpDfsImpl("vagrant", "vagrant", "192.168.0.16", "/home/vagrant");

        dfs.mkdir(Paths.get("a/b"));
    }

    @Test
    public void testRm() throws IOException {
        Dfs dfs = new SftpDfsImpl("vagrant", "vagrant", "192.168.0.16", "/home/vagrant");

        dfs.rm(Paths.get("a/b"));
        dfs.rm(Paths.get("a"));
    }

    @Test
    public void testWrite() throws IOException {
        Dfs dfs = new SftpDfsImpl("vagrant", "vagrant", "192.168.0.16", "/home/vagrant");

        Files.list(Paths.get(".")).forEach(p -> System.out.println(p));

        dfs.write(Paths.get("./pom.xml"), Paths.get(""));
    }

    @Test
    public void testRead() throws IOException {
        Dfs dfs = new SftpDfsImpl("vagrant", "vagrant", "192.168.0.16", "/home/vagrant");

        Path remotePath = dfs.read(Paths.get("./pom.xml"));
        assertTrue(remotePath.toFile().exists());
    }

}

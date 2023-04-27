package com.lueing.oh.dfs;

import java.io.IOException;
import java.nio.file.Path;

public interface Dfs {
    void mkdir(Path remoteDir) throws IOException;
    void rm(Path remote) throws IOException;
    void write(Path local, Path remote) throws IOException;
    Path read(Path remote) throws IOException;
    Path copy(Path remote, Path local) throws IOException;
}

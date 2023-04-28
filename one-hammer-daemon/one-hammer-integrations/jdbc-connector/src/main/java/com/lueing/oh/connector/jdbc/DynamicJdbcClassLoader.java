package com.lueing.oh.connector.jdbc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class DynamicJdbcClassLoader extends URLClassLoader {
    public DynamicJdbcClassLoader(String path) throws IOException {
        super(new URL[]{}, Thread.currentThread().getContextClassLoader().getParent());
        loadResource(path);
    }

    private void loadResource(String path) throws IOException {
        tryLoadJarInDir(path);
    }

    private void tryLoadJarInDir(String path) throws IOException {
        File dir = new File(path);

        if (dir.exists() && dir.isDirectory()) {
            File[] subDirs = dir.listFiles();
            if (null != subDirs) {
                for (File file : subDirs) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        this.addURL(file);
                    }
                }
            }
        }
    }

    private void addURL(File file) throws IOException {
        super.addURL(file.toURI().toURL());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    /**
     * loadClass 无法找到最后会调用 findClass来找
     *
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (Exception ignore) {
            return DynamicJdbcClassLoader.class.getClassLoader().loadClass(name);
        }
    }


}

package com.lueing.oh.config;

import com.lueing.oh.commons.jdbc.JdbcProxy;
import com.lueing.oh.dfs.Dfs;
import com.lueing.oh.dfs.sshpass.SshpassDfsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetaConfiguration {
    @Bean
    public Dfs dfs(@Value("${vendor.dfs.sftp.user}") String sshUser,
                   @Value("${vendor.dfs.sftp.password}") String sshPass,
                   @Value("${vendor.dfs.sftp.host}") String sshHost,
                   @Value("${vendor.dfs.sftp.base}") String baseDir) {
        return new SshpassDfsImpl(sshUser, sshPass, sshHost, baseDir);
    }

    @Bean
    public JdbcProxy jdbcProxy() {
        return new JdbcProxy();
    }
}

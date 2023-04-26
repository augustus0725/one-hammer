package com.lueing.oh.app.config;

import com.lueing.oh.dag.ds.feign.Ds139Feign;
import com.lueing.oh.dag.ds.feign.Ds139Feigns;
import com.lueing.oh.dfs.Dfs;
import com.lueing.oh.dfs.sftp.SftpDfsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public Ds139Feign ds139Feign(@Value("vender.dag.ds139.host") String host) {
        return Ds139Feigns.create(host);
    }

    @Bean
    public Dfs dfs(@Value("vender.dfs.sftp.user") String sshUser,
                   @Value("vender.dfs.sftp.password") String sshPass,
                   @Value("vender.dfs.sftp.host") String sshHost,
                   @Value("vender.dfs.sftp.base") String baseDir) {
        return new SftpDfsImpl(sshUser, sshPass, sshHost, baseDir);
    }
}

package com.lueing.oh.app.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {com.lueing.oh.app.common.config.RepositoryConfig.ReadOnly.REPOSITORY_PACKAGE},
        entityManagerFactoryRef = "rEntityManagerFactory",
        transactionManagerRef = "rTransactionManager",
        includeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, classes = {com.lueing.oh.app.common.config.RepositoryConfig.ReadOnly.class})}
)
public class ReadJpaConfiguration {
    @Resource
    private HibernateProperties hibernateProperties;

    @Resource
    private JpaProperties jpaProperties;

    @Bean(name = "rDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "rEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("rDataSource") DataSource dataSource) {
        Map<String, String> rProperty = new HashMap<>(jpaProperties.getProperties());
        rProperty.put("hibernate.hbm2ddl.auto", "none");
        return builder
                .dataSource(dataSource)
                .packages(
                        com.lueing.oh.app.common.config.RepositoryConfig.JPA_ENTITY_HOME
                )
                .persistenceUnit("r-emf")
                .properties(hibernateProperties
                        .determineHibernateProperties(rProperty, new HibernateSettings()))
                .build();
    }

    @Bean(name = "rTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("rEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }
}

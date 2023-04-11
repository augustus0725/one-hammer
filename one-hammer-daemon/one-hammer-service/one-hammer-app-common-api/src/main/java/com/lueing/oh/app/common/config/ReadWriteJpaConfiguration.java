package com.lueing.oh.app.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {com.lueing.oh.app.common.config.RepositoryConfig.ReadWrite.REPOSITORY_PACKAGE},
        entityManagerFactoryRef = "rwEntityManagerFactory",
        transactionManagerRef = "rwTransactionManager",
        includeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, classes = {com.lueing.oh.app.common.config.RepositoryConfig.ReadWrite.class})}
)
@EnableJpaAuditing
public class ReadWriteJpaConfiguration {
    private HibernateProperties hibernateProperties;
    private JpaProperties jpaProperties;

    @Primary
    @Bean(name = "rwDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db0")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "rwEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("rwDataSource") DataSource dataSource) {

        return builder
                .dataSource(dataSource)
                .packages(
                        com.lueing.oh.app.common.config.RepositoryConfig.JPA_ENTITY_HOME
                )
                .persistenceUnit("rw-emf")
                .properties(hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()))
                .build();
    }

    @Primary
    @Bean(name = "rwTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("rwEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

    @Autowired
    public void setHibernateProperties(HibernateProperties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

    @Autowired
    public void setJpaProperties(JpaProperties jpaProperties) {
        this.jpaProperties = jpaProperties;
    }

}

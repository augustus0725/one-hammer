package com.lueing.oh.app.common.config;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author zhangcanbin@hongwangweb.com
 * @date 2022/5/30 17:06
 */
@Slf4j
public class RepositoryConfig {
    // TODO 需要修改成目标项目的包
    /**
     * Base package name of jpa.
     */
    public static final String JPA_PACKAGE_HOME = "com.lueing.oh";
    public static final String JPA_ENTITY_HOME = JPA_PACKAGE_HOME + ".jpa.entity";
    private static Pattern repositoryPattern;
    private static final String REPOSITORY_CLASS_NAME = "\\.[0-9a-zA-Z_]{1,100}";

    static {
        String driverClassName = null;
        String result;
        String dbType = "pg";
        // 从配置文件里读取jdbcUrl, 然后检测出是什么库, 覆盖 dbType
        try {
            // 先从 resource 里获取 application.yml
            result = findJdbcUrl(findResourcesLinesQuietly("application.yml"));

            if (!Strings.isNullOrEmpty(result)) {
                driverClassName = result;
            }

            // 然后从 spring.profiles.active 参数里获取 application-<spring.profiles.active>.yml
            String active = System.getenv("spring.profiles.active");
            String activePath = String.format("application-%s.yml", active);

            // 先从resource里获取application-%s.yml
            result = findJdbcUrl(findResourcesLinesQuietly(activePath));

            if (!Strings.isNullOrEmpty(result)) {
                driverClassName = result;
            }

            // 从当前路径获取, 约定: 当前jar所在的路径为运行代码的路径
            if (!Strings.isNullOrEmpty(active) && java.nio.file.Files.exists(Paths.get(activePath))) {
                //noinspection UnstableApiUsage
                result = findJdbcUrl(Files.readLines(new File(activePath), StandardCharsets.UTF_8));
                if (!Strings.isNullOrEmpty(result)) {
                    driverClassName = result;
                }
            }

            // 从给的driverClassName猜测数据库的类型
            if (!Strings.isNullOrEmpty(driverClassName)) {
                final Map<String, String> driverDbMapping = Maps.newHashMap();

                driverDbMapping.putAll(
                        // ImmutableMap最多只有5个键值对, 用这个只是写起来简单
                        ImmutableMap.of(
                                "org.postgresql.Driver", "pg",
                                "oracle.jdbc.OracleDriver", "oracle",
                                "oracle.jdbc.driver.OracleDriver", "oracle",
                                "com.mysql.jdbc.Driver", "mysql",
                                "com.microsoft.sqlserver.jdbc.SQLServerDriver", "mssql"
                        )
                );
                driverDbMapping.putAll(
                        ImmutableMap.of(
                                "dm.jdbc.driver.DmDriver", "dm",
                                "org.hibernate.dialect.H2Dialect", "h2"
                        )
                );
                result = driverDbMapping.get(driverClassName);
                if (!Strings.isNullOrEmpty(driverClassName)) {
                    dbType = result;
                }
            }
            // build matcher

            repositoryPattern = Pattern.compile(
                    ReadWrite.REPOSITORY_PACKAGE.replace(".", "\\.") + REPOSITORY_CLASS_NAME
                            + "|" +
                            ReadWrite.REPOSITORY_PACKAGE.replace(".", "\\.") + "\\." + dbType + REPOSITORY_CLASS_NAME
                            + "|" +
                            ReadOnly.REPOSITORY_PACKAGE.replace(".", "\\.") + REPOSITORY_CLASS_NAME
                            + "|" +
                            ReadOnly.REPOSITORY_PACKAGE.replace(".", "\\.") + "\\." + dbType + REPOSITORY_CLASS_NAME
            );
        } catch (Exception e) {
            log.error("Fail to read jdbcUrl from configuration.", e);
        }
    }

    private RepositoryConfig() {
        throw new IllegalStateException("Utility class ,not to be instantiated");
    }

    private static String findJdbcUrl(List<String> lines) {
        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.startsWith("driverClassName")) {
                String[] parts = trimmedLine.split(" ");

                if (parts.length >= 2) {
                    return parts[parts.length - 1];
                }
            }
        }
        return null;
    }

    private static List<String> findResourcesLinesQuietly(String resourceName) {
        try {
            //noinspection UnstableApiUsage
            return Resources.asCharSource(
                    Resources.getResource(resourceName),
                    StandardCharsets.UTF_8).readLines();
        } catch (Exception ignore) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("NullableProblems")
    public static final class ReadWrite implements TypeFilter {
        public static final String REPOSITORY_PACKAGE = JPA_PACKAGE_HOME + ".jpa.repository.rw";

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
            log.info("rw:" + metadataReader.getClassMetadata().getClassName() + "==>" +
                    repositoryPattern.matcher(metadataReader.getClassMetadata().getClassName()).matches());
            return repositoryPattern.matcher(metadataReader.getClassMetadata().getClassName()).matches();
        }
    }

    @SuppressWarnings("NullableProblems")
    public static final class ReadOnly implements TypeFilter {
        public static final String REPOSITORY_PACKAGE = JPA_PACKAGE_HOME + ".jpa.repository.r";

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
            log.info("r:" + metadataReader.getClassMetadata().getClassName() + "==>"
                    + repositoryPattern.matcher(metadataReader.getClassMetadata().getClassName()).matches());
            return repositoryPattern.matcher(metadataReader.getClassMetadata().getClassName()).matches();
        }
    }
}

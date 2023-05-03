package com.lueing.oh.plugins;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.lueing.oh.commons.os.Os;
import com.lueing.oh.commons.utils.CsvUtils;
import com.lueing.oh.connector.jdbc.Connector;
import com.lueing.oh.connector.jdbc.JdbcConnector;
import com.lueing.oh.connector.kafka.KafkaConnector;
import com.lueing.oh.dfs.Dfs;
import com.lueing.oh.dfs.sshpass.SshpassDfsImpl;
import com.zaxxer.hikari.util.DriverDataSource;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 约定:
 * 1. 环境变量包含:
 *     kafka.sourceId, topic, group
 *     jdbc.sourceId
 *     sshUser, sshPass, sshHost, baseDir
 * 2. 一个数据库的数据包含在一个topic里
 */

@Slf4j
public class Kafka2Jdbc {
    private static final Yaml yamlParser = new Yaml();

    private static Dfs createDfs() {
        String sshUser = System.getenv("sshUser");
        String sshPass = System.getenv("sshPass");
        String sshHost = System.getenv("sshHost");
        String baseDir = System.getenv("baseDir");
        if (Strings.isNullOrEmpty(sshUser)) {
            // check
        }
        if (Strings.isNullOrEmpty(sshPass)) {
            // check
        }
        if (Strings.isNullOrEmpty(sshHost)) {
            // check
        }
        if (Strings.isNullOrEmpty(baseDir)) {
            // check
        }
        Dfs dfs = new SshpassDfsImpl(sshUser, sshPass, sshHost, baseDir);
        return dfs;
    }

    private static DriverDataSource createDatasource(Dfs dfs) throws Exception {
        String sourceId = System.getenv("jdbc.sourceId");
        if (Strings.isNullOrEmpty(sourceId)) {
            // check
        }
        JdbcConnector jdbcConnector = new JdbcConnector();
        Path remote = Paths.get(sourceId);
        if (!Files.exists(Paths.get(remote.getFileName().toString()))) {
            dfs.copy(remote, Paths.get("."));
        }
        Connector connector = yamlParser.loadAs(Os.cat(Paths.get(
                remote.getFileName().toString(), "connector.yaml")), Connector.class);
        jdbcConnector.loadDriver(Paths.get(remote.getFileName().toString(),
                "libs").toString(), connector);

        return new DriverDataSource(connector.getJdbcUrl(), connector.getDriverClassName(),
                new Properties(), connector.getUsername(), connector.getPassword());
    }

    private static Consumer<String, String> createKafkaConsumer(Dfs dfs) throws Exception {
        String sourceId = System.getenv("kafka.sourceId");
        if (Strings.isNullOrEmpty(sourceId)) {
            // check
        }
        String topic = System.getenv("topic");
        if (Strings.isNullOrEmpty(topic)) {
            // check
        }
        String group = System.getenv("group");
        if (Strings.isNullOrEmpty(group)) {
            // check
        }
        Path remote = Paths.get(sourceId);
        if (!Files.exists(Paths.get(remote.getFileName().toString()))) {
            dfs.copy(remote, Paths.get("."));
        }
        com.lueing.oh.connector.kafka.Connector connector = yamlParser.loadAs(Os.cat(Paths.get(
                remote.getFileName().toString(), "connector.yaml")), com.lueing.oh.connector.kafka.Connector.class);
        return KafkaConnector.createConsumer(Paths.get(remote.getFileName().toString()).toString(), connector, topic, group);
    }

    public static void main(String[] args) throws Exception {
        final Dfs dfs = createDfs();
        final DriverDataSource driverDataSource = createDatasource(dfs);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(driverDataSource);
        final TransactionTemplate transactionTemplate = new TransactionTemplate(
                new DataSourceTransactionManager(driverDataSource)
        );
        final Consumer<String, String> consumer = createKafkaConsumer(dfs);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(8));
            final AtomicBoolean done = new AtomicBoolean(false);

            transactionTemplate.execute(status -> {
                try {
                    for (ConsumerRecord<String, String> record : records) {
                        Headers headers = record.headers();

                        // 约定, header 包含key: "state" => DONE, 程序正常退出, 消息处理完毕
                        if (Arrays.equals("DONE".getBytes(StandardCharsets.UTF_8),
                                headers.lastHeader("state").value())) {
                            done.set(true);
                            break;
                        }
                        // 一个消息体是一个csv文件
                        // 约定, header包含[table]字段
                        Header header = record.headers().lastHeader("table");
                        assert header != null;
                        sinkOneCsv(jdbcTemplate, new String(header.value(), StandardCharsets.UTF_8), record.value());
                    }
                    consumer.commitSync();
                } catch (Exception e) {
                    status.setRollbackOnly();
                }
                return null;
            });

            //
            if (done.get()) {
                break;
            }
        }
        consumer.close();
    }
    private static void sinkOneCsv(JdbcTemplate jdbcTemplate, String table, String csv) throws Exception {
        try (CsvReader reader = CsvUtils.createCsvReader().build(csv)) {
            List<CsvRow> csvContent = reader.stream().collect(Collectors.toList());
            if (csvContent.size() <= 2) {
                log.warn("Csv: emtpy or with bad format.");
                return;
            }
            // header
            List<String> header = csvContent.get(0).getFields();
            // read field type
            @SuppressWarnings("UnstableApiUsage")
            List<Integer> fieldTypeList = csvContent.get(1).getFields()
                    .stream().map(Ints::tryParse).collect(Collectors.toList());
            // then do insert
            String insertClause = String.format(
                    "insert into %s (%s) values (%s)",
                    table,
                    Joiner.on(',').join(header),
                    Joiner.on(',').join(Collections.nCopies(fieldTypeList.size(), '?')));

            jdbcTemplate.execute(insertClause, (PreparedStatementCallback<Object>) ps -> {
                for (int i = 2; i < csvContent.size(); i++) {
                    CsvRow csvRow = csvContent.get(i);
                    for (int j = 1; j <= fieldTypeList.size(); j++) {
                        // TODO 有隐患
                        if ("null".equals(csvRow.getField(j - 1))) {
                            ps.setNull(j, Types.NULL);
                            continue;
                        }
                        switch (fieldTypeList.get(j - 1)) {
                            case Types.BIT:
                            case Types.TINYINT:
                            case Types.SMALLINT:
                            case Types.INTEGER:
                                if (!Strings.isNullOrEmpty(csvRow.getField(j - 1))) {
                                    //noinspection UnstableApiUsage
                                    Integer intValue = Ints.tryParse(csvRow.getField(j - 1));
                                    if (null == intValue) {
                                        ps.setNull(j, Types.NULL);
                                    } else {
                                        ps.setInt(j, intValue);
                                    }
                                }
                                break;
                            case Types.BIGINT:
                                if (!Strings.isNullOrEmpty(csvRow.getField(j - 1))) {
                                    //noinspection UnstableApiUsage
                                    Long longValue = Longs.tryParse(csvRow.getField(j - 1));
                                    if (null == longValue) {
                                        ps.setNull(j, Types.NULL);
                                    } else {
                                        ps.setLong(j, longValue);
                                    }
                                }
                                break;
                            case Types.FLOAT:
                                if (!Strings.isNullOrEmpty(csvRow.getField(j - 1))) {
                                    //noinspection UnstableApiUsage
                                    Float floatValue = Floats.tryParse(csvRow.getField(j - 1));
                                    if (null == floatValue) {
                                        ps.setNull(j, Types.NULL);
                                    } else {
                                        ps.setFloat(j, floatValue);
                                    }
                                }
                                break;
                            case Types.REAL:
                            case Types.DOUBLE:
                            case Types.NUMERIC:
                            case Types.DECIMAL:
                                if (!Strings.isNullOrEmpty(csvRow.getField(j - 1))) {
                                    //noinspection UnstableApiUsage
                                    Double doubleValue = Doubles.tryParse(csvRow.getField(j - 1));
                                    if (null == doubleValue) {
                                        ps.setNull(j, Types.NULL);
                                    } else {
                                        ps.setDouble(j, doubleValue);
                                    }
                                }
                                break;
                            case Types.CHAR:
                            case Types.VARCHAR:
                            case Types.LONGVARCHAR:
                                if (!Strings.isNullOrEmpty(csvRow.getField(j - 1))) {
                                    ps.setString(j, csvRow.getField(j - 1));
                                }
                                break;
                            case Types.DATE:
                                //noinspection UnstableApiUsage
                                Long dateValue = Longs.tryParse(csvRow.getField(j - 1));
                                if (null == dateValue) {
                                    ps.setNull(j, Types.NULL);
                                } else {
                                    ps.setDate(j, new Date(dateValue));
                                }

                                if (!Strings.isNullOrEmpty(csvRow.getField(j - 1))) {
                                    ps.setDate(j, Date.valueOf(csvRow.getField(j - 1)));
                                }
                                break;
                            case Types.TIME:
                                //noinspection UnstableApiUsage
                                Long timeValue = Longs.tryParse(csvRow.getField(j - 1));
                                if (null == timeValue) {
                                    ps.setNull(j, Types.NULL);
                                } else {
                                    ps.setTime(j, new Time(timeValue));
                                }
                                break;
                            case Types.TIMESTAMP:
                                //noinspection UnstableApiUsage
                                Long longValue = Longs.tryParse(csvRow.getField(j - 1));
                                if (null == longValue) {
                                    ps.setNull(j, Types.NULL);
                                } else {
                                    ps.setTimestamp(j, new Timestamp(longValue));
                                }
                                break;
                            case Types.NULL:
                                ps.setNull(j, Types.NULL);
                                break;
                            case Types.BINARY:
                            case Types.VARBINARY:
                            case Types.LONGVARBINARY:
                            case Types.OTHER:
                            default:
                                log.info("Unsupport field: {}, try to add it as string.", csvRow.getField(j - 1));
                                ps.setString(j, csvRow.getField(j - 1));
                        }
                    }
                    ps.execute();
                    ps.clearParameters();
                }
                return null;
            });
        }

    }
}

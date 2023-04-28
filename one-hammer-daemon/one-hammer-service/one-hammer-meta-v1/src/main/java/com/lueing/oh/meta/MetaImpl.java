package com.lueing.oh.meta;

import com.google.common.collect.Maps;
import com.lueing.oh.commons.connectors.jdbc.Connector;
import com.lueing.oh.commons.connectors.jdbc.JdbcProxy;
import com.lueing.oh.commons.os.Os;
import com.lueing.oh.dfs.Dfs;
import com.lueing.oh.meta.api.HammerMetaException;
import com.lueing.oh.meta.api.Meta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
@Slf4j
public class MetaImpl implements Meta {
    private static final Yaml yamlParser = new Yaml();
    private final Dfs dfs;
    private final JdbcProxy jdbcProxy;
    private final Map<String, Map<String, Integer>> cache = Maps.newHashMap();
    @Override
    public synchronized Map<String, Integer> meta(String identify, String table) throws HammerMetaException {
        final String metaKey = identify + "_" + table;
        if (cache.containsKey(metaKey)) {
            return cache.get(metaKey);
        }

        Path remote = Paths.get(identify);
        if (!Files.exists(Paths.get("sources", remote.getFileName().toString()))) {
            try {
                dfs.copy(remote, Paths.get("sources"));
            } catch (IOException e) {
                throw new HammerMetaException(e);
            }
        }
        // 标准格式
        // 目录: sources/connectors/jdbc/dev/pg-192.168.0.185:5432-pgdb-gpadmin
        // ./connector.yaml
        // ./libs
        Connection connection = null;
        ResultSet rs = null;
        Map<String, Integer> result = Maps.newHashMap();
        try {
            Connector connector = yamlParser.loadAs(Os.cat(Paths.get("sources",
                    remote.getFileName().toString(), "connector.yaml")), Connector.class);
            connection = jdbcProxy.getConnection(Paths.get("sources", remote.getFileName().toString(),
                    "libs").toString(), connector);
            DatabaseMetaData databaseMetaData = connection.getMetaData();

            rs = databaseMetaData.getColumns(null, null, table, null);
            while (rs.next()) {
                result.put(rs.getString(4), Integer.valueOf(rs.getString(5)));
            }
        } catch (Exception e) {
            throw new HammerMetaException(e);
        } finally {
            try {
                if (null != rs) {
                    rs.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (SQLException ignore) {
            }
        }
        // put it to cache first.
        cache.put(metaKey, result);
        if (cache.size() > 1_024_000) {
            // 1_024_000 * 1KB == 1_000MB ~ 1GB
            log.warn("Meta cache is huge.");
        }
        return result;
    }
}

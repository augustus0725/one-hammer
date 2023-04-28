package com.lueing.oh.meta.jdbc;

import com.lueing.oh.commons.connectors.jdbc.Connector;
import com.lueing.oh.commons.connectors.jdbc.JdbcProxy;
import org.junit.Ignore;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;

@Ignore
public class JdbcProxyTest {
    @Test
    public void testConnection() throws Exception {
        JdbcProxy jdbcProxy = new JdbcProxy();

        Connection connection = jdbcProxy.getConnection("sources/libs", Connector.builder()
                .jdbcUrl("jdbc:postgresql://192.168.0.*:5432/**_db")
                .driverClassName("org.postgresql.Driver")
                .username("pgadmin")
                .password("*****")
                .build()
        );

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        // 调试的时候可以看到元数据查询的SQL
        ResultSet rs = databaseMetaData.getTables(null, null, "%", new String[]{"TABLE"});

        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3));
        }
        rs.close();
        // column
        rs = databaseMetaData.getColumns(null, null, "t_app", null);
        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getString(4) + "\t" + rs.getString(5));
        }
        connection.close();
    }

    @Test
    public void testYaml() {
        String yaml = "jdbcUrl: \"jdbc:postgresql://192.168.0.xx:xx/xxxdb\"\n" +
                "username: pgadmin\n" +
                "password: xxxxxx\n" +
                "driverClassName: org.postgresql.Driver";

        Connector connector = new Yaml().loadAs(yaml, Connector.class);
        assertEquals("jdbc:postgresql://192.168.0.xx:xx/xxxdb", connector.getJdbcUrl());
    }

}

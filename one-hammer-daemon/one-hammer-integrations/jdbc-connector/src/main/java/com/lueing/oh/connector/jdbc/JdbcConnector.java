package com.lueing.oh.connector.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class JdbcConnector {
    private final Map<String, Driver> nameDriver = new HashMap<>();

    public Connection getConnection(String libs, Connector connector) throws Exception {
        loadDriver(libs, connector);
        return DriverManager.getConnection(connector.getJdbcUrl(), connector.getUsername(), connector.getPassword());
    }

    public void loadDriver(String libs, Connector connector) throws Exception {
        Driver d = nameDriver.get(connector.getDriverClassName());
        if (null == d) {
            d = (Driver) Class.forName(connector.getDriverClassName(), true,
                    new DynamicJdbcClassLoader(libs)).newInstance();
            // Driver d 是由this.classLoader 加载的, 但是DriverManager只认系统classLoader加载的驱动
            // 所以用ShimDriver包了一层, ShimDriver是由系统classLoader加载的
            DriverManager.registerDriver(new ShimDriver(d));
            nameDriver.put(connector.getDriverClassName(), d);
        }
    }
}

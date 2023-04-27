package com.lueing.oh.meta.api;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Connector {
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
}

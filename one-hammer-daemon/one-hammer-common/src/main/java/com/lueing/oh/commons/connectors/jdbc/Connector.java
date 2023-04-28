package com.lueing.oh.commons.connectors.jdbc;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode
public class Connector {
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
}

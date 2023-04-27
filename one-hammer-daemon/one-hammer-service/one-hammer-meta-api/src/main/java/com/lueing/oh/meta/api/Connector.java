package com.lueing.oh.meta.api;

import lombok.*;
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

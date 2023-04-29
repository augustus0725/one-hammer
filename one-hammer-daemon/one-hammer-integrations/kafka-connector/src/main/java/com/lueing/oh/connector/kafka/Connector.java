package com.lueing.oh.connector.kafka;

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
    private String bootstrapServers;
    private boolean enableKerberos;
}

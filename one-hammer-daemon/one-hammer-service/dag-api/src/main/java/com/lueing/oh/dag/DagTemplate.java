package com.lueing.oh.dag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DagTemplate {
    private String namespace;
    private String name;
    private String path;
    private String description;
}

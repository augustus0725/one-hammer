package com.lueing.oh.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class OneHammerDag {
    private String template;
    private String dagId;
    private String name;
    private String description;
    private String schedule;
}

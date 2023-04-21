package com.lueing.oh.dag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@ToString
@NoArgsConstructor
public class DagTask {
    private String name;
    private String taskType;
    private String state;
    private String submitTime;
    private String startTime;
    private String endTime;
    private boolean taskSuccess;
}

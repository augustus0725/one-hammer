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
public class DagSnapshot {
    private String snapshotId;
    private String dagId;
    private String state;
    private String startTime;
    private String endTime;
    private String name;
    private String duration;
}

package com.lueing.oh.dag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@ToString
@NoArgsConstructor
public class DagTasksSnapshot {
    private String dagId;
    private String state;
    private List<DagTask> taskList;
}

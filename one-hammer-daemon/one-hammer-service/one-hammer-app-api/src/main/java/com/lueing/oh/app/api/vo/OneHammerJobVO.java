package com.lueing.oh.app.api.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class OneHammerJobVO {
    private String apiVersion;
    private String kind;
    private String expectedStatus;
    private String name;
    private String description;
    private String namespace;
    private String catalog;
    private String id;
    private Timestamp createdDate;
    private Long lastModifiedDate;
    private com.lueing.oh.pojo.OneHammerJob job;
}

package com.lueing.oh.jpa.entity;

import com.lueing.oh.jpa.entity.base.BaseEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "t_ds139_template")
@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
@ToString
@DynamicInsert
@DynamicUpdate
public class Ds139Template extends BaseEntity {
    private String namespace;
    private String name;
    private String description;
    private String path;
}

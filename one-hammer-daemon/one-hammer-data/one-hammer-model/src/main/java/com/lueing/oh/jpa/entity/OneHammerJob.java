package com.lueing.oh.jpa.entity;

import com.lueing.oh.jpa.entity.base.BaseEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "t_one_hammer_job", indexes = {@Index(columnList = "catalog")})
@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
@ToString
@DynamicInsert
@DynamicUpdate
public class OneHammerJob extends BaseEntity {
    @Comment("完整的job描述, 用yaml存储, 常用的信息抽取出来, 作为维度查询, 为了解决yaml大小不可控的问题, " +
            "这里的yaml是路径, 文件可以放在nginx/ftp/minio")
    @Column(length = 8192)
    private String yaml;

    /* 查询区域 */
    private String apiVersion;
    private String kind;
    @Comment("job期待的状态, STARTED/STOPPED")
    private String expectedStatus;
    private String name;
    private String description;
    private String namespace;
    private String catalog;
}

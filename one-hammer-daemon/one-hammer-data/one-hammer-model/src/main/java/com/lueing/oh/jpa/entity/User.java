package com.lueing.oh.jpa.entity;

import com.lueing.oh.jpa.entity.base.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
@Entity
@Table(name = "t_user")
@Getter
@Setter
@RequiredArgsConstructor
@SuperBuilder
@ToString
@DynamicInsert
@DynamicUpdate
public class User extends BaseEntity {
    @Column
    private String name;
    @Column
    private int age;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

package com.practice.growth.domain.entity;

import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 역할
 */

@Getter
@Setter
@Entity
@DynamicInsert
public class Role implements Serializable {
    @Id
    @Column(length = 20)
    private String roleName;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    @Column
    private String description;

    @Column
    private int sortOrder;

    /**
     * 삭제 가능 여부
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private YNType defaultYn;

    /** todo : 추후 추가해야됨
    @ManyToMany(mappedBy = "roles")
    private Set<Menu> menus = new HashSet<>();
    **/
}

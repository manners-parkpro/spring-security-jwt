package com.practice.growth.domain.entity;

import com.practice.growth.domain.types.YNType;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 역할
 */

@Getter
@Setter
@Entity
@DynamicInsert
public class AccountRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @NotNull
    @Column(length = 20)
    private String roleName;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    @Column
    private int sortOrder;

    @NotNull
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    public void beforePersist() {
        LocalDateTime dateTime = LocalDateTime.now();
        this.createdAt = dateTime;
        this.updatedAt = dateTime;
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

package com.practice.growth.domain.entity;

import com.practice.growth.domain.types.ProviderType;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicInsert
@NoArgsConstructor
public class Account extends BaseEntity {

    @Id
    @SequenceGenerator(name = "ACCOUNT_SEQ", sequenceName = "ACCOUNT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCOUNT_SEQ")
    private Long id;
    @Column(length = 100)
    private String username;
    @Column(length = 100)
    private String email;
    @Column(length = 50)
    private String tel;
    private String password;
    private String role; // 나중에는 연관관계 필요
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ProviderType provider;
    @Column(length = 50)
    private String providerId;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'Y'")
    private YNType activeYn;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private YNType dormantYn;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private YNType deleteYn;
    private LocalDateTime lastLoginAt;
}

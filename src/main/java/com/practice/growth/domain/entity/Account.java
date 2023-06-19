package com.practice.growth.domain.entity;

import com.practice.growth.domain.types.ProviderType;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @Column(length = 50)
    private String roles; // 나중에는 연관관계 필요
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ProviderType provider;
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

    // ENUM으로 안하고 ,로 해서 구분해서 ROLE을 입력 -> 그걸 파싱!!
    public List<String> getRoleList() {
        if (StringUtils.isNotBlank(this.roles))
            return Arrays.asList(this.roles.split(","));

        return new ArrayList<>();
    }
}

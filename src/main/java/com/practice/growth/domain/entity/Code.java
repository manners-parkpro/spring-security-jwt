package com.practice.growth.domain.entity;

import com.practice.growth.domain.types.YNType;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 코드관리
 *
 * @author prographer
 * @date: 10/25/19
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
@Table(indexes = {
        @Index(columnList = "code", unique = true)
})
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CODE_SEQ")
    @SequenceGenerator(name = "CODE_SEQ", sequenceName = "CODE_SEQ", allocationSize = 1)
    private Long id;

    /**
     * 코드
     */
    @Column(length = 100)
    private String code;

    /**
     * 코드그룹(부모코드)
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "GROUP_CODE")
    private Code group;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder")
    @JoinColumn(name = "GROUP_CODE")
    private List<Code> children = new ArrayList<>();

    /**
     * 코드 설명
     */
    @Column
    private String description;

    /**
     * 코드 값
     */
    @Column(length = 1000)
    private String value;

    /**
     * 사용 여부
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'Y'")
    private YNType activeYn;

    /**
     * 최종 수정자
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "LAST_MODIFIER_ID")
    private Account lastModifier;

    /**
     * 순서
     */
    @Column
    private int sortOrder;

    /**
     * 하위 코드 추가
     *
     * @param code 하위 코드
     */
    public void addChildren(Code code) {
        this.children.add(code);
        code.setGroup(this);
    }

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

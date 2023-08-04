package com.practice.growth.domain.entity;

import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Setter
@Entity
@DynamicInsert
@NoArgsConstructor
public class StayReply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String reply;

    @ManyToOne
    @JoinColumn(name = "STAY_ID")
    private Stay stay;

    private String nickname;
    private String password;

    /**
     * 등록자 ID
     */
    @ManyToOne
    @JoinColumn(name = "WRITER_ID")
    private Account writer;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private YNType delYn;
}

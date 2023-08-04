package com.practice.growth.domain.entity;

import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicInsert
@NoArgsConstructor
public class Stay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column
    private String tel;
    @Column
    private String location;
    @Column
    private String postCode;
    @OneToMany(mappedBy = "stay", cascade = CascadeType.ALL)
    private List<StayReply> replies = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "WRITER_ID")
    private Account writer;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'Y'")
    private YNType activeYn;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private YNType delYn;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "STAY_ATTACHE",
            joinColumns = @JoinColumn(name = "STAY_ID"),
            inverseJoinColumns = @JoinColumn(name = "ATTACHE_ID")
    )
    private List<Attachment> attachments = new ArrayList<>();

    // 시설 가능한 Service
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "STAY_CODE",
            joinColumns = @JoinColumn(name = "STAY_ID"),
            inverseJoinColumns = @JoinColumn(name = "CODE_ID")
    )
    private List<Code> codes = new ArrayList<>();
}

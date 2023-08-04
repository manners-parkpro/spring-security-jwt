package com.practice.growth.domain.entity;

import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.joda.time.LocalDate;

import javax.persistence.*;

@Getter
@Setter
@Entity
@DynamicInsert
@NoArgsConstructor
public class Reserve extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RESERVE_SEQ")
    @SequenceGenerator(name = "RESERVE_SEQ", sequenceName = "RESERVE_SEQ", allocationSize = 1)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;
    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;
    @Column
    @ColumnDefault("0")
    private int personNumber;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private YNType babyYn;
    @ManyToOne
    @JoinColumn(name = "RESERVER_ID")
    private Account reserver;
}

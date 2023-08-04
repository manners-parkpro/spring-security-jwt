package com.practice.growth.domain.entity;

import com.practice.growth.domain.types.ReserveStatusType;
import com.practice.growth.domain.types.RoomType;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicInsert
@NoArgsConstructor
@Table(indexes = {
        @Index(columnList = "name")
})
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROOM_SEQ")
    @SequenceGenerator(name = "ROOM_SEQ", sequenceName = "ROOM_SEQ", allocationSize = 1)
    private Long id;
    @Column
    private String name;
    // @Lob (Large Objects in auto-commit mode Error) 발생
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column
    @ColumnDefault("0")
    private int minimum;
    @Column
    @ColumnDefault("0")
    private int maximum;
    @Column
    @ColumnDefault("0")
    private int price;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RoomType roomType = RoomType.STANDARD;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ReserveStatusType reserveStatusType = ReserveStatusType.ABLE;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private YNType doubleBedYn;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private YNType petYn;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'Y'")
    private YNType activeYn;
    @Enumerated(EnumType.STRING)
    @Column(length = 1, columnDefinition = "char(1) default 'N'")
    private YNType delYn;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ROOM_ATTACHE",
            joinColumns = @JoinColumn(name = "ROOM_ID"),
            inverseJoinColumns = @JoinColumn(name = "ATTACHE_ID")
    )
    private List<Attachment> attachments = new ArrayList<>();
}

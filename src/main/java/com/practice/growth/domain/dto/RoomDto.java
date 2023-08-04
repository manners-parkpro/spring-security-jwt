package com.practice.growth.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.practice.growth.domain.entity.Room;
import com.practice.growth.domain.types.ReserveStatusType;
import com.practice.growth.domain.types.RoomType;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
public class RoomDto {

    private Long id;
    private String name;
    private String description;
    private int minimum;
    private int maximum;
    private int price;
    private RoomType roomType;
    private ReserveStatusType reserveStatusType;
    private YNType doubleBedYn;
    private YNType petYn;
    private YNType activeYn;
    private YNType delYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AttachmentDto> attachments = new ArrayList<>();

    public RoomDto(Room r) { this(r, false); };

    public RoomDto(Room r, boolean isAttachment) {
        this.id = r.getId();
        this.name = r.getName();
        this.description = r.getDescription();
        this.minimum = r.getMinimum();
        this.maximum = r.getMaximum();
        this.price = r.getPrice();
        this.roomType = r.getRoomType();
        this.reserveStatusType = r.getReserveStatusType();
        this.doubleBedYn = r.getDoubleBedYn();
        this.petYn = r.getPetYn();
        this.activeYn = r.getActiveYn();
        this.delYn = r.getDelYn();
        this.createdAt = r.getCreatedAt();
        this.updatedAt = r.getUpdatedAt();

        if (isAttachment)
            this.attachments = r.getAttachments().stream().map(AttachmentDto::new).collect(Collectors.toList());
    }
}

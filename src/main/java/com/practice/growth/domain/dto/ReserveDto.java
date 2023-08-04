package com.practice.growth.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.practice.growth.domain.entity.Reserve;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_EMPTY)
public class ReserveDto {

    private Long id;
    private RoomDto room;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class) // json 객체를 java 객체로 변환하는 작업
    @JsonSerialize(using = LocalDateSerializer.class) // java 객체를 json 객체로 변환하는 작업
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class) // json 객체를 java 객체로 변환하는 작업
    @JsonSerialize(using = LocalDateSerializer.class) // java 객체를 json 객체로 변환하는 작업
    private LocalDate endDate;

    private int personNumber;
    private YNType babyYn;
    private AccountDto reserver;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReserveDto(Reserve r) {
        this.id = r.getId();
        this.room = new RoomDto(r.getRoom());
        this.startDate = r.getStartDate();
        this.endDate = r.getEndDate();
        this.personNumber = r.getPersonNumber();
        this.babyYn = r.getBabyYn();
        this.reserver = new AccountDto(r.getReserver());
        this.createdAt = r.getCreatedAt();
        this.updatedAt = r.getUpdatedAt();
    }
}

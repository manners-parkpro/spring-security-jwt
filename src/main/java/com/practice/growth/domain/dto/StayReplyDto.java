package com.practice.growth.domain.dto;

import com.practice.growth.domain.entity.StayReply;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StayReplyDto {

    private Long id;
    private String reply;
    private String nickname;
    private String password;
    private AccountDto writer;
    private YNType delYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StayReplyDto(StayReply sr) {
        this.id = sr.getId();
        this.reply = sr.getReply();
        this.nickname = sr.getNickname();
        this.password = sr.getPassword();
        this.writer = new AccountDto(sr.getWriter());
        this.delYn = sr.getDelYn();
        this.createdAt = sr.getCreatedAt();
        this.updatedAt = sr.getUpdatedAt();
    }
}

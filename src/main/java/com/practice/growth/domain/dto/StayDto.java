package com.practice.growth.domain.dto;

import com.practice.growth.domain.entity.Stay;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class StayDto {
    private Long id;
    private String name;
    private String description;
    private String tel;
    private String location;
    private String postCode;
    private AccountDto writer;
    private YNType activeYn;
    private YNType delYn;
    private List<AttachmentDto> attachments = new ArrayList<>();
    private List<CodeDto> codes = new ArrayList<>();
    private List<StayReplyDto> stayReplies = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StayDto(Stay s) { this(s, false, false); }

    public StayDto(Stay s, boolean isAttachment, boolean isDetail) {
        this.id = s.getId();
        this.name = s.getName();
        this.description = s.getDescription();
        this.tel = s.getTel();
        this.location = s.getLocation();
        this.postCode = s.getPostCode();
        this.writer = new AccountDto(s.getWriter());
        this.activeYn = s.getActiveYn();
        this.delYn = s.getDelYn();
        this.codes = s.getStayService().stream().map(CodeDto::new).collect(Collectors.toList());
        this.createdAt = s.getCreatedAt();
        this.updatedAt = s.getUpdatedAt();

        if (isAttachment)
            this.attachments = s.getAttachments().stream().map(AttachmentDto::new).collect(Collectors.toList());

        if (isDetail)
            this.stayReplies = s.getReplies().stream().map(StayReplyDto::new).collect(Collectors.toList());
    }
}

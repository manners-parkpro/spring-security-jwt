package com.practice.growth.domain.dto;

import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.Attachment;
import com.practice.growth.domain.entity.Notice;
import com.practice.growth.domain.types.YNType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NoticeDto {
    private Long id;
    private Long[] ids;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AccountDto writer;
    private YNType fixedTopYn;
    private YNType activeYn;
    private YNType delYn;
    private List<AttachmentDto> attachments = new ArrayList<>();

    public NoticeDto(Notice n) { this(n, false); }

    public NoticeDto(Notice n, boolean setAttachment) {
        this.id = n.getId();
        this.title = n.getTitle();
        this.content = n.getContent();
        this.updatedAt = n.getUpdatedAt();
        this.createdAt = n.getCreatedAt();
        this.writer = new AccountDto(n.getWriter());
        this.fixedTopYn = n.getFixedTopYn();
        this.activeYn = n.getActiveYn();
        this.delYn = n.getDelYn();

        if (setAttachment) {
            for (Attachment a : n.getAttachments()) {
                this.attachments.add(new AttachmentDto(a));
            }
        }
    }
}

package com.practice.growth.domain.dto;

import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.Attachment;
import com.practice.growth.domain.types.FileType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentDto {

    private Long id;

    private FileType fileType;

    private String orgFilename;

    private String savedFilename;

    private String fullPath;

    private String thumbnailPath;

    private int fileSize;

    private int position;
    /**
     * 실제 파일
     */
    private File rawFile;

    public AttachmentDto(Attachment a) {
        this.id = a.getId();
        this.fileType = a.getFileType();
        this.orgFilename = a.getOrgFilename();
        this.savedFilename = a.getSavedFilename();
        this.fullPath = a.getFullPath();
        this.thumbnailPath = a.getThumbnailPath();
        this.position = a.getPosition();
    }

    public Attachment convertAttachDtoToModel(AttachmentDto a, Account register) {
        Attachment attachment = new Attachment();
        attachment.setFileType(a.getFileType());
        attachment.setOrgFilename(a.getOrgFilename());
        attachment.setSavedFilename(a.getSavedFilename());
        attachment.setFullPath(a.getFullPath());
        attachment.setThumbnailPath(a.getThumbnailPath());
        attachment.setRegister(register);
        attachment.setPosition(a.getPosition());
        return attachment;
    }
}

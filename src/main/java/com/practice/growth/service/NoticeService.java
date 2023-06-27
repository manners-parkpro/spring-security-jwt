package com.practice.growth.service;

import com.practice.growth.domain.dto.AttachmentDto;
import com.practice.growth.domain.dto.NoticeDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.Attachment;
import com.practice.growth.domain.entity.Notice;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.exception.RequiredParamNonException;
import com.practice.growth.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository repository;

    public NoticeDto getNotice(Long id) throws NotFoundException {
        if (id == null)
            throw new NotFoundException("Board not found", NotFoundException.BOARD_NOT_FOUND);

        Optional<Notice> optNotice = repository.findById(id);
        if (optNotice.isEmpty())
            throw new NotFoundException("Board not found", NotFoundException.BOARD_NOT_FOUND);

        return new NoticeDto(optNotice.get(), true);
    }

    public Long createOrModifyNotice(NoticeDto dto, Account writer) throws NotFoundException {
        Notice notice;

        if (dto.getId() != null) {
            Optional<Notice> optBoard = repository.findById(dto.getId());
            if (optBoard.isEmpty())
                throw new NotFoundException("Board not found", NotFoundException.BOARD_NOT_FOUND);

            notice = optBoard.get();
        } else
            notice = new Notice();

        assert dto.getTitle() != null;

        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setActiveYn(dto.getActiveYn());
        notice.setFixedTopYn(dto.getFixedTopYn());
        notice.setWriter(writer);

        if (!CollectionUtils.isEmpty(dto.getAttachments())) {
            notice.getAttachments().clear();

            List<Attachment> attachmentList = new ArrayList<>();

            for (AttachmentDto attachmentDto : dto.getAttachments()) {
                Attachment attachment = new Attachment();

                attachment.setFileType(attachmentDto.getFileType());
                attachment.setOrgFilename(attachmentDto.getOrgFilename());
                attachment.setSavedFilename(attachmentDto.getSavedFilename());
                attachment.setFullPath(attachmentDto.getFullPath());
                attachment.setThumbnailPath(attachmentDto.getThumbnailPath());
                attachment.setPosition(attachmentDto.getPosition());
                attachment.setRegister(writer);

                attachmentList.add(attachment);
            }

            notice.getAttachments().addAll(attachmentList);
        }

        repository.save(notice);

        return notice.getId();
    }
}

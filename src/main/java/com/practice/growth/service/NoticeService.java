package com.practice.growth.service;

import com.practice.growth.domain.dto.AttachmentDto;
import com.practice.growth.domain.dto.BoardSearchDto;
import com.practice.growth.domain.dto.NoticeDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.Attachment;
import com.practice.growth.domain.entity.Notice;
import com.practice.growth.domain.types.YNType;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository repository;
    @PersistenceContext
    private EntityManager em;

    public Page<NoticeDto> findNoticeBySearch(BoardSearchDto search, boolean isSearch) {
        int page = search.getPage();
        int size = search.getSize();

        TypedQuery<Notice> tq = searchNotices(search, isSearch);
        long total = searchCount(search);
        tq.setFirstResult(page * size).setMaxResults(size);
        List<Notice> results = tq.getResultList();

        return new PageImpl<>(getList(results), PageRequest.of(page, size), total);
    }

    private List<NoticeDto> getList(List<Notice> notices) {
        return notices.stream().map(NoticeDto::new).collect(Collectors.toList());
    }

    private long searchCount(BoardSearchDto search) {
        CriteriaQuery<Long> countQuery = makeNoticeQuery(search, true);
        return em.createQuery(countQuery).getSingleResult();
    }

    private TypedQuery<Notice> searchNotices(BoardSearchDto search, boolean isSearch) {
        return em.createQuery(makeNoticeQuery(search, false));
    }

    private CriteriaQuery makeNoticeQuery(BoardSearchDto search, boolean isTotalCount) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery criteriaQuery;

        if (isTotalCount) criteriaQuery = builder.createQuery(Long.class);
        else criteriaQuery = builder.createQuery(Notice.class);

        Root<Notice> root = criteriaQuery.from(Notice.class);

        if (isTotalCount)
            criteriaQuery.select(builder.count(root));

        List<Predicate> likeOr = new ArrayList<>();
        List<Predicate> predicates = new ArrayList<>();

        // 기본 조건
        predicates.add(builder.equal(root.get("delYn"), YNType.N));

        if (!"ALL".equals(search.getFixedTopYn()))
            predicates.add(builder.equal(root.get("fixedTopYn"), YNType.valueOf(search.getFixedTopYn())));

        if (!"ALL".equals(search.getActiveYn()))
            predicates.add(builder.equal(root.get("activeYn"), YNType.valueOf(search.getActiveYn())));

        if (StringUtils.hasText(search.getSearchTitle()))
            likeOr.add(builder.like(builder.upper(root.get("title")), "%" + search.getSearchTitle().toUpperCase() + "%"));

        if (likeOr.size() > 0 && predicates.size() > 0)
            criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])), builder.or((likeOr.toArray(new Predicate[likeOr.size()]))));
        else if (likeOr.size() > 0)
            criteriaQuery.where(builder.or((likeOr.toArray(new Predicate[likeOr.size()]))));
        else if (predicates.size() > 0)
            criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));

        // order by
        //criteriaQuery.orderBy(builder.desc(root.get("createdAt")));

        return criteriaQuery;
    }

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
                attachment.setFileSize(attachmentDto.getFileSize());
                attachment.setPosition(attachmentDto.getPosition());
                attachment.setRegister(writer);

                attachmentList.add(attachment);
            }

            notice.getAttachments().addAll(attachmentList);
        }

        repository.save(notice);

        return notice.getId();
    }

    public void remove(Long[] ids) throws NotFoundException {
        if (ids.length == 0)
            throw new NotFoundException("Board not found", NotFoundException.BOARD_NOT_FOUND);

        List<Notice> notices = repository.findAllById(Arrays.asList(ids));
        if (CollectionUtils.isEmpty(notices))
            throw new NotFoundException("Board not found", NotFoundException.BOARD_NOT_FOUND);

        notices.stream().forEach(n -> n.setDelYn(YNType.Y));

        repository.saveAll(notices);
    }
}

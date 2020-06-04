package com.virnect.content.dao;

import com.virnect.content.domain.Content;
import com.virnect.content.domain.YesOrNo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-15
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Domain Repository Class
 */
public interface ContentRepository extends JpaRepository<Content, Long>, ContentCustomRepository {
    @Transactional(readOnly = true)
    Optional<Content> findByUuid(String contentUUID);

    Optional<Content> findByTargetList(String contentUUID);

    long countByConverted(YesOrNo yesOrNo);
    long countByShared(YesOrNo yesOrNo);
    long countByDeleted(YesOrNo yesOrNo);

    @Transactional
    Long deleteByUuid(String contentUUID);

    Boolean existsByUuid(String uuid);

    @Transactional(readOnly = true)
    Page<Content> findByNameIsContainingOrUserUUIDIsIn(String contentName, List<String> userUUIDList, Pageable pageable);

    Page<Content> findByWorkspaceUUIDAndNameIsContainingOrUserUUIDIsIn(String workspaceUUID, String contentName, List<String> userUUIDList, Pageable pageable);

    Page<Content> findByWorkspaceUUID(String workspaceUUID, Pageable pageable);

//    @Transactional(readOnly = true)
//    List<Content> findByStatus(ContentStatus contentStatus);

//    Page<Content> findByStatus(ContentStatus contentStatus, Pageable pageable);

//    @Transactional(readOnly = true)
//    long countByStatus(ContentStatus status);
}

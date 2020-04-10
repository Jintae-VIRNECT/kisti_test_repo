package com.virnect.content.dao;

import com.virnect.content.domain.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-15
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Domain Repository Class
 */
public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByUuid(String contentUUID);

    Long deleteByUuid(String contentUUID);

    Page<Content> findByNameIsContainingOrUserUUIDIsIn(String contentName, List<String> userUUIDList, Pageable pageable);

    Page<Content> findByWorkspaceUUIDAndNameIsContainingOrUserUUIDIsIn(String workspaceUUID, String contentName, List<String> userUUIDList, Pageable pageable);

    Page<Content> findByWorkspaceUUID(String workspaceUUID, Pageable pageable);
}

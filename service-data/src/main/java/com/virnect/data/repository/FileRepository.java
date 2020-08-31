package com.virnect.data.repository;

import com.virnect.data.dao.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByWorkspaceIdAndSessionIdAndName(final String workspaceId, final String sessionId, final String name);

    Optional<File> findByWorkspaceIdAndSessionIdAndObjectName(final String workspaceId, final String sessionId, final String objectName);

    List<File> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    Page<File> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId, Pageable pageable);

    Page<File> findByWorkspaceIdAndSessionIdAndDeletedIsTrue(final String workspaceId, final String sessionId, Pageable pageable);
}

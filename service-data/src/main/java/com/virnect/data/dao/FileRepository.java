package com.virnect.data.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.virnect.data.domain.file.File;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByWorkspaceIdAndSessionIdAndName(final String workspaceId, final String sessionId, final String name);

    Optional<File> findByWorkspaceIdAndSessionIdAndObjectName(final String workspaceId, final String sessionId, final String objectName);

    List<File> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    Page<File> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId, Pageable pageable);

    Page<File> findByWorkspaceIdAndSessionIdAndDeletedIsTrue(final String workspaceId, final String sessionId, Pageable pageable);

    @Modifying
    @Query("delete from File f where f.workspaceId = ?1 and f.sessionId = ?2")
    void deleteAllByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);
}

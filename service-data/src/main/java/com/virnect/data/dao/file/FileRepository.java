package com.virnect.data.dao.file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileType;

@Repository
public interface FileRepository extends JpaRepository<File, Long>, CustomFileRepository{
    Optional<File> findByWorkspaceIdAndSessionIdAndName(final String workspaceId, final String sessionId, final String name);

    Optional<File> findByWorkspaceIdAndSessionIdAndObjectName(final String workspaceId, final String sessionId, final String objectName);

    Optional<File> findByWorkspaceIdAndSessionIdAndObjectNameAndFileType(final String workspaceId, final String sessionId, final String objectName, FileType fileType);

    List<File> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    Page<File> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId, Pageable pageable);

    Page<File> findByWorkspaceIdAndSessionIdAndDeletedIsTrueAndFileType(final String workspaceId, final String sessionId, Pageable pageable, FileType fileType);

    @Modifying
    @Query("delete from File f where f.workspaceId = ?1 and f.sessionId = ?2")
    void deleteAllByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    List<File> findByWorkspaceIdAndSessionIdAndDeletedIsTrue(String workspaceId, String sessionId);

    List<File> findByWorkspaceIdAndSessionIdAndDeletedIsFalse(String workspaceId, String sessionId);

    List<File> findByWorkspaceIdAndDeletedIsTrue(String workspaceId);

    List<File> findByWorkspaceIdAndDeletedIsFalse(String workspaceId);

    List<File> findByWorkspaceIdAndSessionIdAndFileType(final String workspaceId, final String sessionId, FileType fileType);

}

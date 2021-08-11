package com.virnect.data.dao.file;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileType;

@Repository
public interface FileRepository extends JpaRepository<File, Long>, CustomFileRepository{

    Optional<File> findByWorkspaceIdAndSessionIdAndObjectName(final String workspaceId, final String sessionId, final String objectName);

    List<File> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    @Modifying
    @Query("delete from File f where f.workspaceId = ?1 and f.sessionId = ?2")
    void deleteAllByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId);

    List<File> findByWorkspaceIdAndSessionIdAndFileType(final String workspaceId, final String sessionId, FileType fileType);

}

package com.virnect.data.dao.file;

import com.virnect.data.domain.file.RecordFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordFileRepository extends JpaRepository<RecordFile, Long>, CustomRecordFileRepository{
    Optional<RecordFile> findByWorkspaceIdAndSessionIdAndObjectName(final String workspaceId, final String sessionId, final String objectName);

    Page<RecordFile> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId, Pageable pageable);

    Page<RecordFile> findByWorkspaceIdAndSessionIdAndDeletedIsTrue(final String workspaceId, final String sessionId, Pageable pageable);

	List<RecordFile> findByWorkspaceIdAndSessionIdAndDeleted(String workspaceId, String sessionId, boolean deleted);

	List<RecordFile> findByWorkspaceIdAndDeleted(String workspaceId, boolean deleted);
}

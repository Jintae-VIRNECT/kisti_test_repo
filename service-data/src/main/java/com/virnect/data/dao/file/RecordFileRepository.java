package com.virnect.data.dao.file;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.virnect.data.domain.file.RecordFile;

@Repository
public interface RecordFileRepository extends JpaRepository<RecordFile, Long>, CustomRecordFileRepository{
    Optional<RecordFile> findByWorkspaceIdAndSessionIdAndObjectName(final String workspaceId, final String sessionId, final String objectName);

	List<RecordFile> findByWorkspaceIdAndSessionIdAndDeleted(String workspaceId, String sessionId, boolean deleted);

	List<RecordFile> findByWorkspaceIdAndDeleted(String workspaceId, boolean deleted);
}

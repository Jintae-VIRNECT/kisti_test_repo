package com.virnect.data.dao.file;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.data.domain.file.RecordFile;

public interface CustomRecordFileRepository {

	List<RecordFile> findByWorkspaceIdAndSessionIdAndDeleted(String workspaceId, String sessionId, boolean delete);

	List<RecordFile> findByWorkspaceIdAndDeleted(String workspaceId, boolean deleted);

	Page<RecordFile> findByWorkspaceIdAndSessionIdAndDeletedUsePaging(final String workspaceId, final String sessionId, boolean delete, Pageable pageable);
}

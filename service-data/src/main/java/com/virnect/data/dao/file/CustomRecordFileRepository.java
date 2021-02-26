package com.virnect.data.dao.file;

import java.util.List;

import com.virnect.data.domain.file.RecordFile;

public interface CustomRecordFileRepository {

	List<RecordFile> findByWorkspaceIdAndSessionIdAndDeleted(String workspaceId, String sessionId, boolean delete);

	List<RecordFile> findByWorkspaceIdAndDeleted(String workspaceId, boolean deleted);
}

package com.virnect.data.dao.file;

import java.util.List;

import com.virnect.data.domain.file.File;

public interface CustomFileRepository {

	List<File> findByWorkspaceIdAndSessionIdAndDeleted(String workspaceId, String sessionId, boolean delete);

	List<File> findByWorkspaceIdAndDeleted(String workspaceId, boolean delete);

}

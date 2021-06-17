package com.virnect.data.dao.file;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileType;

public interface CustomFileRepository {

	List<File> findByWorkspaceIdAndSessionIdAndDeleted(String workspaceId, String sessionId, boolean delete);

	List<File> findByWorkspaceIdAndDeleted(String workspaceId, boolean delete);

	Page<File> findShareFileByWorkspaceAndSessionId(final String workspaceId, final String sessionId, boolean paging, Pageable pageable);

}

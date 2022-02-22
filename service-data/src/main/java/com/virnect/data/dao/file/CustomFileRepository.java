package com.virnect.data.dao.file;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.virnect.data.domain.file.File;
import com.virnect.data.domain.file.FileType;

public interface CustomFileRepository {

	List<File> findByWorkspaceIdAndSessionIdAndDeleted(String workspaceId, String sessionId, boolean delete);

	List<File> findByWorkspaceIdAndDeleted(String workspaceId, boolean delete);

	Page<File> findShareFileByWorkspaceAndSessionIdAndFileType(final String workspaceId, final String sessionId, boolean paging, Pageable pageable, FileType fileType);

	Page<File> findByWorkspaceIdAndSessionIdAndDeletedAndFileType(final String workspaceId, final String sessionId, boolean deleted, Pageable pageable);

	List<File> findShareFilesAll(final String workspaceId, final String sessionId);

}

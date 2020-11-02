package com.virnect.data.repository;

import com.virnect.data.dao.File;
import com.virnect.data.dao.RecordFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecordFileRepository extends JpaRepository<RecordFile, Long> {
    Page<RecordFile> findByWorkspaceIdAndSessionId(final String workspaceId, final String sessionId, Pageable pageable);

    Page<RecordFile> findByWorkspaceIdAndSessionIdAndDeletedIsTrue(final String workspaceId, final String sessionId, Pageable pageable);
}

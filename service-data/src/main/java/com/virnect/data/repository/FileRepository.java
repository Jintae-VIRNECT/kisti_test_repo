package com.virnect.data.repository;

import com.virnect.data.dao.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByWorkspaceIdAndSessionIdAndName(final String workspaceId, final String sessionId, final String name);

}

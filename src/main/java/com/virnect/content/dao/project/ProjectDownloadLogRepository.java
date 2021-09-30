package com.virnect.content.dao.project;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.content.domain.ContentDownloadLog;
import com.virnect.content.domain.project.ProjectDownloadLog;

public interface ProjectDownloadLogRepository
	extends JpaRepository<ProjectDownloadLog, Long>, ProjectDownloadLogCustomRepository {
}

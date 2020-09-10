package com.virnect.content.dao.contentdonwloadlog;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.content.domain.ContentDownloadLog;

public interface ContentDownloadLogRepository
	extends JpaRepository<ContentDownloadLog, Long>, ContentDownloadLogCustomRepository {
}

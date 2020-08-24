package com.virnect.content.dao;

import com.virnect.content.domain.ContentDownloadLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentDownloadLogRepository extends JpaRepository<ContentDownloadLog, Long>, ContentDownloadLogCustomRepository {
}

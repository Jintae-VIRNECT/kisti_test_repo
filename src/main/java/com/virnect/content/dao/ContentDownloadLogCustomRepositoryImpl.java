package com.virnect.content.dao;

import com.virnect.content.domain.ContentDownloadLog;
import com.virnect.content.domain.QContentDownloadLog;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;

public class ContentDownloadLogCustomRepositoryImpl extends QuerydslRepositorySupport implements ContentDownloadLogCustomRepository {
    public ContentDownloadLogCustomRepositoryImpl() {
        super(ContentDownloadLog.class);
    }

    @Override
    public long calculateResourceUsageAmountByWorkspaceIdAndStartDateAndEndDate(String workspaceId, LocalDateTime startDate, LocalDateTime endDate) {
        QContentDownloadLog qContentDownloadLog = QContentDownloadLog.contentDownloadLog;
        return from(qContentDownloadLog)
                .select(qContentDownloadLog.id)
                .where(qContentDownloadLog.workspaceUUID.eq(workspaceId)
                        .and(qContentDownloadLog.createdDate.between(startDate, endDate)))
                .fetchCount();
    }
}

package com.virnect.content.dao.contentdonwloadlog;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.ContentDownloadLog;
import com.virnect.content.domain.QContentDownloadLog;

public class ContentDownloadLogCustomRepositoryImpl extends QuerydslRepositorySupport
	implements ContentDownloadLogCustomRepository {
	public ContentDownloadLogCustomRepositoryImpl()
	{
		super(ContentDownloadLog.class);
	}

	@Override
	public long calculateResourceUsageAmountByWorkspaceIdAndStartDateAndEndDate(
		String workspaceId, LocalDateTime startDate, LocalDateTime endDate
	) {
		QContentDownloadLog qContentDownloadLog = QContentDownloadLog.contentDownloadLog;
		return from(qContentDownloadLog)
			.select(qContentDownloadLog.id)
			.where(qContentDownloadLog.workspaceUUID.eq(workspaceId)
				.and(qContentDownloadLog.createdDate.between(startDate, endDate)))
			.fetchCount();
	}

	@Override
	public long deleteAllContentDownloadLogByWorkspaceUUID(String workspaceUUID) {
		QContentDownloadLog qContentDownloadLog = QContentDownloadLog.contentDownloadLog;
		return delete(qContentDownloadLog).where(qContentDownloadLog.workspaceUUID.eq(workspaceUUID)).execute();
	}
}

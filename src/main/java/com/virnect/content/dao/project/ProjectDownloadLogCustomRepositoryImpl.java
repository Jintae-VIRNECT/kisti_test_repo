package com.virnect.content.dao.project;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.virnect.content.domain.project.ProjectDownloadLog;
import com.virnect.content.domain.project.QProjectDownloadLog;

public class ProjectDownloadLogCustomRepositoryImpl extends QuerydslRepositorySupport
	implements ProjectDownloadLogCustomRepository {
	public ProjectDownloadLogCustomRepositoryImpl() {
		super(ProjectDownloadLog.class);
	}

	@Override
	public long calculateResourceUsageAmountByWorkspaceIdAndStartDateAndEndDate(
		String workspaceId, LocalDateTime startDate, LocalDateTime endDate
	) {
		QProjectDownloadLog qProjectDownloadLog = QProjectDownloadLog.projectDownloadLog;
		return from(qProjectDownloadLog)
			.select(qProjectDownloadLog.id)
			.where(qProjectDownloadLog.workspaceUUID.eq(workspaceId)
				.and(qProjectDownloadLog.createdDate.between(startDate, endDate))).fetchCount();
	}

	@Override
	public long deleteAllContentDownloadLogByWorkspaceUUID(String workspaceUUID) {
		QProjectDownloadLog qProjectDownloadLog = QProjectDownloadLog.projectDownloadLog;
		return delete(qProjectDownloadLog).where(qProjectDownloadLog.workspaceUUID.eq(workspaceUUID)).execute();
	}
}
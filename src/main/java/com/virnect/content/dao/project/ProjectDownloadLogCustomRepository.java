package com.virnect.content.dao.project;

import java.time.LocalDateTime;

public interface ProjectDownloadLogCustomRepository {
	long calculateResourceUsageAmountByWorkspaceIdAndStartDateAndEndDate(
		String workspaceId, LocalDateTime startDate, LocalDateTime endDate
	);

	long deleteAllProjectDownloadLogByWorkspaceUUID(String workspaceUUID);
}

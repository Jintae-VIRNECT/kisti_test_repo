package com.virnect.content.dao.contentdonwloadlog;

import java.time.LocalDateTime;

public interface ContentDownloadLogCustomRepository {
	long calculateResourceUsageAmountByWorkspaceIdAndStartDateAndEndDate(
		String workspaceId, LocalDateTime startDate, LocalDateTime endDate
	);

	long deleteAllContentDownloadLogByWorkspaceUUID(String workspaceUUID);
}

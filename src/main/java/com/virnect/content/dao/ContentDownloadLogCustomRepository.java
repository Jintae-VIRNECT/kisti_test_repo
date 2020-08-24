package com.virnect.content.dao;

import com.virnect.content.dto.response.ContentResourceUsageInfoResponse;

import java.time.LocalDateTime;

public interface ContentDownloadLogCustomRepository {
    long calculateResourceUsageAmountByWorkspaceIdAndStartDateAndEndDate(String workspaceId, LocalDateTime startDate, LocalDateTime endDate);
}

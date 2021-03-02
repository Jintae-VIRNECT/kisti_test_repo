package com.virnect.dashboard.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomHistoryStatsRequest {

	private String workspaceId;
	private String userId;
	private String period;
	private int diffTime;

}

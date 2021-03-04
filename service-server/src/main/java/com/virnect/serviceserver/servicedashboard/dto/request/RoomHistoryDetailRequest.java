package com.virnect.serviceserver.servicedashboard.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomHistoryDetailRequest {

	private String workspaceId;
	private String sessionId;

}

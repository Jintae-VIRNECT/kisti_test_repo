package com.virnect.workspace.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@ApiModel
@Getter
@RequiredArgsConstructor
public class WorkspaceMemberPasswordChangeResponse {
	private final String requestUserId;
	private final String userId;
	private final LocalDateTime passwordChangedDate;

	@Override
	public String toString() {
		return "WorkspaceMemberPasswordChangeResponse{" +
			"requestUserId='" + requestUserId + '\'' +
			", userId='" + userId + '\'' +
			", passwordChangedDate=" + passwordChangedDate +
			'}';
	}
}

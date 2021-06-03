package com.virnect.data.redis.domain;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ForceLogoutMessage implements Serializable {

	private static final long serialVersionUID = -7772024247876390265L;

	private String workspaceId;			// Workspace ID
	private String userId;				// Master User ID
	private List<String> targetUserIds; // 로그아웃 대상 uuid 리스트

	@Override
	public String toString() {
		return "ForceLogoutMessage{" +
			"workspaceId='" + workspaceId + '\'' +
			", userId='" + userId + '\'' +
			", targetUserIds=" + targetUserIds +
			'}';
	}
}

package com.virnect.data.redis.domain;

import java.io.Serializable;
import java.util.List;

public class ForceLogoutMessage implements Serializable {
	private static final long serialVersionUID = -7772024247876390265L;
	private String workspaceId;
	private String userId;
	private List<String> targetUserIds;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getWorkspaceId() {
		return workspaceId;
	}

	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getTargetUserIds() {
		return targetUserIds;
	}

	public void setTargetUserIds(List<String> targetUserIds) {
		this.targetUserIds = targetUserIds;
	}

	@Override
	public String toString() {
		return "{" +
			"workspaceId='" + workspaceId + '\'' +
			", userId='" + userId + '\'' +
			", targetUserIds=" + targetUserIds +
			'}';
	}
}

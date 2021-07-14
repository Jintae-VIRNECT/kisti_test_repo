package com.virnect.data.dto.request.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.virnect.data.dto.request.PageRequest;

@Getter
@Setter
@Builder
public class FileDataRequest {

	private String workspaceId;
	private String sessionId;
	private String userId;
	private String objectName;
	private PageRequest pageable;
	private boolean deleted;
	private String id;
	private String order;

	@Override
	public String toString() {
		return "FileDataOption{" +
			"workspaceId='" + workspaceId + '\'' +
			", sessionId='" + sessionId + '\'' +
			", userId='" + userId + '\'' +
			", objectName='" + objectName + '\'' +
			", pageable=" + pageable +
			", deleted=" + deleted +
			", id='" + id + '\'' +
			'}';
	}
}

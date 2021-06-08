package com.virnect.data.redis.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusMessage implements Serializable {

	private static final long serialVersionUID = 2082503192322391880L;
	private String uuid;
	private String nickname;
	private String email;
	private String status;
	private String workspaceId;

	@Override
	public String toString() {
		return "StatusMessage{" +
			"uuid='" + uuid + '\'' +
			", nickname='" + nickname + '\'' +
			", email='" + email + '\'' +
			", status='" + status + '\'' +
			", workspaceId='" + workspaceId + '\'' +
			'}';
	}
}
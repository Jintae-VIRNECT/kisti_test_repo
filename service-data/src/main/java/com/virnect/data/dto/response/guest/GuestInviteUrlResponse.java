package com.virnect.data.dto.response.guest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel
public class GuestInviteUrlResponse {

	@ApiModelProperty(value = "coturn user name")
	private String workspaceId;

	@ApiModelProperty(value = "coturn user name")
	private String sessionId;

	@ApiModelProperty(value = "coturn user name")
	private String url;

	@Override
	public String toString() {
		return "GuestInviteUrlResponse{" +
			"workspaceId='" + workspaceId + '\'' +
			", sessionId='" + sessionId + '\'' +
			", url='" + url + '\'' +
			'}';
	}
}

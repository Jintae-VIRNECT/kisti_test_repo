package com.virnect.data.dto.response.group;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class RemoteGroupResponse {
	@ApiModelProperty(value = "Workspace Identifier", position = 1, example = "40f9bbee9d85dca7a34a0dd205aae718")
	private String workspaceId = "";

	@ApiModelProperty(value = "Group id", position = 2, example = "group id")
	private String groupId = "";

	@ApiModelProperty(value = "Group name", position = 3, example = "group name")
	private String groupName = "";

	@ApiModelProperty(value = "Group members", position = 4, example = "group members")
	private List<RemoteGroupMemberResponse> remoteGroupMemberResponses = new ArrayList<>();
}

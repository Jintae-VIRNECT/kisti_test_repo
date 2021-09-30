package com.virnect.data.dto.response.group;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel
public class FavoriteGroupResponse {

	@ApiModelProperty(value = "Workspace Identifier", position = 1, example = "40f9bbee9d85dca7a34a0dd205aae718")
	private String workspaceId = "";

	@ApiModelProperty(value = "Group id", position = 2, example = "group id")
	private String groupId = "";

	@ApiModelProperty(value = "Group name", position = 3, example = "group name")
	private String groupName = "";

	@ApiModelProperty(value = "Group Creator", position = 4, example = "uuid")
	private String uuid = "";

	@ApiModelProperty(value = "Group members", position = 5, example = "group members")
	private List<FavoriteGroupMemberResponse> favoriteGroupMemberResponses;

}

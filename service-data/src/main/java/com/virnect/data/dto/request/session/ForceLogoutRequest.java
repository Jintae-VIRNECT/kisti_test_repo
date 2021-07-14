package com.virnect.data.dto.request.session;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForceLogoutRequest {

	@ApiModelProperty(value = "Sender Master User Unique Identifier", example = "4d8d02b431ccbccbae9355324551123e", required = true, position = 2)
	@NotBlank
	private String userId;

	@ApiModelProperty(value = "Workspace Identifier", example = "aNgtFNEVMCOwG", required = true, position = 1)
	@NotBlank
	private String workspaceId;

	@ApiModelProperty(
		value = "Receiver Target User Unique Identifier",
		example = "[\n" +
			" \"4d8d02b431ccbccbae93553245511231\",\n" +
			" \"4d8d02b431ccbccbae93553245511232\",\n" +
			"\n" +
			"]",
		required = true,
		position = 3)
	@NotNull
	private List<String> targetUserIds;

	@Override
	public String toString() {
		return "ForceLogoutRequest{" +
			"userId='" + userId + '\'' +
			", workspaceId='" + workspaceId + '\'' +
			", targetUserIds=" + targetUserIds +
			'}';
	}
}

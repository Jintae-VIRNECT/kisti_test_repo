package com.virnect.data.dto.request.member;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GroupRequest {

	@ApiModelProperty(value = "Group name")
	private String groupName;

	@NotNull
	private List<String> memberList;

	@Override
	public String toString() {
		return "GroupRequest{" +
			", groupName='" + groupName + '\'' +
			", memberList=" + memberList +
			'}';
	}
}

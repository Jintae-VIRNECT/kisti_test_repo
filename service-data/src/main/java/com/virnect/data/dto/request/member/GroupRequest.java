package com.virnect.data.dto.request.member;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@ApiModel
public class GroupRequest {

	@ApiModelProperty(value = "Group name")
	private String groupName;

	@NotNull
	private List<String> memberList;

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setMemberList(List<String> memberList) {
		this.memberList = memberList.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "GroupRequest{" +
			", groupName='" + groupName + '\'' +
			", memberList=" + memberList +
			'}';
	}
}

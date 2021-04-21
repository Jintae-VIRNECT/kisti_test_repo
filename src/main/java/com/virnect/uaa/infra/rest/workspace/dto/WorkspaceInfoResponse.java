package com.virnect.uaa.infra.rest.workspace.dto;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-User
 * DATE: 2020-02-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceInfoResponse {
	@ApiModelProperty(value = "워크스페이스 식별자", notes = "워크 스페이스를 구별하기 위해 필요한 식별자", example = "4d6eab0860969a50acbfa4599fbb5ae8")
	private String uuid;
	@ApiModelProperty(value = "워크스페이스 고유번호", notes = "워크스페이스에 할당된 고유 번호", position = 1, example = "532878")
	private String pinNumber;
	@ApiModelProperty(value = "워크스페이스 소개", notes = "워크스페이스 설명 문구", position = 2, example = "SMIC Workspace")
	private String description;
	@ApiModelProperty(value = "해당 워크스페이스에서의 역할 ( MASTER(마스터), MANAGER(매니저), MEMBER(멤버) )", position = 3, example = "MASTER")
	private String role;
	@ApiModelProperty(value = "워크스페이스 생성일자", position = 4, example = "2020-01-20T14:05:30")
	private LocalDateTime createdDate;
	@ApiModelProperty(value = "워크스페이스 수정 일자", position = 5, example = "2020-01-20T14:05:30")
	private LocalDateTime updatedDate;
	@ApiModelProperty(value = "워크스페이스 소속 일자", position = 6, example = "2020-01-20T14:05:30")
	private LocalDateTime joinDate;
	@ApiModelProperty(value = "해당 워크스페이스의 마스터 유저 이름", position = 7, example = "SMIC")
	private String masterName;
	@ApiModelProperty(value = "해당 워크스페이스의 마스터 유저 프로필 사진", position = 8, example = "http://192.168.6.3:8081/users/upload/master.png")
	private String masterProfile;
	@ApiModelProperty(value = "워크스페이스 이름", position = 9, example = "Workspace")
	private String name;
	@ApiModelProperty(value = "워크스페이스 사진", position = 10, example = "http://192.168.6.3:8081/users/upload/master.png")
	private String profile;
	@ApiModelProperty(value = "해당 워크스페이스의 마스터 유저 nickName", position = 11, example = "닉네임")
	private String masterNickName;

	@Override
	public String toString() {
		return "WorkspaceInfoResponse{" +
			"uuid='" + uuid + '\'' +
			", pinNumber='" + pinNumber + '\'' +
			", description='" + description + '\'' +
			", role='" + role + '\'' +
			", createdDate=" + createdDate +
			", updatedDate=" + updatedDate +
			", joinDate=" + joinDate +
			", masterName='" + masterName + '\'' +
			", masterProfile='" + masterProfile + '\'' +
			", name='" + name + '\'' +
			", profile='" + profile + '\'' +
			", masterNickName='" + masterNickName + '\'' +
			'}';
	}
}

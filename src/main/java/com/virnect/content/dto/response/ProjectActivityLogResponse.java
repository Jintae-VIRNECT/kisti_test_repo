package com.virnect.content.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-11-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class ProjectActivityLogResponse {
	@ApiModelProperty(value = "프로젝트 식별자", position = 0, example = "")
	private String uuid;
	@ApiModelProperty(value = "프로젝트 이름", position = 1, example = "")
	private String name;
	@ApiModelProperty(value = "활동 유저 시퀀스 식별자", position = 2, example = "")
	private String userId;
	@ApiModelProperty(value = "활동 유저 식별자", position = 3, example = "")
	private String userUUID;
	@ApiModelProperty(value = "활동 유저 이름", position = 4, example = "")
	private String userName;
	@ApiModelProperty(value = "활동 유저 닉네임", position = 5, example = "")
	private String userNickname;
	@ApiModelProperty(value = "활동 유저 프로필 이미지", position = 6, example = "")
	private String userProfileImage;
	@ApiModelProperty(value = "활동 내용", position = 7, example = "")
	private String message;
	@ApiModelProperty(value = "활동 일자", position = 8, example = "")
	private LocalDateTime createdDate;
}

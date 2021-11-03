package com.virnect.content.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
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
@Builder
public class ProjectActivityLogResponse {
	@ApiModelProperty(value = "프로젝트 식별자", position = 0, example = "")
	private final String uuid;
	@ApiModelProperty(value = "프로젝트 이름", position = 1, example = "")
	private final String name;
	@ApiModelProperty(value = "활동 유저 시퀀스 식별자", position = 2, example = "")
	private final String userId;
	@ApiModelProperty(value = "활동 유저 식별자", position = 3, example = "")
	private final String userUUID;
	@ApiModelProperty(value = "활동 유저 이름", position = 4, example = "")
	private final String userName;
	@ApiModelProperty(value = "활동 유저 닉네임", position = 5, example = "")
	private final String userNickname;
	@ApiModelProperty(value = "활동 유저 프로필 이미지", position = 6, example = "")
	private final String userProfileImage;
	@ApiModelProperty(value = "활동 내용", position = 7, example = "")
	private final String message;
	@ApiModelProperty(value = "활동 일자", position = 8, example = "")
	private final LocalDateTime createdDate = LocalDateTime.now();
}

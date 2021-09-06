package com.virnect.content.dto.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.content.domain.EditPermission;
import com.virnect.content.domain.SharePermission;

/**
 * Project: PF-ContentManagement
 * DATE: 2021-09-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class EditPermissionRequest {
	@ApiModelProperty(value = "편집 권한 정보", example = "MEMBER", position = 0, required = true)
	@NotNull
	private EditPermission permission;
	@ApiModelProperty(value = "지정 멤버 목록", example = "[]", position = 1, required = false)
	private List<String> userList;
}

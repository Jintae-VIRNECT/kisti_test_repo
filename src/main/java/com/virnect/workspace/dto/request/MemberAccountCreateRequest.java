package com.virnect.workspace.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2020-10-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class MemberAccountCreateRequest {
	@ApiModelProperty(value = "계정 생성 요청 유저 식별자", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608", position = 0)
	@NotBlank
	private String userId;
	private List<MemberAccountCreateInfo> memberAccountCreateRequest;
}

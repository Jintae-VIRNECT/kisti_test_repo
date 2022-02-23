package com.virnect.workspace.dto.request;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Workspace
 * DATE: 2020-10-15
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class WorkspaceLogoUpdateRequest {
	@ApiModelProperty(value = "로고 변경 유저 식별자", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608", position = 0)
	@NotBlank
	private String userId;

	@ApiModelProperty(value = "로고 이미지", required = false, position = 1)
	private MultipartFile defaultLogo;

	@ApiModelProperty(value = "로고 그레이 이미지", required = false, position = 2)
	private MultipartFile greyLogo;

	@ApiModelProperty(value = "로고 화이트 이미지", required = false, position = 3)
	private MultipartFile whiteLogo;

	@Override
	public String toString() {
		return "WorkspaceLogoUpdateRequest{" +
			"userId='" + userId + '\'' +
			'}';
	}
}

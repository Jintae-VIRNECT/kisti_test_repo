package com.virnect.uaa.domain.user.dto.request;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.uaa.global.validator.ProfileImageValidate;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.07
 */

@Getter
@Setter
@ApiModel
public class ProfileImageUpdateRequest {
	@ApiModelProperty(value = "변경할 프로필 이미지", notes = "null인 경우 기본 이미지로 설정합니다.")
	@ProfileImageValidate
	private MultipartFile profile;

	public boolean isUpdateAsDefaultImage() {
		return profile == null;
	}
}

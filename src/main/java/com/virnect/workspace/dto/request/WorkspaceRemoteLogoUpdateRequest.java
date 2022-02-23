package com.virnect.workspace.dto.request;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import springfox.documentation.annotations.ApiIgnore;

@Getter
@Setter
@ApiModel
public class WorkspaceRemoteLogoUpdateRequest {
	@ApiModelProperty(value = "로고 변경 유저 식별자", required = true, example = "498b1839dc29ed7bb2ee90ad6985c608", position = 0)
	@NotBlank
	private String userId;

	@ApiModelProperty(value = "안드로이드 스플래시 로고 이미지", required = false, position = 4)
	private MultipartFile androidSplashLogo;

	@ApiModelProperty(value = "안드로이드 스플래시 로고 이미지", required = false, position = 4)
	private boolean defaultAndroidSplashLogo = false;

	@ApiModelProperty(value = "안드로이드 로그인 로고 이미지", required = false, position = 5)
	private MultipartFile androidLoginLogo;

	@ApiModelProperty(value = "안드로이드 스플래시 로고 이미지", required = false, position = 4)
	private boolean defaultAndroidLoginLogo = false;

	@ApiModelProperty(value = "홀로렌즈2 로고 이미지", required = false, position = 5)
	private MultipartFile hololens2Logo;

	@ApiModelProperty(value = "안드로이드 스플래시 로고 이미지", required = false, position = 4)
	private boolean defaultHololens2Logo = false;

	@ApiIgnore
	public boolean isUpdateAndroidSplashLogo() {
		return defaultAndroidSplashLogo || androidSplashLogo != null;
	}

	@ApiIgnore
	public boolean isUpdateAndroidLoginLogo() {
		return defaultAndroidLoginLogo || androidLoginLogo != null;
	}

	@ApiIgnore
	public boolean isUpdateHololens2Logo() {
		return defaultHololens2Logo || hololens2Logo != null;
	}
}

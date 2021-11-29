package com.virnect.download.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.download.domain.AppUpdateStatus;

/**
 * Project: PF-Download
 * DATE: 2021-11-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class AdminCommonAppUploadRequest {
	@ApiModelProperty(value = "앱 구동 시스템 정보(IOS, ANDROID, WINDOWS, WINDOWS_UWP)", example = "ANDROID", required = true)
	@NotBlank(message = "앱 구동 시스템 정보는 반드시 입력되어야 합니다.")
	private String operationSystem;
	@ApiModelProperty(value = "앱 제품 정보(REMOTE, MAKE, VIEW)", position = 1, example = "REMOTE", required = true)
	private String productName;
	@ApiModelProperty(value = "앱 구동 디바이스 타입(MOBILE, PC, REALWEAR, LINKFLOW, HOLOLENS)", position = 2, example = "MOBILE", required = true)
	@NotBlank(message = "앱 구동 디바이스 타입 정보는 반드시 입력되어야 합니다.")
	private String deviceType;
	@ApiModelProperty(value = "앱 구동 디바이스 모델(SMARTPHONE_TABLET, HTM-1, HOLOLENS_2, FITT/NEXX, WINDOWS_10)", position = 3, example = "SMARTPHONE_TABLET", required = true)
	@NotBlank(message = "앱 구동 디바이스 모델 정보는 반드시 입력되어야 합니다.")
	private String deviceModel;
	@NotNull(message = "앱 업로드 파일은 반드시 있어야합니다.")
	private MultipartFile uploadAppFile;
	@ApiModelProperty(value = "앱 버전 정보", position = 4, example = "1.3.2", required = true)
	@NotBlank(message = "앱 버전 코드는 반드시 있어야합니다.")
	@Pattern(regexp = "^[0-9.]+$", message = "숫자와 마침표(.)만 입력할 수 있습니다.")
	private String versionName;

	@Override
	public String toString() {
		return "AdminCommonAppUploadRequest{" +
			"operationSystem='" + operationSystem + '\'' +
			", productName='" + productName + '\'' +
			", deviceType='" + deviceType + '\'' +
			", deviceModel='" + deviceModel + '\'' +
			", versionName='" + versionName + '\'' +
			'}';
	}

	@ApiModelProperty(hidden = true)
	public long getVersionCode() {
		return Long.parseLong(StringUtils.deleteAny(versionName, "."));
	}
}

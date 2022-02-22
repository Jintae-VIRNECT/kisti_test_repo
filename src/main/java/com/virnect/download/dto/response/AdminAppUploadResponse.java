package com.virnect.download.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Download
 * DATE: 2021-11-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class AdminAppUploadResponse {
	@ApiModelProperty(value = "앱 식별자", example = "05d1-8795535a1234")
	private String uuid = "";
	@ApiModelProperty(value = "앱 버전 정보", position = 1, example = "v1.0.20")
	private String version = "";
	@ApiModelProperty(value = "앱 구동 디바이스 타입(ex: MOBILE, PC, REALWARE, HOLORENSE)", position = 2, example = "PC")
	private String deviceType = "";
	@ApiModelProperty(value = "앱 구동 디바이스 모델(ex: SMARTPHONE_TABLET, HTM-1, HOLOLENS_2, FITT/NEXX, WINDOWS_10)", position = 3, example = "PC")
	private String deviceModel = "";
	@ApiModelProperty(value = "앱 구동 시스템 정보(ex: ANDROID, WINDOWS)", position = 4, example = "Android")
	private String operationSystem;
	@ApiModelProperty(value = "앱 패키지명", position = 5, example = "com.virnect.platform.server")
	private String packageName;
	@ApiModelProperty(value = "앱 설치 파일 주소", position = 6, example = "https://s3.url.com")
	private String appUrl = "";
	@ApiModelProperty(value = "앱 제품 정보", position = 7, example = "REMOTE")
	private String productName = "";
}

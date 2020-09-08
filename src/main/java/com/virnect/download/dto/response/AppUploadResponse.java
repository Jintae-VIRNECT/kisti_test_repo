package com.virnect.download.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Download
 * DATE: 2020-08-06
 * AUTHOR: jeonghyeon.chang (johnmark)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class AppUploadResponse {
	@ApiModelProperty(value = "앱 식별자", example = "05d1-8795535a1234")
	private String uuid = "";
	@ApiModelProperty(value = "앱 버전 정보", position = 1, example = "v1.0.20")
	private String version = "";
	@ApiModelProperty(value = "앱 구동 디바이스 타입(ex: MOBILE, PC, REALWARE, HOLORENSE)", position = 2, example = "PC")
	private String deviceType = "";
	@ApiModelProperty(value = "앱 구동 시스템 정보(ex: ANDROID, WINDOWS)", position = 3, example = "Android")
	private String operationSystem;
	@ApiModelProperty(value = "앱 패키지명", position = 4, example = "com.virnect.platform.server")
	private String packageName;
	@ApiModelProperty(value = "앱 설치 파일 주소", position = 5, example = "https://s3.url.com")
	private String appUrl = "";
	@ApiModelProperty(value = "앱 제품 정보", position = 6, example = "REMOTE")
	private String productName = "";

}

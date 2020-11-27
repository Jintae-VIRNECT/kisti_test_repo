package com.virnect.download.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SignedAppInfoResponse {
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
	@ApiModelProperty(value = "앱 강제 업데이트 여부", position = 7, example = "false")
	private boolean isUpdateRequired;
	@ApiModelProperty(value = "앱 등록 정보 생성일자", position = 8)
	private LocalDateTime createdDate;
	@ApiModelProperty(value = "앱 등록 정보 업데이트 일자", position = 9)
	private LocalDateTime appInfoUpdatedDate;
}

package com.virnect.uaa.infra.rest.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class AppDetailInfoResponse {
	@ApiModelProperty(value = "앱 식별자", example = "05d1-8795535a1234")
	private String uuid = "";
	@ApiModelProperty(value = "앱 버전 정보", position = 1, example = "v1.0.20")
	private String version = "";
	@ApiModelProperty(value = "앱 버전 코드 정보", position = 2, example = "1020")
	private long versionCode = 0L;
	@ApiModelProperty(value = "앱 구동 디바이스 타입(ex: MOBILE, PC, REALWARE, HOLORENSE)", position = 3, example = "PC")
	private String deviceType = "";
	@ApiModelProperty(value = "앱 구동 시스템 정보(ex: ANDROID, WINDOWS)", position = 4, example = "Android")
	private String operationSystem;
	@ApiModelProperty(value = "앱 패키지명", position = 5, example = "com.virnect.platform.server")
	private String packageName;
	@ApiModelProperty(value = "앱 설치 파일 주소", position = 6, example = "https://s3.url.com")
	private String appUrl = "";
	@ApiModelProperty(value = "앱 제품 정보", position = 7, example = "REMOTE")
	private String productName = "";
	@ApiModelProperty(value = "앱 서명 키 정보", position = 8, example = "asdasdasdasd")
	private String signingKey = "";
	@ApiModelProperty(value = "앱 강제 업데이트 여부", position = 9, example = "false")
	private boolean isUpdateRequired;

	@Override
	public String toString() {
		return "AppDetailInfoResponse{" +
			"uuid='" + uuid + '\'' +
			", version='" + version + '\'' +
			", versionCode=" + versionCode +
			", deviceType='" + deviceType + '\'' +
			", operationSystem='" + operationSystem + '\'' +
			", packageName='" + packageName + '\'' +
			", appUrl='" + appUrl + '\'' +
			", productName='" + productName + '\'' +
			", signingKey='" + signingKey + '\'' +
			", isUpdateRequired=" + isUpdateRequired +
			'}';
	}
}

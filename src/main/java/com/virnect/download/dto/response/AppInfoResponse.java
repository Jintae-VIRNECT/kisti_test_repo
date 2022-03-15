package com.virnect.download.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.download.domain.DeviceSupportUpdateStatus;

/**
 * Project: PF-Download
 * DATE: 2020-07-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class AppInfoResponse {
	@ApiModelProperty(value = "앱 id", position = 0)
	private Long id = 0L;
	@ApiModelProperty(value = "앱 식별자", position = 1, example = "")
	private String uuid = "";
	@ApiModelProperty(value = "앱 버전 정보", position = 2, example = "")
	private String version = "";
	@ApiModelProperty(value = "앱 버전 코드", position = 3, example = "")
	private Long versionCode = 0L;
	@ApiModelProperty(value = "앱 디바이스 이름", position = 4, example = "")
	private String deviceName = "";
	@ApiModelProperty(value = "앱 디바이스 타입", position = 5, example = "")
	private String deviceType = "";
	@ApiModelProperty(value = "앱 디바이스 자동업데이트 지원 유무", position = 6, example = "")
	private DeviceSupportUpdateStatus deviceSupportUpdateStatus;
	@ApiModelProperty(value = "패키지 이름 정보", position = 7, example = "")
	private String packageName = "";
	@ApiModelProperty(value = "앱 디바이스 타입", position = 8, example = "")
	private String os = "";
	@ApiModelProperty(value = "앱 설치파일", position = 9, example = "")
	private String appUrl = "";
	@ApiModelProperty(value = "앱 가이드파일", position = 10, example = "")
	private String guideUrl = "";
	@ApiModelProperty(value = "앱 썸네일 url", position = 11, example = "")
	private String imageUrl = "";
	@ApiModelProperty(value = "앱 릴리즈 일자", position = 12, example = "")
	private LocalDateTime releaseTime = LocalDateTime.now();
}

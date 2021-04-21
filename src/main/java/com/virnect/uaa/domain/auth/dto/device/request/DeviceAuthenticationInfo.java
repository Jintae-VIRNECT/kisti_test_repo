package com.virnect.uaa.domain.auth.dto.device.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class DeviceAuthenticationInfo {
	@NotBlank
	@ApiModelProperty(value = "어플리케이션 서명키", example = "signingKeyvaluessigningKeyvalues")
	private String verificationCode;
	@NotBlank
	@ApiModelProperty(value = "패키지명", position = 1, example = "com.virnect.mobile.dev2")
	private String packageName;
	@NotBlank
	@ApiModelProperty(value = "기기 타입", position = 2, example = "REALWEARE")
	private String deviceType;
	@NotBlank
	@ApiModelProperty(value = "앱 버전 코드", position = 3, example = "2001")
	private long versionCode = 0L;
	@NotBlank
	@ApiModelProperty(value = "앱 버전 명", position = 4, example = "2.0.1")
	private String versionName;
	@ApiModelProperty(value = "기기 언어 설정 정보", position = 5, example = "한국어")
	private String language;
	@ApiModelProperty(value = "기기 국가 설정 정보", position = 6, example = "대한민국")
	private String country;
	@ApiModelProperty(value = "기기 로케일 설정 정보", position = 7, example = "ko_KR")
	private String locale;
	@ApiModelProperty(value = "기기 커널 정보", position = 8, example = "4.4.78-13953229")
	private String kernel;
	@ApiModelProperty(value = "기기 os 버전 정보", position = 9, example = "8.1.0")
	private String os;
	@ApiModelProperty(value = "기기 모델명 정보", position = 10, example = "SM-T835N")
	private String model;
	@ApiModelProperty(value = "기기 제조사 정보", position = 11, example = "samsung")
	private String manufacture;
	@ApiModelProperty(value = "기기 디스플레이 규격 정보", position = 12, example = "1600x2452")
	private String display;
	@ApiModelProperty(value = "기기 식별 키 정보(인증이 이미 된 기기의 경우, 서버에서 내려준 인증키를 설정)", position = 13, example = "9acb86ab-e4d2-4ee1-962c-59c9a207980f")
	private String deviceId;

	@Override
	public String toString() {
		return "DeviceAuthenticationInfo{" +
			"verificationCode='" + verificationCode + '\'' +
			", packageName='" + packageName + '\'' +
			", deviceType=" + deviceType +
			", versionCode=" + versionCode + "" +
			", versionName='" + versionName + '\'' +
			", language='" + language + '\'' +
			", country='" + country + '\'' +
			", locale='" + locale + '\'' +
			", kernel='" + kernel + '\'' +
			", os='" + os + '\'' +
			", model='" + model + '\'' +
			", manufacture='" + manufacture + '\'' +
			", display='" + display + '\'' +
			'}';
	}
}

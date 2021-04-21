package com.virnect.uaa.domain.auth.dto.device.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class DeviceAuthenticationResponse {
	@ApiModelProperty(value = "앱 인증 키(헤더에 포함해야함)", example = "$2a$15$SNFs4WVzQo14PND8PHdA0u")
	private String deviceAuthKey;
	@ApiModelProperty(value = "데이터 암호화 키(AES 256, 32글자)", position = 1, example = "asdjhaksbdzbxcizxcnm12nbd78123@!")
	private String secretKey;
	@ApiModelProperty(value = "디바이스 식별 아이디(uuid base)", position = 2, example = "9acb86ab-e4d2-4ee1-962c-59c9a207980f")
	private String deviceId;
	@ApiModelProperty(value = "최신 앱 버전 코드 정보", position = 3, example = "22001")
	private long latestAppVersionCode;
	@ApiModelProperty(value = "최신 앱 버전 네임 정보", position = 3, example = "2.0.1")
	private String latestAppVersionName;
	@ApiModelProperty(value = "앱 업그레이드 여부", position = 5, example = "true")
	private boolean isVersionUp;
	@ApiModelProperty(value = "앱 강제 업데이트 여부", position = 6, example = "false")
	private boolean isUpdateRequired;
	@ApiModelProperty(value = "앱 설치(or 다운로드) URL 링크", position = 7, example = "http://file.virnect.com/app/some-app.apk")
	private String appUpdateUrl;
	@ApiModelProperty(value = "앱 인증 키", hidden = true)
	private String appSignature;

	@Override
	public String toString() {
		return "DeviceAuthenticationResponse{" +
			"deviceAuthKey='" + deviceAuthKey + '\'' +
			", secretKey='" + secretKey + '\'' +
			", deviceId='" + deviceId + '\'' +
			", latestAppVersionCode=" + latestAppVersionCode +
			", latestAppVersionName='" + latestAppVersionName + '\'' +
			", isVersionUp=" + isVersionUp +
			", isUpdateRequired=" + isUpdateRequired +
			", appUpdateUrl='" + appUpdateUrl + '\'' +
			", appSignature='" + appSignature + '\'' +
			'}';
	}
}

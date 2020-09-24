package com.virnect.download.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.download.domain.AppStatus;
import com.virnect.download.domain.AppUpdateStatus;

@Getter
@Setter
public class AppVersionInfoResponse {
	@ApiModelProperty(value = "앱 id", position = 0)
	private Long id = 0L;
	@ApiModelProperty(value = "앱 식별자", position = 1, example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String uuid = "";
	@ApiModelProperty(value = "앱 버전 정보", position = 2, example = "v2.2.3")
	private String versionName = "";
	@ApiModelProperty(value = "앱 디바이스 이름", position = 3, example = "REALWEAR")
	private String deviceName = "";
	@ApiModelProperty(value = "앱 설치파일", position = 5, example = "https://s3.url.com")
	private String appUrl = "";
	@ApiModelProperty(value = "앱 패키지명", position = 6, example = "com.virnect.platform.server")
	private String packageName;
	@ApiModelProperty(value = "앱 가이드파일", position = 7, example = "https://file.virnect.com/Guide/remote_user_guide.pdf")
	private String guideUrl = "";
	@ApiModelProperty(value = "앱 썸네일 url", position = 8, example = "https://file.virnect.com/resource/view_android.png")
	private String imageUrl = "";
	@ApiModelProperty(value = "앱 등록 일자", position = 9, example = "2020-01-20T14:05:30")
	private LocalDateTime registerDate;
	@ApiModelProperty(value = "앱 배포 상태 정보", position = 10, example = "ACTIVE")
	private AppStatus appStatus;
	@ApiModelProperty(value = "앱 업데이트 상태 정보", position = 11, example = "REQUIRED")
	private AppUpdateStatus appUpdateStatus;
	@JsonProperty(value = "isSigningApp")
	@ApiModelProperty(value = "앱 서명 여부", position = 12, example = "true")
	private boolean isSigningApp;

	@Override
	public String toString() {
		return "AppVersionInfoResponse{" +
			"id='" + id + '\'' +
			", uuid='" + uuid + '\'' +
			", versionName='" + versionName + '\'' +
			", deviceName='" + deviceName + '\'' +
			", appUrl='" + appUrl + '\'' +
			", packageName='" + packageName + '\'' +
			", guideUrl='" + guideUrl + '\'' +
			", imageUrl='" + imageUrl + '\'' +
			", registerDate=" + registerDate +
			", appStatus=" + appStatus +
			", appUpdateStatus=" + appUpdateStatus +
			", isSigningApp='" + isSigningApp + '\'' +
			'}';
	}
}

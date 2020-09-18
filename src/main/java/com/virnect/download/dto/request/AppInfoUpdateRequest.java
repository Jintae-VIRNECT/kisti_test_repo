package com.virnect.download.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.download.domain.AppStatus;
import com.virnect.download.domain.AppUpdateStatus;

@Getter
@Setter
@ApiModel
public class AppInfoUpdateRequest {
	@NotBlank
	@ApiModelProperty(value = "앱 고유 식별자", example = "aaa-bb-ccc-33")
	private String appUUID;

	@ApiModelProperty(value = "앱 활성화 정보", position = 1, example = "ACTIVE")
	private AppStatus appStatus;

	@ApiModelProperty(value = "앱 강제 업데이트 여부", position = 2, example = "OPTIONAL")
	private AppUpdateStatus appUpdateStatus;

	@Override
	public String toString() {
		return "AppInfoUpdateRequest{" +
			"appUUID='" + appUUID + '\'' +
			", appStatus=" + appStatus +
			", appUpdateStatus=" + appUpdateStatus +
			'}';
	}
}

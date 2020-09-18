package com.virnect.download.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@ApiModel
@RequiredArgsConstructor
public class AppVersionInfoListResponse {
	@ApiModelProperty(value = "앱 정보 리스트")
	private final List<AppVersionInfoResponse> appInfoList;

	@Override
	public String toString() {
		return "AppVersionInfoListResponse{" +
			"appInfoList=" + appInfoList +
			'}';
	}
}

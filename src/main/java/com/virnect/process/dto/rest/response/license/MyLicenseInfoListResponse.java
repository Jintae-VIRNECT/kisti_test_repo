package com.virnect.process.dto.rest.response.license;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-10-06
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class MyLicenseInfoListResponse {
	@ApiModelProperty(value = "내 라이선스 정보 목록")
	private List<MyLicenseInfoResponse> licenseInfoList;
}

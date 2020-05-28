package com.virnect.workspace.dto.rest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-05-28
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

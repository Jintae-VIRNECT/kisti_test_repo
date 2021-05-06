package com.virnect.process.dto.rest.response.license;

import java.time.LocalDateTime;

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
public class MyLicenseInfoResponse {
	@ApiModelProperty(value = "라이선스 식별자", example = "1")
	private long id;
	@ApiModelProperty(value = "라이선스 시리얼 키", example = "29923633-47D7-439C-B515-94FF4A9B5BB0")
	private String serialKey;
	@ApiModelProperty(value = "라이선스 제품 플랜 상태 (ACTIVE, INACTIVE, EXCEEDED)", example = "ACTIVE")
	private String productPlanStatus;
	@ApiModelProperty(value = "라이선스 상태")
	private String status;
	@ApiModelProperty(value = "라이선스 할당 제품명", example = "MAKE")
	private String productName;
	// @ApiModelProperty(value = "라이선스 타입", example = "BASIC")
	// private String licenseType;
	@ApiModelProperty(value = "라이선스 정보 생성일", example = "2020-04-16T16:34:35")
	private LocalDateTime createdDate;
	@ApiModelProperty(value = "라이선스 정보 수정일", example = "2020-04-16T16:34:35")
	private LocalDateTime updatedDate;
}

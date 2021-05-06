package com.virnect.process.dto.rest.response.license;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-07-28
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class LicenseDetailInfoResponse {
	@ApiModelProperty(value = "라이선스 키", example = "29923633-47D7-439C-B515-94FF4A9B5BB0")
	private String licenseKey;
	@ApiModelProperty(value = "할당된 유저 식별자", position = 1, example = "")
	private String userId;
	@ApiModelProperty(value = "라이선스 상태", position = 2, example = "UNUSE")
	private String status;
	@ApiModelProperty(value = "라이선스 생성일", position = 3, example = "2020-04-16T16:34:35")
	private LocalDateTime createdDate;
	@ApiModelProperty(value = "라이선스 생성일", position = 4, example = "2020-04-16T16:34:35")
	private LocalDateTime updatedDate;
}

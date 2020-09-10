package com.virnect.download.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Download
 * DATE: 2020-08-06
 * AUTHOR: jeonghyeon.chang (johnmark)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ApiModel
public class AppUploadRequest {
	@ApiModelProperty(value = "앱 구동 시스템 정보(ANDROID, WINDOWS)", example = "ANDORID")
	@NotBlank(message = "앱 구동 시스템 정보는 반드시 입력되어야 합니다.")
	private String operationSystem;
	@ApiModelProperty(value = "앱 제품 정보(REMOTE,MAKE,VIEW)", position = 1, example = "REMOTE")
	private String productName;
	@ApiModelProperty(value = "앱 구동 디바이스 타입(ex: MOBILE, PC, REALWARE)", position = 2, example = "MOBILE")
	@NotBlank(message = "앱 구동 디바이스 타입 정보는 반드시 입력되어야 합니다.")
	private String deviceType;
	@ApiModelProperty(value = "업로드 앱 파일", dataType = "__file", position = 3, hidden = true)
	@NotNull(message = "앱 업로드 파일은 반드시 있어야합니다.")
	private MultipartFile uploadAppFile;

	@Override
	public String toString() {
		return "AppUploadRequest{" +
			"operationSystem='" + operationSystem + '\'' +
			", productName='" + productName + '\'' +
			", deviceType='" + deviceType + '\'' +
			'}';
	}
}

package com.virnect.process.dto.rest.response.workspace;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-15
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkspaceSettingInfoResponse {
	@ApiModelProperty(value = "워크스페이스 설정 id", required = true, example = "")
	private Long settingId;

	@ApiModelProperty(value = "워크스페이스 설정 이름", required = true, example = "")
	private String settingName;

	@ApiModelProperty(value = "워크스페이스 설정 이름 설명", required = true, example = "")
	private String settingDescription;

	@ApiModelProperty(value = "워크스페이스 설정 값", required = true, example = "")
	private String settingValue;

	@ApiModelProperty(value = "워크스페이스 설정 선택 목록", required = true, example = "")
	private List<String> settingValueOptionList;

	@ApiModelProperty(value = "워크스페이스 설정 결제 타입", required = true, example = "")
	private String paymentType;

	@ApiModelProperty(value = "워크스페이스 설정 추가 일자", required = true, example = "")
	private LocalDateTime createdDate;

	@ApiModelProperty(value = "워크스페이스 설정 수정 일자", required = true, example = "")
	private LocalDateTime updatedDate;
}

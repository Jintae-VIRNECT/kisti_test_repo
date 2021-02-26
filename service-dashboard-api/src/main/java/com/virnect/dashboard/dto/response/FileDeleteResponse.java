package com.virnect.dashboard.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class FileDeleteResponse {

	@ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스 구별합니다.")
	private String workspaceId;
	@ApiModelProperty(value = "원격협업 식별자", notes = "해당 식별자를 통해 파일 경로 구분 합니다.", position = 1)
	private String sessionId;
	@ApiModelProperty(value = "파일 이름", notes = "삭제된 파일 이름 입니다", position = 2, example = "직박구리 파일")
	private String fileName;
}

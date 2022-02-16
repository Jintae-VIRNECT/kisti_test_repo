package com.virnect.content.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class LiveShareRoomLeaveRequest {
	@ApiModelProperty(value = "컨텐츠 공유 정보 기록 데이터", example = "{\"ObjectName\":\"object\"}", position = 0, required = true)
	private String data;
}

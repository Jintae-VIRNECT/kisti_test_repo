package com.virnect.serviceserver.servicedashboard.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ResultResponse {
	@ApiModelProperty
	private Boolean result;

	@Override
	public String toString() {
		return "ResultResponse{" +
			"result='" + result + '\'' +
			'}';
	}
}

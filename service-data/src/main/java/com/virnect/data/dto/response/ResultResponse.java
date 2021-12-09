package com.virnect.data.dto.response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ResultResponse {
	@ApiModelProperty(value = "User Identifier", example = "410df50ca6e32db0b6acba09bcb457ff")
	public String userId = "";
	@ApiModelProperty(value = "Responses result", position = 1, example = "true")
	public Boolean result;
	@ApiModelProperty(value = "Response Date", position = 2, example = "2020-01-20T14:05:30")
	public LocalDateTime resultDate;

	@ApiModelProperty(value = "Result reason", example = "{\n" +
		"  \"custom1\": \"string\",\n" +
		"  \"custom2\": \"string\"\n" +
		"}", required = true, position = 3)
	private Map<Object, Object> reason = new HashMap<>();

	@Builder
	public ResultResponse(String userId, Boolean result) {
		this.userId = userId;
		this.result = result;
		this.resultDate = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "ResultResponse{" +
			"userId='" + userId + '\'' +
			"result='" + result + '\'' +
			"resultDate='" + resultDate + '\'' +
			'}';
	}
}

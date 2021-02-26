package com.virnect.dashboard.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HistoryCountResponse {

	@ApiModelProperty(value = "History count Array(My)", example = "[1,2,3,...,24]")
	private final int[] myHistory;

	@ApiModelProperty(value = "History count Array(Entire)", example = "[1,2,3,...,24]")
	private final int[] entireHistory;

	@ApiModelProperty(value = "History count Array(My within Duration)", example = "[10,20,30,...,24]")
	private final long[] myDuration;

	@ApiModelProperty(value = "History count Array(Entire within Duration)", example = "[10,20,30,...,24]")
	private final long[] entireDuration;

	public HistoryCountResponse(int[] myHistory, int[] entireHistory, long[] myDuration, long[] entireDuration) {
		this.myHistory = myHistory;
		this.entireHistory = entireHistory;
		this.myDuration = myDuration;
		this.entireDuration = entireDuration;
	}
}

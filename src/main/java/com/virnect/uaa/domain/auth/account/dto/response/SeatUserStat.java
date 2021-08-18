package com.virnect.uaa.domain.auth.account.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SeatUserStat {
	@ApiModelProperty(value = "전체 시트 계정 수", example = "10")
	int totalSeatUser;
	@ApiModelProperty(value = "할당된 시트 계정 수", position = 1, example = "5")
	int allocateSeatUserTotal;
	@ApiModelProperty(value = "할당 안된 시트 계정 수", position = 2, example = "5")
	int deallocateSeatUserTotal;

	@Override
	public String toString() {
		return "SeatUserStat{" +
			"totalSeatUser=" + totalSeatUser +
			", allocateSeatUserTotal=" + allocateSeatUserTotal +
			", deallocateSeatUserTotal=" + deallocateSeatUserTotal +
			'}';
	}
}

package com.virnect.data.dto.rest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GuestUserStat {
	@ApiModelProperty(value = "전체 시트 계정 수", example = "10")
	int totalGuestUser;
	@ApiModelProperty(value = "할당된 시트 계정 수", position = 1, example = "5")
	int allocateGuestUserTotal;
	@ApiModelProperty(value = "할당 안된 시트 계정 수", position = 2, example = "5")
	int deallocateGuestUserTotal;

	@Override
	public String toString() {
		return "GuestUserStat{" +
			"totalGuestUser=" + totalGuestUser +
			", allocateGuestUserTotal=" + allocateGuestUserTotal +
			", deallocateGuestUserTotal=" + deallocateGuestUserTotal +
			'}';
	}
}

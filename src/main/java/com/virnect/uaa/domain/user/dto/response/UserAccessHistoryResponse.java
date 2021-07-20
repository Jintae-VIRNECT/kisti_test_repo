package com.virnect.uaa.domain.user.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */

@Getter
@ApiModel
@RequiredArgsConstructor
public class UserAccessHistoryResponse {
	@ApiModelProperty(value = "접속 이력 정보")
	private final List<UserAccessDeviceInfoResponse> accessInfoList;
	@ApiModelProperty(value = "페이징 정보", position = 1)
	private final PageMetadataResponse pageMeta;
}

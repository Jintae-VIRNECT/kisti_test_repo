package com.virnect.uaa.domain.user.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.10
 */

@Getter
@Setter
@ApiModel
public class UserEmailFindResponse {
	@ApiModelProperty(value = "찾은 이메일 정보 리스트")
	List<UserEmailFindInfoResponse> emailFindInfoList;
}

package com.virnect.uaa.domain.user.dto.response;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.09
 */

@Getter
@Setter
@ApiModel
public class UserAccessDeviceInfoResponse {
	@ApiModelProperty(value = "접속 아이피", example = "127.0.0.1")
	private String ip;
	@ApiModelProperty(value = "접속 기기 정보", example = "Chrome")
	private String device;
	@ApiModelProperty(value = "접속 위치 정보", example = "South Korea - Yongsan-gu")
	private String location;
	@ApiModelProperty(value = "접속 일자", example = "2020-05-12T10:01:44.0")
	private LocalDateTime lastLoggedIn;

	@Override
	public String toString() {
		return "UserAccessDeviceInfoResponse{" +
			"ip='" + ip + '\'' +
			", device='" + device + '\'' +
			", location='" + location + '\'' +
			", lastLoggedIn=" + lastLoggedIn +
			'}';
	}
}

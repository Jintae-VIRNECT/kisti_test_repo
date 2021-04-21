package com.virnect.uaa.domain.auth.dto.user.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.17
 */
@Getter
@Setter
public class TokenRefreshRequest {
	@NotBlank
	private String accessToken;
	@NotBlank
	private String refreshToken;
}

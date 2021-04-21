package com.virnect.uaa.domain.auth.dto.user.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.14
 */
@Getter
@Setter
public class OTPQRGenerateRequest {
	private String userId;
	private String email;
}

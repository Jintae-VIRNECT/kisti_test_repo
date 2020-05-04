package com.virnect.login.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Email Authentication Response Dto Class
 * @since 2020.03.11
 */
@Getter
@Setter
public class EmailVerificationResponse {
    private boolean result;
    private String sessionCode;
}

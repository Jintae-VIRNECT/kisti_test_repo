package com.virnect.login.dto;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Login Response Dto Class
 * @since 2020.03.11
 */
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String scope;
    private long expire;
    private String grant;
}

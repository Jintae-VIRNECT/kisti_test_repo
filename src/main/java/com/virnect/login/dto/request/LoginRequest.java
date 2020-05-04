package com.virnect.login.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Login
 * @email practice1356@gmail.com
 * @description Login Request Dto Class
 * @since 2020.03.11
 */

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "아이디를 반드시 입력해주세요")
    private String email;
    @NotBlank(message = "비밀번호를 반드시 입력해주세요")
    private String password;
}

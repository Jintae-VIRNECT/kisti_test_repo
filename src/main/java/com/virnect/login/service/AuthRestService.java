package com.virnect.login.service;

import com.virnect.login.common.ApiResponse;
import com.virnect.login.dto.LoginRequest;
import com.virnect.login.dto.LoginResponse;
import com.virnect.login.dto.request.LogoutRequest;
import com.virnect.login.dto.response.LogoutResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @project: PF-Login
 * @author: jeonghyeon.chang (johnmark)
 * @email: practice1356@gmail.com
 * @since: 2020.03.09
 * @description: Auth Service Feign Client Interface
 */

@FeignClient(name = "auth-service")
public interface AuthRestService {
    @PostMapping("/auth/signin")
    ApiResponse<LoginResponse> loginRequestHandler(LoginRequest loginRequest);

    @PostMapping("/auth/signout")
    ApiResponse<LogoutResponse> logoutRequestHandler(LogoutRequest logoutRequest);
}

package com.virnect.uaa.domain.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

import com.virnect.uaa.domain.auth.dto.user.request.SessionLoginRequest;
import com.virnect.uaa.domain.auth.dto.user.response.SessionLogoutResponse;
import com.virnect.uaa.domain.user.dto.response.UserDetailsInfoResponse;
import com.virnect.uaa.global.common.ApiResponse;

@RestController
@RequestMapping("/")
public class SessionAuthController {

	// Spring Security Login API Document
	@ApiOperation("세션 기반 로그인")
	@PostMapping("/v1/auth/signin")
	public ApiResponse<UserDetailsInfoResponse> userSessionLogin(@RequestBody SessionLoginRequest request) {
		throw new IllegalStateException(
			"This method shouldn't be called. It's implemented by Spring Security filters.");
	}

	// Spring Security Login API Document
	@ApiOperation("세션 기반 로그아웃")
	@GetMapping("/v1/auth/signout")
	public ApiResponse<SessionLogoutResponse> userSessionLogout() {
		throw new IllegalStateException(
			"This method shouldn't be called. It's implemented by Spring Security filters.");
	}
}

package com.virnect.uaa.domain.auth.account.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.virnect.uaa.domain.auth.account.dto.request.SessionLoginRequest;
import com.virnect.uaa.domain.auth.account.dto.response.SessionLogoutResponse;
import com.virnect.uaa.domain.user.dto.response.UserDetailsInfoResponse;
import com.virnect.uaa.global.common.ApiResponse;

@RestController
@Api(value = "세션기반 로그인 로그아웃 관련 인증 컨트롤러")
@RequestMapping("/v1")
public class AccountSessionSignInController {

	// Spring Security Login API Document
	@ApiOperation("세션 기반 로그인")
	@PostMapping("/auth/signin")
	public ApiResponse<UserDetailsInfoResponse> userSessionLogin(@RequestBody SessionLoginRequest request) {
		throw new IllegalStateException(
			"This method shouldn't be called. It's implemented by Spring Security filters.");
	}

	// Spring Security Login API Document
	@ApiOperation("세션 기반 로그아웃")
	@GetMapping("/auth/signout")
	public ApiResponse<SessionLogoutResponse> userSessionLogout() {
		throw new IllegalStateException(
			"This method shouldn't be called. It's implemented by Spring Security filters.");
	}
}

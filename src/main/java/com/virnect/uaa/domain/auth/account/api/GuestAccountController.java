package com.virnect.uaa.domain.auth.account.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.uaa.domain.auth.account.application.GuestAccountService;
import com.virnect.uaa.domain.auth.account.dto.request.GuestAccountAllocateRequest;
import com.virnect.uaa.domain.auth.account.dto.response.GuestAccountInfoResponse;
import com.virnect.uaa.domain.auth.common.error.AuthenticationErrorCode;
import com.virnect.uaa.domain.auth.common.exception.UserAuthenticationServiceException;
import com.virnect.uaa.global.common.ApiResponse;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class GuestAccountController {
	private final GuestAccountService guestAccountService;

	@GetMapping("/guest")
	public ResponseEntity<ApiResponse<GuestAccountInfoResponse>> guestAccountAllocateRequestHandle(
		@ModelAttribute @Valid GuestAccountAllocateRequest guestAccountAllocateRequest,
		@RequestHeader("x-guest-user-agent") String guestUserAgent,
		@RequestHeader("x-guest-user-ip") String guestUserIp,
		BindingResult result
	) {
		if (result.hasErrors()) {
			throw new UserAuthenticationServiceException(AuthenticationErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		GuestAccountInfoResponse guestAccountInfoResponse = guestAccountService.getAllocatableGuestAccount(
			guestAccountAllocateRequest, guestUserAgent, guestUserIp
		);

		return ResponseEntity.ok(new ApiResponse<>(guestAccountInfoResponse));
	}
}

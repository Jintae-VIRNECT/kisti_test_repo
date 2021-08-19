package com.virnect.data.application.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.data.dto.rest.GuestAccountInfoResponse;
import com.virnect.data.dto.rest.UserInfoListOnlyResponse;
import com.virnect.data.dto.rest.UserInfoListResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.global.common.ApiResponse;

@FeignClient(name = "account-server", fallbackFactory = AccountRestFallbackFactory.class)
public interface AccountRestService {

	@GetMapping("/users")
	ApiResponse<UserInfoListResponse> getUserInfoList(
		@RequestParam(name = "search", required = false) String search,
		@RequestParam("paging") boolean paging);

	@GetMapping("/users")
	ApiResponse<UserInfoListResponse> getUserInfoList(@RequestParam("paging") boolean paging);

	@GetMapping("/users/{userId}")
	ApiResponse<UserInfoResponse> getUserInfoByUserId(@PathVariable("userId") String userId);

	@GetMapping("/users")
	ApiResponse<UserInfoListResponse> getUserInfo(
		@RequestParam(name = "paging") boolean paging
	);

	@GetMapping("/users/infoList")
	ApiResponse<UserInfoListOnlyResponse> getUserInfoListByUserUUIDArray(@RequestParam("uuid") String[] uuid);

	@GetMapping("/auth/guest")
	ApiResponse<GuestAccountInfoResponse> getGuestAccountInfo(
		@RequestParam("product") String product,
		@RequestParam("workspaceId") String workspaceId,
		@RequestHeader("x-guest-user-agent") String xGuestUserAgent,
		@RequestHeader("x-guest-user-ip") String xGuestUserIp
	);
}
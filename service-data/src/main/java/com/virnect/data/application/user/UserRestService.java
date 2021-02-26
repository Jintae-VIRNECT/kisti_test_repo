package com.virnect.data.application.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.data.dto.rest.UserInfoListOnlyResponse;
import com.virnect.data.dto.rest.UserInfoListResponse;
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.global.common.ApiResponse;

@FeignClient(name = "user-server", fallbackFactory = UserRestFallbackFactory.class)
public interface UserRestService {
	/**
	 * User Information List
	 * @param search
	 * @param paging
	 * @return
	 */
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
}
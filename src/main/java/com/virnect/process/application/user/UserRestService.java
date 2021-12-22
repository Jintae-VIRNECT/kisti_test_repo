package com.virnect.process.application.user;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.process.dto.rest.response.user.UserInfoListResponse;
import com.virnect.process.dto.rest.response.user.UserInfoResponse;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.config.FeignConfiguration;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-03
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: User Server Rest Client Service
 */

@FeignClient(name = "account-server", configuration = FeignConfiguration.class)
public interface UserRestService {

	@GetMapping("/users/{userId}")
	ApiResponse<UserInfoResponse> getUserInfoByUserUUID(@PathVariable("userId") String userUUID);

	@GetMapping("/users")
	ApiResponse<UserInfoListResponse> getUserInfoSearch(
		@RequestParam(value = "search", required = false) String search, @RequestParam(value = "paging") boolean paging
	);

	@PostMapping("/users/list")
	ApiResponse<UserInfoListResponse> getUserInfoSearchNickName(
		@RequestParam(name = "search", required = false) String search, @RequestBody List<String> workspaceUserIdList
	);
}

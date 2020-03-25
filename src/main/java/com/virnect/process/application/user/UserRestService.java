package com.virnect.process.application.user;

import com.virnect.process.dto.rest.response.user.UserInfoListResponse;
import com.virnect.process.dto.rest.response.user.UserInfoResponse;
import com.virnect.process.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-02-03
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: User Server Rest Client Service
 */

@FeignClient(name = "UserServerRestClient", url = "${user.serverUrl}")
public interface UserRestService {

    @GetMapping("/{userUUID}")
    ApiResponse<UserInfoResponse> getUserInfoByUserUUID(@PathVariable("userUUID") String userUUID);

    @GetMapping
    ApiResponse<UserInfoListResponse> getUserInfoSearch(@RequestParam(value = "search", required = false) String search, @RequestParam(value = "paging") boolean paging);
}

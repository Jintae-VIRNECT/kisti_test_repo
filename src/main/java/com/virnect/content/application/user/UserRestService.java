package com.virnect.content.application.user;

import com.virnect.content.dto.rest.UserInfoListResponse;
import com.virnect.content.dto.rest.UserInfoResponse;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.ResponseMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-02-03
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: User Server Rest Client Service
 */

@FeignClient(name = "user-server")
public interface UserRestService {

    @GetMapping("/{userUUID}")
    ApiResponse<UserInfoResponse> getUserInfoByUserUUID(@PathVariable("userUUID") String userUUID);

    @GetMapping
    ApiResponse<UserInfoListResponse> getUserInfoSearch(@RequestParam(value = "search", required = false) String search, @RequestParam(value = "paging") boolean paging);
}

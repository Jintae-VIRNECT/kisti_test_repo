package com.virnect.serviceserver.application.user;


import com.virnect.service.ApiResponse;
import com.virnect.service.dto.feign.UserInfoListResponse;
import com.virnect.service.dto.feign.UserInfoResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${feign.user-prefix}", url = "${feign.user-url}", fallbackFactory = UserRestFallbackFactory.class)
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
}

package com.virnect.api.feign;


import com.virnect.api.ApiResponse;
import com.virnect.api.dto.rest.UserInfoListResponse;
import com.virnect.api.dto.rest.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

//url = "http://192.168.6.3:8081",
@FeignClient(name = "user-server", fallbackFactory = UserRestFallbackFactory.class)
public interface UserRestService {

    //@ApiIgnore PageRequest pageRequest
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
    ApiResponse<UserInfoResponse> getUserInfoByUuid(@PathVariable("userId") String userId);
}

package com.virnect.serviceserver.feign;

import com.virnect.serviceserver.api.ApiResponse;
import com.virnect.serviceserver.dto.rest.UserInfoListResponse;
import com.virnect.serviceserver.dto.rest.UserInfoResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class UserRestFallbackFactory implements FallbackFactory<UserRestService> {

    @Override
    public UserRestService create(Throwable cause) {
        log.info(cause.getMessage(), cause);
        return new UserRestService() {
            @Override
            public ApiResponse<UserInfoListResponse> getUserInfoList(String search, boolean paging) {
                log.info("[USER WORKSPACE LIST API FALLBACK] => USER_ID: {}");
                UserInfoListResponse empty = new UserInfoListResponse();
                empty.setUserInfoList(new ArrayList<>());
                return new ApiResponse<UserInfoListResponse>(empty);
            }

            @Override
            public ApiResponse<UserInfoListResponse> getUserInfoList(boolean paging) {
                return null;
            }

            @Override
            public ApiResponse<UserInfoResponse> getUserInfoByUuid(String userId) {
                return null;
            }
        };

        /*return (search, paging) -> {
            log.info("[USER WORKSPACE LIST API FALLBACK] => USER_ID: {}");
            UserInfoListResponse empty = new UserInfoListResponse();
            empty.setUserInfoList(new ArrayList<>());
            return new ApiResponse<UserInfoListResponse>(empty);
        };*/
    }
}

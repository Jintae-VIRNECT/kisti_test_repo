package com.virnect.process.application.user;

import com.virnect.process.dto.rest.response.user.UserInfoListResponse;
import com.virnect.process.dto.rest.response.user.UserInfoResponse;
import com.virnect.process.global.common.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class UserFallbackService implements UserRestService {
    @Override
    public ApiResponse<UserInfoResponse> getUserInfoByUserUUID(String userUUID) {
        return null;
    }

    @Override
    public ApiResponse<UserInfoListResponse> getUserInfoSearch(String search, boolean paging) {
        return null;
    }
}

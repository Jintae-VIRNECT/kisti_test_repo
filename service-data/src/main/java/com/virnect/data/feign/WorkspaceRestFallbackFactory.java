package com.virnect.data.feign;


import com.virnect.data.ApiResponse;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.feign.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.feign.WorkspaceMemberInfoResponse;
import com.virnect.data.feign.service.WorkspaceRestService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
public class WorkspaceRestFallbackFactory implements FallbackFactory<WorkspaceRestService> {

    @Override
    public WorkspaceRestService create(Throwable cause) {
        return new WorkspaceRestService() {
            @Override
            public ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMemberInfoList(String workspaceId, String filter, int page, int size) {
                log.error("[USER WORKSPACE INFO LIST API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId, cause.getMessage());
                WorkspaceMemberInfoListResponse empty = new WorkspaceMemberInfoListResponse();
                empty.setMemberInfoList(new ArrayList<>());
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<WorkspaceMemberInfoResponse> getWorkspaceMemberInfo(String workspaceId, String userId) {
                log.error("[USER WORKSPACE INFO API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId, cause.getMessage());
                WorkspaceMemberInfoResponse empty = new WorkspaceMemberInfoResponse();
                return new ApiResponse<>(empty);
            }
        };
    }
}

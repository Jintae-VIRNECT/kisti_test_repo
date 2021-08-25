package com.virnect.data.application.workspace;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@Component
public class WorkspaceRestFallbackFactory implements FallbackFactory<WorkspaceRestService> {

    @Override
    public WorkspaceRestService create(Throwable cause) {
        return new
                WorkspaceRestService() {
            @Override
            public ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMembers(String workspaceId, String filter, String search, int page, int size) {
                log.error("[USER WORKSPACE INFO LIST API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId, cause.getMessage());
                WorkspaceMemberInfoListResponse empty = new WorkspaceMemberInfoListResponse();
                empty.setMemberInfoList(new ArrayList<>());
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMembers(
                String workspaceId, String filter, String search, int size
            ) {
                log.error("[USER WORKSPACE INFO LIST API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId, cause.getMessage());
                WorkspaceMemberInfoListResponse empty = new WorkspaceMemberInfoListResponse();
                empty.setMemberInfoList(new ArrayList<>());
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMembers(String workspaceId, String plan, int size) {
                log.error("[USER WORKSPACE INFO LIST API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId, cause.getMessage());
                WorkspaceMemberInfoListResponse empty = new WorkspaceMemberInfoListResponse();
                empty.setMemberInfoList(new ArrayList<>());
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMembersOnlyMember(
                String workspaceId, String filter, String plan, int size
            ) {
                log.error("[USER WORKSPACE(ONLY USER) INFO LIST API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId, cause.getMessage());
                WorkspaceMemberInfoListResponse empty = new WorkspaceMemberInfoListResponse();
                empty.setMemberInfoList(new ArrayList<>());
                return new ApiResponse<>(empty);
            }

                    @Override
            public ApiResponse<WorkspaceMemberInfoResponse> getWorkspaceMember(String workspaceId, String userId) {
                log.error("[USER WORKSPACE INFO API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId, cause.getMessage());
                WorkspaceMemberInfoResponse empty = new WorkspaceMemberInfoResponse();
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMembers(String workspaceId) {
                log.error("[USER WORKSPACE MEMBERS INFO API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId, cause.getMessage());
                WorkspaceMemberInfoListResponse empty = new WorkspaceMemberInfoListResponse();
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMembersExcludeUserIds(String workspaceId, String[] userIds
            ) {
                log.error("[USER WORKSPACE MEMBERS INFO API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId, cause.getMessage());
                WorkspaceMemberInfoListResponse empty = new WorkspaceMemberInfoListResponse();
                return new ApiResponse<>(empty);
            }
        };
    }
}

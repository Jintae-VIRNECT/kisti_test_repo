package com.virnect.data.service;

import com.virnect.data.ApiResponse;
import com.virnect.data.constraint.LicenseConstants;
import com.virnect.data.dto.feign.WorkspaceMemberInfoListResponse;
import com.virnect.data.feign.service.WorkspaceRestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly=true)
public class MemberService {
    private static final String TAG = MemberService.class.getSimpleName();

    private final WorkspaceRestService workspaceRestService;

    public ApiResponse<WorkspaceMemberInfoListResponse> getMembers(String workspaceId, String filter, int page, int size) {
        log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
        ApiResponse<WorkspaceMemberInfoListResponse> response = this.workspaceRestService.getWorkspaceMemberInfoList(workspaceId, filter, page, size);
        response.getData().getMemberInfoList().removeIf(memberInfoResponses ->
                Arrays.toString(memberInfoResponses.getLicenseProducts()).isEmpty()
                        || !Arrays.toString(memberInfoResponses.getLicenseProducts()).contains(LicenseConstants.PRODUCT_NAME));
        //log.info("getUsers: " + response.getData().getMemberInfoList());
        return new ApiResponse<>(response.getData());
    }
}

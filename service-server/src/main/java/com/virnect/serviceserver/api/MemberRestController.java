package com.virnect.serviceserver.api;


import com.virnect.data.ApiResponse;
import com.virnect.data.api.IMemberRestAPI;
import com.virnect.data.dto.feign.WorkspaceMemberInfoListResponse;
import com.virnect.data.dto.response.MemberInfoListResponse;
import com.virnect.data.service.MemberService;
import com.virnect.serviceserver.data.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberRestController implements IMemberRestAPI {
    private static final String TAG = MemberRestController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/member";

    private final DataRepository dataRepository;

    @Override
    public ResponseEntity<ApiResponse<WorkspaceMemberInfoListResponse>> getMembers(String workspaceId, String filter, int page, int size) {
        //log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
        log.info("REST API: GET {}/{}",
                REST_PATH,
                workspaceId != null ? workspaceId: "{}");
        //increase page number + 1, cause page index starts 0
        ApiResponse<WorkspaceMemberInfoListResponse> apiResponse = this.dataRepository.loadMemberList(workspaceId, filter, page + 1, size);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembers(String workspaceId, String userId, String filter, int page, int size) {
        log.info("REST API: GET {}/{}/{}",
                REST_PATH,
                workspaceId != null ? workspaceId: "{}",
                userId != null ? userId: "{}");
        //increase page number + 1, cause page index starts 0
        ApiResponse<MemberInfoListResponse> apiResponse = this.dataRepository.loadMemberList(workspaceId, userId, filter, page + 1, size);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembers(String workspaceId, String sessionId, String userId, String filter, int page, int size) {
        log.info("REST API: GET {}/{}/{}/{}",
                REST_PATH,
                workspaceId != null ? workspaceId: "{}",
                sessionId != null ? sessionId: "{}",
                userId != null ? userId: "{}");
        //increase page number + 1, cause page index starts 0
        ApiResponse<MemberInfoListResponse> apiResponse = this.dataRepository.loadMemberList(workspaceId, sessionId, userId, filter, page + 1, size);
        return ResponseEntity.ok(apiResponse);
    }
}

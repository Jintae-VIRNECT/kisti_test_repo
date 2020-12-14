package com.virnect.serviceserver.api;



import com.virnect.data.service.MemberService;
import com.virnect.service.ApiResponse;
import com.virnect.service.api.IMemberRestAPI;
import com.virnect.service.dto.feign.WorkspaceMemberInfoListResponse;
import com.virnect.service.dto.service.response.MemberInfoListResponse;
import com.virnect.service.dto.service.response.MemberSecessionResponse;
import com.virnect.serviceserver.data.DataRepository;
import com.virnect.serviceserver.data.MemberDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberRestController implements IMemberRestAPI {
    private static final String TAG = MemberRestController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/member";

    private MemberDataRepository memberDataRepository;

    @Qualifier(value = "memberDataRepository")
    @Autowired
    public void setMemberDataRepository(MemberDataRepository memberDataRepository) {
        this.memberDataRepository = memberDataRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<WorkspaceMemberInfoListResponse>> getMembers(String workspaceId, String filter, String search, int page, int size) {
        //log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
        log.info("REST API: GET {}/{}",
                REST_PATH,
                workspaceId != null ? workspaceId: "{}");
        //increase page number + 1, cause page index starts 0
        ApiResponse<WorkspaceMemberInfoListResponse> apiResponse = this.memberDataRepository.loadMemberList(workspaceId, filter, search, page + 1, size);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembers(String workspaceId, String userId, String filter, String search, int page, int size) {
        log.info("REST API: GET {}/{}/{}",
                REST_PATH,
                workspaceId != null ? workspaceId: "{}",
                userId != null ? userId: "{}");
        //increase page number + 1, cause page index starts 0
        ApiResponse<MemberInfoListResponse> apiResponse = this.memberDataRepository.loadMemberList(
                workspaceId,
                userId,
                filter,
                search,
                page + 1,
                size);
        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembers(String workspaceId, String sessionId, String userId, String filter, String search, int page, int size) {
        log.info("REST API: GET {}/{}/{}/{}",
                REST_PATH,
                workspaceId != null ? workspaceId: "{}",
                sessionId != null ? sessionId: "{}",
                userId != null ? userId: "{}");
        //increase page number + 1, cause page index starts 0
        ApiResponse<MemberInfoListResponse> apiResponse = this.memberDataRepository.loadMemberList(
                workspaceId,
                sessionId,
                userId,
                filter,
                search,
                page + 1,
                size);
        return ResponseEntity.ok(apiResponse);
    }



    @Override
    public ResponseEntity<ApiResponse<MemberSecessionResponse>> deleteMembersBySecession(String userId) {
        log.info("REST API: GET {}/{}",
                REST_PATH,
                userId != null ? userId: "{}");

        ApiResponse<MemberSecessionResponse> apiResponse = this.memberDataRepository.deleteMember(userId);
        return ResponseEntity.ok(apiResponse);
    }
}

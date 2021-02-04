package com.virnect.serviceserver.api;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.serviceserver.application.MemberService;
import com.virnect.serviceserver.dto.response.member.MemberInfoListResponse;
import com.virnect.serviceserver.dto.response.member.MemberSecessionResponse;
import com.virnect.serviceserver.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.serviceserver.global.common.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberRestController {
    //private static final String TAG = MemberRestController.class.getSimpleName();
    //private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/member";

    //private MemberDataRepository memberDataRepository;

    private final MemberService memberService;

	/*@Qualifier(value = "memberDataRepository")
	@Autowired
	public void setMemberDataRepository(MemberDataRepository memberDataRepository) {
		this.memberDataRepository = memberDataRepository;
	}*/

    @ApiOperation(value = "Lookup Workspace Member Information List", notes = "워크스페이스 멤버 리스트를 조회하는 API 입니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", dataType = "Integer", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
        @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @GetMapping(value = "members/{workspaceId}")
    public ResponseEntity<ApiResponse<WorkspaceMemberInfoListResponse>> getMembers(
        @PathVariable(name = "workspaceId") String workspaceId,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    ) {
        //log.info("WORKSPACE MEMBER SEARCH BY WORKSPACE ID => [{}]", workspaceId);
        log.info(
            "REST API: GET {}/{}",
            REST_PATH,
            workspaceId != null ? workspaceId : "{}"
        );

        //increase page number + 1, cause page index starts 0
		/*ApiResponse<WorkspaceMemberInfoListResponse> apiResponse = this.memberDataRepository.loadMemberList(
			workspaceId, filter, search, page + 1, size);*/

        WorkspaceMemberInfoListResponse responseData = memberService.getMembers(
            workspaceId, filter, search, page + 1, size
        );

        return ResponseEntity.ok(new ApiResponse<>(responseData));
    }

    @ApiOperation(value = "Lookup Remote Member Information List", notes = "본인을 제외한 워크스페이스 리모트 멤버 리스트를 조회하는 API 입니다.(동일한 사용자 정보 제외)")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", dataType = "Integer", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
        @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @GetMapping(value = "members/{workspaceId}/{userId}")
    public ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembers(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "userId") String userId,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    ) {
        log.info(
            "REST API: GET {}/{}/{}",
            REST_PATH,
            workspaceId != null ? workspaceId : "{}",
            userId != null ? userId : "{}"
        );
        //increase page number + 1, cause page index starts 0
		/*ApiResponse<MemberInfoListResponse> apiResponse = this.memberDataRepository.loadMemberList(
			workspaceId,
			userId,
			filter,
			search,
			page + 1,
			size
		);*/

        MemberInfoListResponse responseData = memberService.getMembersExceptForMe(
            workspaceId,
            userId,
            filter,
            search,
            page + 1,
            size
        );

        return ResponseEntity.ok(new ApiResponse(responseData));
    }

    @ApiOperation(value = "Lookup Invitable Remote Member Information List", notes = "초대 가능한 워크스페이스 리모트 멤버 리스트를 조회하는 API 입니다.(원격협업 참가자 제외)")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "filter", value = "사용자 필터(MASTER, MANAGER, MEMBER)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", dataType = "Integer", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "size", value = "Paging Data Size", dataType = "number", paramType = "query", defaultValue = "20"),
        @ApiImplicitParam(name = "sort", value = "Sort Option", paramType = "query", defaultValue = "role, desc"),
    })
    @GetMapping(value = "members/{workspaceId}/{sessionId}/{userId}")
    public ResponseEntity<ApiResponse<MemberInfoListResponse>> getMembers(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "sessionId") String sessionId,
        @PathVariable(name = "userId") String userId,
        @RequestParam(value = "filter", required = false) String filter,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size") int size
    ) {
        log.info(
            "REST API: GET {}/{}/{}/{}",
            REST_PATH,
            workspaceId != null ? workspaceId : "{}",
            sessionId != null ? sessionId : "{}",
            userId != null ? userId : "{}"
        );
        //increase page number + 1, cause page index starts 0
		/*ApiResponse<MemberInfoListResponse> apiResponse = this.memberDataRepository.loadMemberList(
			workspaceId,
			sessionId,
			userId,
			filter,
			search,
			page + 1,
			size
		);
		return ResponseEntity.ok(apiResponse);*/
        MemberInfoListResponse responseData = memberService.getMembersInvitePossible(workspaceId,
            sessionId,
            userId,
            filter,
            search,
            page + 1,
            size
        );
        return ResponseEntity.ok(new ApiResponse(responseData));
    }

    @ApiOperation(value = "Member Account Withdrawal", notes = "")
    @DeleteMapping(value = "members/{userId}")
    public ResponseEntity<ApiResponse<MemberSecessionResponse>> deleteMembersBySession(
        @PathVariable(name = "userId") String userId
    ) {
        log.info(
            "REST API: GET {}/{}",
            REST_PATH,
            userId != null ? userId : "{}"
        );

		/*ApiResponse<MemberSecessionResponse> apiResponse = this.memberDataRepository.deleteMember(userId);
		return ResponseEntity.ok(apiResponse);*/

        MemberSecessionResponse responseData = memberService.deleteMembersBySession(
            userId
        );
        return ResponseEntity.ok(new ApiResponse(responseData));

    }
}

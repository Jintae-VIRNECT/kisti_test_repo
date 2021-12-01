package com.virnect.process.api;

import java.util.Objects;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.process.application.IssueService;
import com.virnect.process.dto.request.TroubleMemoUploadRequest;
import com.virnect.process.dto.response.IssueInfoResponse;
import com.virnect.process.dto.response.IssuesResponse;
import com.virnect.process.dto.response.TroubleMemoUploadResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.PageRequest;
import com.virnect.process.global.error.ErrorCode;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-08-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class IssueController {
	private final IssueService issueService;

	/**
	 * 이슈 목록 조회 (이전 /tasks/issues?inout=IN)
	 *
	 * @param workspaceUUID
	 * @param search
	 * @param myUUID
	 * @param pageable
	 * @return
	 */
	@ApiOperation(value = "이슈 목록 조회", notes = "searchType 없앰. 정렬 컬럼명은 updatedDate")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "search", value = "검색어(사용자 이메일, 닉네임, 작업명, 하위 작업명, 이슈 내용, 단계명)", dataType = "string", paramType = "query", defaultValue = ""),
		@ApiImplicitParam(name = "myUUID", value = "사용자 식별자 (내 이슈용)", dataType = "string", paramType = "query", defaultValue = ""),
		@ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc"),
		@ApiImplicitParam(name = "stepId", value = "스텝ID", dataType = "string", paramType = "query")
	})
	@GetMapping("/issues")
	public ResponseEntity<ApiResponse<IssuesResponse>> getIssues(
		@RequestParam(value = "workspaceUUID", required = false, defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8") String workspaceUUID
		, @RequestParam(value = "search", required = false) String search
		, @RequestParam(value = "myUUID", required = false) String myUUID
		, @RequestParam(value = "stepId", required = false) Long stepId
		, @ApiIgnore PageRequest pageable
	) {
		ApiResponse<IssuesResponse> issuesResponseApiResponse = this.issueService.getIssuesIn(
			myUUID, workspaceUUID, search, stepId, pageable.of());
		// 검색어가 없다면 검색분류도 없는 것으로 처리.
		//        if (search == null || search.isEmpty()) {
		//            searchType = SearchType.NONE;
		//        }
		//        switch (searchType) {
		//            // 사용자명 검색시
		//            case USER_NAME:
		//                // 작업내에서의 사용자명 검색시
		//                issuesResponseApiResponse = this.taskService.getIssuesInSearchUserName(null, workspaceUUID, search, pageable.of());
		//                break;
		//            // 검색어가 비어 있을 경우
		//            case NONE:
		//                issuesResponseApiResponse = this.taskService.getIssuesIn(null, workspaceUUID, pageable.of());
		//                break;
		//            // 작업, 하위작업, 단계으로 검색시
		//            default:
		//                // 단계 내에서 작업, 하위작업, 단계을 검색
		//                switch (searchType) {
		//                    // 작업으로 검색시
		//                    case TASK_NAME:
		//                        issuesResponseApiResponse = this.taskService.getIssuesInSearchProcessTitle(workspaceUUID, search, pageable.of());
		//                        break;
		//                    // 하위 작업으로 검색시
		//                    case SUBTASK_NAME:
		//                        issuesResponseApiResponse = this.taskService.getIssuesInSearchSubProcessTitle(workspaceUUID, search, pageable.of());
		//                        break;
		//                    // 단계로 검색시
		//                    case STEP_NAME:
		//                        issuesResponseApiResponse = this.taskService.getIssuesInSearchJobTitle(workspaceUUID, search, pageable.of());
		//                        break;
		//                    default:
		//                        throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		//                }
		//        }

		return ResponseEntity.ok(issuesResponseApiResponse);
	}

	/**
	 * 이슈상세조회
	 *
	 * @param issueId
	 * @return
	 */
	@ApiOperation(value = "이슈상세조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "issueId", value = "issue 식별자", dataType = "string", paramType = "path", required = true, example = "1")
	})
	@GetMapping("/issue/{issueId}")
	public ResponseEntity<ApiResponse<IssueInfoResponse>> getIssueInfo(@PathVariable("issueId") Long issueId) {
		if (Objects.isNull(issueId)) {
			log.info("[issueId] => [{}]", issueId);
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<IssueInfoResponse> issueInfoResponseApiResponse = this.issueService.getIssueInfo(issueId);
		return ResponseEntity.ok(issueInfoResponseApiResponse);
	}

	/**
	 * 트러블 메모 목록 조회 (이전 /tasks/issues?inout=OUT)
	 *
	 * @param workspaceUUID
	 * @param search
	 * @param myUUID
	 * @param pageable
	 * @return
	 */
	@ApiOperation(value = "트러블 메모 목록 조회", notes = "searchType을 최우선 확인함. searchType이 NONE인 경우 검색어는 무시됨.\n정렬 컬럼명은 updatedDate.\n 특정 작업 등에 얽매이지 않는다. 현재는 JOB_ID가 null인 것을 리턴.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query", required = true),
		@ApiImplicitParam(name = "search", value = "검색어 - searchType이 NONE인 경우 검색어는 무시됨.", dataType = "string", paramType = "query", defaultValue = ""),
		@ApiImplicitParam(name = "myUUID", value = "사용자 식별자", dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "page", value = "조회할 페이지 번호(1부터)", dataType = "number", paramType = "query", defaultValue = "1"),
		@ApiImplicitParam(name = "size", value = "페이지당 목록 개수", dataType = "number", paramType = "query", defaultValue = "10"),
		@ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(요청파라미터 명, 정렬조건)", dataType = "String", paramType = "query", defaultValue = "updatedDate,desc")
	})
	@GetMapping("/troubleMemos")
	public ResponseEntity<ApiResponse<IssuesResponse>> getTroubleMemo(
		@RequestParam(value = "workspaceUUID") String workspaceUUID
		, @RequestParam(value = "search", required = false) String search
		, @RequestParam(value = "myUUID", required = false) String myUUID
		, @ApiIgnore PageRequest pageable
	) {
		if (StringUtils.isEmpty(workspaceUUID)) {
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<IssuesResponse> issuesResponseApiResponse = this.issueService.getTroubleMemos(
			myUUID, workspaceUUID, search, pageable.of());

		return ResponseEntity.ok(issuesResponseApiResponse);
	}

	/**
	 * 트러블 메모 상세 조회
	 *
	 * @param troubleMemoId
	 * @return
	 */
	@ApiOperation(value = "트러블 메모 상세 조회")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "troubleMemoId", value = "troubleMemo 식별자", dataType = "string", paramType = "path", required = true, example = "1")
	})
	@GetMapping("/troubleMemo/{troubleMemoId}")
	public ResponseEntity<ApiResponse<IssueInfoResponse>> getTroubleMemoInfo(
		@PathVariable("troubleMemoId") Long troubleMemoId
	) {
		if (Objects.isNull(troubleMemoId)) {
			log.info("[troubleMemoId] => [{}]", troubleMemoId);
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<IssueInfoResponse> issueInfoResponseApiResponse = this.issueService.getIssueInfo(troubleMemoId);
		return ResponseEntity.ok(issueInfoResponseApiResponse);
	}

	@ApiOperation(value = "트러블메모 업로드")
	@PostMapping("/troubleMemo/upload")
	public ResponseEntity<ApiResponse<TroubleMemoUploadResponse>> setTroubleMemo(
		@RequestBody @Valid TroubleMemoUploadRequest troubleMemoUploadRequest, BindingResult result
	) {
		log.info("[TROUBLE MEMO UPLOAD] Request Body >> {}", troubleMemoUploadRequest.toString());
		if (result.hasErrors()) {
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<TroubleMemoUploadResponse> responseMessage = this.issueService.uploadTroubleMemo(
			troubleMemoUploadRequest);

		return ResponseEntity.ok(responseMessage);
	}
}

package com.virnect.serviceserver.serviceremote.api;

import javax.validation.Valid;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.request.member.GroupRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.group.FavoriteGroupListResponse;
import com.virnect.data.dto.response.group.FavoriteGroupResponse;
import com.virnect.data.dto.response.group.RemoteGroupListResponse;
import com.virnect.data.dto.response.group.RemoteGroupResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.application.GroupService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class GroupRestController {

	private static final String TAG = GroupRestController.class.getSimpleName();
	private static final String REST_PATH = "/remote/member";
	private final GroupService groupService;

	@ApiOperation(value = "[MASTER] Create member group", notes = "멤버 그룹을 생성합니다")
	@PostMapping(value = "members/group/{workspaceId}/{userId}")
	public ResponseEntity<ApiResponse<RemoteGroupResponse>> createRemoteGroup(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@RequestBody @Valid GroupRequest groupRequest
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId + "::"
				+ "groupRequest:" + groupRequest.toString(),
			"createRemoteGroup"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RemoteGroupResponse> responseData = groupService.createRemoteGroup(workspaceId, userId, groupRequest);
		return ResponseEntity.ok(responseData);
	}


	@ApiOperation(value = "[MASTER] Get member groups", notes = "멤버 그룹을 조회합니다")
	@GetMapping(value = "members/group/{workspaceId}")
	public ResponseEntity<ApiResponse<RemoteGroupListResponse>> getRemoteGroups(
		@PathVariable(name = "workspaceId") String workspaceId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(value = "includeOneself", required = false) boolean includeOneself
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId,
			"getRemoteGroups"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RemoteGroupListResponse> responseData = groupService.getRemoteGroups(workspaceId, userId, includeOneself);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "[MASTER] Get selected member group detail information", notes = "멤버그룹을 상세조회합니다")
	@GetMapping(value = "members/group/{workspaceId}/{groupId}")
	public ResponseEntity<ApiResponse<RemoteGroupResponse>> getRemoteGroupDetail(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "groupId") String groupId,
		@RequestParam(value = "userId") String userId,
		@RequestParam(value = "filter", required = false) String filter,
		@RequestParam(value = "search", required = false) String search,
		@RequestParam(value = "includeOneself", required = false) boolean includeOneself,
		@RequestParam(value = "accessTypeFilter", required = false) boolean accessTypeFilter
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ groupId,
			"getRemoteGroupDetailInfo"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(groupId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RemoteGroupResponse> responseData = groupService.getRemoteGroupDetail(
			workspaceId,
			groupId,
			userId,
			filter,
			search,
			includeOneself,
			accessTypeFilter
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "[MASTER] Update member group", notes = "멤버 그룹정보를 수정합니다")
	@PutMapping(value = "members/group/{workspaceId}/{userId}/{groupId}")
	public ResponseEntity<ApiResponse<RemoteGroupResponse>> updateRemoteGroup(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@PathVariable(name = "groupId") String groupId,
		@RequestBody @Valid GroupRequest groupRequest
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: PUT "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId + "::"
				+ "groupRequest:" + groupRequest.toString(),
			"updateRemoteGroup"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(userId) || Strings.isBlank(groupId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<RemoteGroupResponse> responseData = groupService.updateRemoteGroup(
			workspaceId,
			userId,
			groupId,
			groupRequest
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "[MASTER] Delete member group", notes = "멤버 그룹을 삭제합니다")
	@DeleteMapping(value = "members/group/{workspaceId}/{userId}/{groupId}")
	public ResponseEntity<ApiResponse<ResultResponse>> deleteRemoteGroup(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@PathVariable(name = "groupId") String groupId
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: DELETE "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId + "::"
				+ "groupId:" + groupId,
			"deleteRemoteGroup"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(userId) || Strings.isBlank(groupId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ResultResponse> responseData = groupService.deleteRemoteGroup(workspaceId, userId, groupId);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "[NORMAL USER] Create member favorite group", notes = "개인별 멤버 그룹을 생성합니다")
	@PostMapping(value = "members/favorite-group/{workspaceId}/{userId}")
	public ResponseEntity<ApiResponse<FavoriteGroupResponse>> createFavoriteGroup(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@RequestBody @Valid GroupRequest groupRequest
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: POST "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId + "::"
				+ "groupRequest:" + groupRequest.toString(),
			"createFavoriteGroup"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<FavoriteGroupResponse> responseData = groupService.createFavoriteGroup(workspaceId, userId, groupRequest);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "[NORMAL USER] Get member favorite groups", notes = "개인별 멤버 그룹을 조회합니다")
	@GetMapping(value = "members/favorite-group/{workspaceId}")
	public ResponseEntity<ApiResponse<FavoriteGroupListResponse>> getFavoriteGroups(
		@PathVariable(name = "workspaceId") String workspaceId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(value = "includeOneself", required = false) boolean includeOneself
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId,
			"getFavoriteGroups"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<FavoriteGroupListResponse> responseData = groupService.getFavoriteGroups(workspaceId, userId, includeOneself);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "[NORMAL USER] Get selected member favorite group detail information", notes = "개인별 멤버그룹을 상세조회합니다")
	@GetMapping(value = "members/favorite-group/{workspaceId}/{groupId}")
	public ResponseEntity<ApiResponse<FavoriteGroupResponse>> getFavoriteGroupDetail(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "groupId") String groupId,
		@RequestParam(value = "userId") String userId,
		@RequestParam(value = "filter", required = false) String filter,
		@RequestParam(value = "search", required = false) String search,
		@RequestParam(value = "includeOneself", required = false) boolean includeOneself,
		@RequestParam(value = "accessTypeFilter", required = false) boolean accessTypeFilter
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ groupId,
			"getFavoriteGroupDetailInfo"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(groupId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<FavoriteGroupResponse> responseData = groupService.getFavoriteGroupDetail(
			workspaceId,
			groupId,
			userId,
			filter,
			search,
			includeOneself,
			accessTypeFilter
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "[NORMAL USER] Update member favorite group", notes = "개인별 멤버 그룹정보를 수정합니다")
	@PutMapping(value = "members/favorite-group/{workspaceId}/{userId}/{groupId}")
	public ResponseEntity<ApiResponse<FavoriteGroupResponse>> updateFavoriteGroup(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@PathVariable(name = "groupId") String groupId,
		@RequestBody @Valid GroupRequest groupRequest
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: PUT "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId + "::"
				+ "groupRequest:" + groupRequest.toString(),
			"updateFavoriteGroup"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(groupId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<FavoriteGroupResponse> responseData = groupService.updateFavoriteGroup(
			workspaceId,
			userId,
			groupId,
			groupRequest
		);
		return ResponseEntity.ok(responseData);
	}

	@ApiOperation(value = "[NORMAL USER] Delete member favorite group", notes = "개인별 멤버 그룹을 삭제합니다")
	@DeleteMapping(value = "members/favorite-group/{workspaceId}/{userId}/{groupId}")
	public ResponseEntity<ApiResponse<ResultResponse>> deleteFavoriteGroup(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@PathVariable(name = "groupId") String groupId
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: DELETE "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId + "::"
				+ "groupId:" + groupId,
			"deleteFavoriteGroup"
		);
		if (Strings.isBlank(workspaceId) || Strings.isBlank(groupId) || Strings.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<ResultResponse> responseData = groupService.deleteFavoriteGroup(workspaceId, userId, groupId);
		return ResponseEntity.ok(responseData);
	}
}

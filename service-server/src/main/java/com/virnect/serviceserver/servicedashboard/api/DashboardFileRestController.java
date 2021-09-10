package com.virnect.serviceserver.servicedashboard.api;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.rest.ListRecordingFilesResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.servicedashboard.application.DashboardFileService;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDeleteResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDetailInfoListResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileInfoListResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FilePreSignedResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote/dashboard/file")
public class DashboardFileRestController {

	private static final String TAG = DashboardFileRestController.class.getSimpleName();
	private static final String REST_PATH = "/remote/dashboard/file";

	private final DashboardFileService fileService;

	@ApiOperation(value = "[협업]에서 업로드된 첨부파일 목록을 반환하는 API")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "47cfc8b44fad3d0bbdc96d5307c6370a", required = true),
		@ApiImplicitParam(name = "sessionId", value = "session Id", defaultValue = "ses_KpgdMljkcn", required = true),
		@ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "4d127135f699616fb88e6bc4fa6d784a", paramType = "query", required = true),
		@ApiImplicitParam(name = "deleted", value = "삭제 파일 필터 옵션 (YES, NO)", dataType = "boolean", allowEmptyValue = true, defaultValue = "false")
	})
	@GetMapping(value = "{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<FileInfoListResponse>> getAttachedFileListRequestHandler(
		@PathVariable(value = "workspaceId") String workspaceId,
		@PathVariable(name = "sessionId") String sessionId,
		@RequestParam(value = "userId") String userId,
		@RequestParam(value = "deleted") boolean deleted
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId,
			"getAttachedFileListRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(fileService.getAttachedFileList(workspaceId, sessionId, deleted))
		);
	}

	@ApiOperation(value = "[REMOTE WEB]에서 업로드된 로컬 녹화 파일 목록을 반환하는 API")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "47cfc8b44fad3d0bbdc96d5307c6370a", required = true),
		@ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "4d127135f699616fb88e6bc4fa6d784a", required = true),
		@ApiImplicitParam(name = "sessionId", value = "검색 결과 페이지네이션 여부", defaultValue = "ses_WYJJpr4xBH", required = true),
		@ApiImplicitParam(name = "deleted", value = "삭제 파일 필터 옵션 (YES, NO)", dataType = "boolean", allowEmptyValue = true, defaultValue = "false")
	})
	@GetMapping(value = "record-file/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<FileDetailInfoListResponse>> getLocalRecordFileListRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "sessionId") String sessionId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "deleted") boolean deleted
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId,
			"getLocalRecordFileListRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(fileService.getLocalRecordFileList(workspaceId, sessionId, deleted))
		);
	}

	@ApiOperation(value = "서버녹화 파일 목록을 반환하는 API")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "47cfc8b44fad3d0bbdc96d5307c6370a", required = true),
		@ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "4d127135f699616fb88e6bc4fa6d784a", required = true),
		@ApiImplicitParam(name = "sessionId", value = "Session ID", paramType = "query", defaultValue = "session id"),
		@ApiImplicitParam(name = "order", value = "정렬", paramType = "query", defaultValue = "createdAt.asc")
	})
	@GetMapping(value = "record/{workspaceId}/{userId}")
	ResponseEntity<ApiResponse<ListRecordingFilesResponse>> getServerRecordFileListRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@RequestParam(name = "sessionId") String sessionId,
		@RequestParam(name = "order") String order
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId,
			"getServerRecordFileListRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(fileService.getServerRecordFileList(workspaceId, sessionId, userId, order))
		);
	}

	@ApiOperation(value = "서버 녹화 파일 삭제")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "40f9bbee9d85dca7a34a0dd205aae718", required = true),
		@ApiImplicitParam(name = "userId", value = "유저 uuid", paramType = "query", defaultValue = "410df50ca6e32db0b6acba09bcb457ff", required = true),
		@ApiImplicitParam(name = "id", value = "녹화 ID", paramType = "query", defaultValue = "410df50ca6e32db0b6acba09bcb457ff", required = true)
	})
	@DeleteMapping(value = "record/{workspaceId}")
	ResponseEntity<ApiResponse<Object>> deleteServerRecordFileRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "id") String id
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: DELETE "
				+ REST_PATH + "/"
				+ workspaceId,
			"deleteServerRecordFileRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(fileService.deleteServerRecordFileUrl(workspaceId, userId, id))
		);
	}

	@ApiOperation(value = "[Remote web]에서 업로드된 로컬 녹화 파일 삭제")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "workspaceId", required = true),
		@ApiImplicitParam(name = "sessionId", value = "session ID", required = true),
		@ApiImplicitParam(name = "userId", value = "user ID", paramType = "query", required = true),
		@ApiImplicitParam(name = "objectName", value = "object name", paramType = "query", required = true)
	})
	@DeleteMapping(value = "record-file/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<FileDeleteResponse>> deleteLocalRecordFileRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "sessionId") String sessionId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "objectName") String objectName
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: DELETE "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId,
			"deleteLocalRecordFileRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId) || StringUtils.isBlank(objectName)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(fileService.deleteLocalRecordFileUrl(workspaceId, sessionId, objectName))
		);
	}

	@ApiOperation(value = "협업 중 첨부된 파일 삭제")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "workspaceId", required = true),
		@ApiImplicitParam(name = "sessionId", value = "session ID", required = true),
		@ApiImplicitParam(name = "objectName", value = "object name", paramType = "query", required = true),
		@ApiImplicitParam(name = "userId", value = "user ID", paramType = "query", required = true),
	})
	@DeleteMapping(value = "{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<FileDeleteResponse>> deleteAttachedFileRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "sessionId") String sessionId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "objectName") String objectName
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: DELETE "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId,
			"deleteAttachedFileRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId) || StringUtils.isBlank(objectName)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(fileService.deleteAttachedFile(workspaceId, sessionId, objectName))
		);
	}

	@ApiOperation(value = "파일 다운로드 URL을 받습니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "path", defaultValue = "47cfc8b44fad3d0bbdc96d5307c6370a", required = true),
		@ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "path", defaultValue = "ses_WYJJpr4xBH", required = true),
		@ApiImplicitParam(name = "userId", value = "다운로드 사용자 고유 식별자", dataType = "string", paramType = "query", defaultValue = "40247ff4cbe04a1e8ae3203298996f4c", required = true),
		@ApiImplicitParam(name = "objectName", value = "다운로드 파일 고유 이름", dataType = "string", paramType = "query", defaultValue = "2020-11-06_LMcnWJllnCkqkPffKJkH", required = true)
	})
	@GetMapping(value = "url/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<FilePreSignedResponse>> getFileDownloadUrlRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "sessionId") String sessionId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "objectName") String objectName
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId,
			"getFileDownloadUrlRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId) || StringUtils.isBlank(objectName)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(fileService.getAttachedFileUrl(workspaceId, sessionId, objectName))
		);
	}

	@ApiOperation(value = "서버 녹화 파일 다운로드 URL을 받습니다")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", defaultValue = "47cfc8b44fad3d0bbdc96d5307c6370a", dataType = "string", paramType = "path", required = true),
		@ApiImplicitParam(name = "userId", defaultValue = "40247ff4cbe04a1e8ae3203298996f4c", dataType = "string", paramType = "path", required = true),
		@ApiImplicitParam(name = "id", defaultValue = "1f419621-0dfc-41c5-94ef-9892d4878a5d", dataType = "string", paramType = "path", required = true)
	})
	@GetMapping(value = "record/workspaces/{workspaceId}/users/{userId}/files/{id}/url")
	ResponseEntity<ApiResponse<String>> getServerRecordFileDownloadUrlRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@PathVariable(name = "id") String id
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ userId + "/"
				+ id,
			"getServerRecordFileDownloadUrlRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(userId)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(fileService.getServerRecordFileUrl(workspaceId, userId, id))
		);
	}

	@ApiOperation(value = "로컬 녹화 파일 다운로드 URL을 받습니다.")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "path", defaultValue = "47cfc8b44fad3d0bbdc96d5307c6370a", required = true),
		@ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "path", defaultValue = "ses_J7MhL0Sp5L", required = true),
		@ApiImplicitParam(name = "userId", value = "다운로드 사용자 고유 식별자", dataType = "string", paramType = "query", defaultValue = "4d127135f699616fb88e6bc4fa6d784a", required = true),
		@ApiImplicitParam(name = "objectName", value = "다운로드 파일 고유 이름", dataType = "string", paramType = "query", defaultValue = "2020-11-11_xJUXNgKBgZNEgdHFCter", required = true)
	})
	@GetMapping(value = "record-file/url/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<FilePreSignedResponse>> getLocalRecordFileDownloadUrlRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "sessionId") String sessionId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "objectName") String objectName
	) {
		LogMessage.formedInfo(
			TAG,
			"REST API: GET "
				+ REST_PATH + "/"
				+ workspaceId + "/"
				+ sessionId,
			"getLocalRecordFileDownloadUrlRequestHandler"
		);
		if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId) || StringUtils.isBlank(objectName)) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		return ResponseEntity.ok(
			new ApiResponse<>(fileService.getLocalRecordFileUrl(workspaceId, sessionId, objectName))
		);
	}
}

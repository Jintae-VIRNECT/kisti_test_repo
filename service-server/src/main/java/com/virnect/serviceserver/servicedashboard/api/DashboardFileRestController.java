package com.virnect.serviceserver.servicedashboard.api;

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

import com.virnect.serviceserver.servicedashboard.application.DashboardFileService;
import com.virnect.serviceserver.servicedashboard.dto.request.FileDataRequest;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDeleteResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileDetailInfoListResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FileInfoListResponse;
import com.virnect.serviceserver.servicedashboard.dto.response.FilePreSignedResponse;
import com.virnect.data.dto.rest.RecordServerFileInfoListResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote/dashboard/file")
public class DashboardFileRestController {

	private final DashboardFileService fileService;
	/*
	 *	2. 협업에서 업로드된 첨부파일 목록을 반환하는 API
	 *	3. 로컬 녹화 파일 목록을 반환하는 API
	 *	6. 서버녹화 파일목록을 반환하는 API
	 *	7. 서버녹화 파일 삭제
	 *	8. 로컬녹화 파일 삭제
	 *	9. 첨부파일 삭제
	 *  11.첨부파일 다운로드 링크를 반환하는 API
	 *  12.서버 녹화 파일 다운로드 링크를 반환하는 API
	 *  13.로컬 녹화 파일 다운로드 링크를 반환하는 API
	 */

	/**
	 * 2. 협업에서 업로드된 첨부파일 목록을 반환하는 API
	 */
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
		if (workspaceId.isEmpty() || userId.isEmpty() || sessionId.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		FileDataRequest option = FileDataRequest.builder()
				.workspaceId(workspaceId)
				.sessionId(sessionId)
				.userId(userId)
				.deleted(deleted)
				.build();

		FileInfoListResponse responseData = fileService.getAttachedFileList(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 3. 로컬 녹화 파일 목록을 반환하는 API
	 */
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
		if (workspaceId.isEmpty() || userId.isEmpty() || sessionId.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		FileDataRequest option = FileDataRequest.builder()
				.workspaceId(workspaceId)
				.sessionId(sessionId)
				.userId(userId)
				.deleted(deleted)
				.build();

		FileDetailInfoListResponse responseData = fileService.getLocalRecordFileList(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 6. 서버녹화 파일목록을 반환하는 API
	 */
	@ApiOperation(value = "서버녹화 파일 목록을 반환하는 API")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "워크스페이스 ID", defaultValue = "47cfc8b44fad3d0bbdc96d5307c6370a", required = true),
		@ApiImplicitParam(name = "userId", value = "유저 uuid", defaultValue = "4d127135f699616fb88e6bc4fa6d784a", required = true),
		@ApiImplicitParam(name = "sessionId", value = "Session ID", paramType = "query", defaultValue = "session id"),
		@ApiImplicitParam(name = "order", value = "정렬", paramType = "query", defaultValue = "createdAt.asc")
	})
	@GetMapping(value = "record/{workspaceId}/{userId}")
	ResponseEntity<ApiResponse<RecordServerFileInfoListResponse>> getServerRecordFileListRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@RequestParam(name = "sessionId") String sessionId,
		@RequestParam(name = "order") String order
	) {
		if (workspaceId.isEmpty() || userId.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		FileDataRequest option = FileDataRequest.builder()
				.workspaceId(workspaceId)
				.sessionId(sessionId)
				.userId(userId)
				.order(order)
				.build();

		RecordServerFileInfoListResponse responseData = fileService.getServerRecordFileList(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 7. 서버녹화 파일 삭제
	 */
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
		if (workspaceId.isEmpty() || userId.isEmpty() || id.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		FileDataRequest option = FileDataRequest.builder()
				.workspaceId(workspaceId)
				.userId(userId)
				.id(id)
				.build();

		Object responseData = fileService.deleteServerRecordFileUrl(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 8. 로컬녹화 파일 삭제
	 */
	@ApiOperation(value = "[Remote web]에서 업로드된 로컬 녹화 파일 삭제")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "workspaceId", defaultValue = "", required = true),
		@ApiImplicitParam(name = "sessionId", value = "session ID", defaultValue = "", required = true),
		@ApiImplicitParam(name = "userId", value = "user ID", paramType = "query", defaultValue = "", required = true),
		@ApiImplicitParam(name = "objectName", value = "object name", paramType = "query", defaultValue = "", required = true)
	})
	@DeleteMapping(value = "record-file/{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<FileDeleteResponse>> deleteLocalRecordFileRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "sessionId") String sessionId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "objectName") String objectName
	) {
		if (workspaceId.isEmpty() || sessionId.isEmpty() || userId.isEmpty() || objectName.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		FileDataRequest option = FileDataRequest.builder()
				.workspaceId(workspaceId)
				.sessionId(sessionId)
				.userId(userId)
				.objectName(objectName)
				.build();

		FileDeleteResponse responseData = fileService.deleteLocalRecordFileUrl(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 9. 협업 중 첨부파일 삭제
	 */
	@ApiOperation(value = "협업 중 첨부된 파일 삭제")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "workspaceId", value = "workspaceId", defaultValue = "", required = true),
		@ApiImplicitParam(name = "sessionId", value = "session ID", defaultValue = "", required = true),
		@ApiImplicitParam(name = "objectName", value = "object name", paramType = "query", defaultValue = "", required = true),
		@ApiImplicitParam(name = "userId", value = "user ID", paramType = "query", defaultValue = "", required = true),
	})
	@DeleteMapping(value = "{workspaceId}/{sessionId}")
	ResponseEntity<ApiResponse<FileDeleteResponse>> deleteAttachedFileRequestHandler(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "sessionId") String sessionId,
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "objectName") String objectName
	) {
		if (workspaceId.isEmpty() || sessionId.isEmpty() || userId.isEmpty() || objectName.isEmpty()) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		FileDataRequest option = FileDataRequest.builder()
				.workspaceId(workspaceId)
				.sessionId(sessionId)
				.userId(userId)
				.objectName(objectName)
				.build();

		FileDeleteResponse responseData = fileService.deleteAttachedFile(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 11. 협업중 업로드된 첨부파일을 다운로드 링크를 반환하는 API
	 */
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
		if (userId == null && objectName == null) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		FileDataRequest option = FileDataRequest.builder()
				.workspaceId(workspaceId)
				.sessionId(sessionId)
				.userId(userId)
				.objectName(objectName)
				.build();

		FilePreSignedResponse responseData = fileService.getAttachedFileUrl(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 12. 서버 녹화 파일 다운로드 링크를 반환하는 API
	 */
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

		FileDataRequest option = FileDataRequest.builder()
				.workspaceId(workspaceId)
				.userId(userId)
				.id(id)
				.build();

		String responseData = fileService.getServerRecordFileUrl(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}

	/**
	 * 13. 로컬 녹화 파일 다운로드 링크를 반환하는 API
	 */
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
		if (userId == null && objectName == null) {
			throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}

		FileDataRequest option = FileDataRequest.builder()
				.workspaceId(workspaceId)
				.sessionId(sessionId)
				.userId(userId)
				.objectName(objectName)
				.build();

		FilePreSignedResponse responseData = fileService.getLocalRecordFileUrl(option);

		return ResponseEntity.ok(new ApiResponse<>(responseData));
	}
}

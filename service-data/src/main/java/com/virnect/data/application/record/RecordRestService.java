package com.virnect.data.application.record;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.data.dto.rest.RecordServerFileInfoListResponse;
import com.virnect.data.dto.rest.StopRecordingResponse;
import com.virnect.data.global.common.ApiResponse;

@FeignClient(name = "remote-record-server", fallbackFactory = RecordRestFallbackFactory.class)
public interface RecordRestService {

	@DeleteMapping("/remote/recorder/workspaces/{workspaceId}/users/{userId}/recordings")
	ApiResponse<StopRecordingResponse> stopRecordingBySessionId(
		@PathVariable(name = "workspaceId") String workspaceId,
		@PathVariable(name = "userId") String userId,
		@RequestParam(name = "sessionId") String sessionId);

	@GetMapping("/remote/recorder/workspaces/{workspaceId}/users/{userId}/files")
	ApiResponse<RecordServerFileInfoListResponse> getServerRecordFileList(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("userId") String userId,
		@RequestParam("sessionId") String sessionId
	);

	@GetMapping("/remote/recorder/workspaces/{workspaceId}/users/{userId}/files")
	ApiResponse<RecordServerFileInfoListResponse> getServerRecordFileList(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("userId") String userId
	);

	@GetMapping("/remote/recorder/workspaces/{workspaceId}/users/{userId}/files")
	ApiResponse<RecordServerFileInfoListResponse> getServerRecordFileList(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("userId") String userId,
		@RequestParam("sessionId") String sessionId,
		@RequestParam("order") String order
	);

	@DeleteMapping("/remote/recorder/workspaces/{workspaceId}/users/{userId}/files/{id}")
	ApiResponse<Object> deleteServerRecordFile(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("userId") String userId,
		@PathVariable("id") String id
	);

	@GetMapping("/remote/recorder/workspaces/{workspaceId}/users/{userId}/files/{id}/url")
	ApiResponse<String> getServerRecordFileDownloadUrl(
		@PathVariable("workspaceId") String workspaceId,
		@PathVariable("userId") String userId,
		@PathVariable("id") String id
	);
}

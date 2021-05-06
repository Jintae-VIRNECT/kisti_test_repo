package com.virnect.process.application.content;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.virnect.process.domain.YesOrNo;
import com.virnect.process.dto.rest.request.content.ContentDeleteRequest;
import com.virnect.process.dto.rest.request.content.DownloadLogAddRequest;
import com.virnect.process.dto.rest.response.content.ContentCountResponse;
import com.virnect.process.dto.rest.response.content.ContentDeleteListResponse;
import com.virnect.process.dto.rest.response.content.ContentInfoResponse;
import com.virnect.process.dto.rest.response.content.ContentRestDto;
import com.virnect.process.dto.rest.response.content.ContentUploadResponse;
import com.virnect.process.dto.rest.response.content.DownloadLogAddResponse;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.config.FeignConfiguration;

@FeignClient(name = "content-server", configuration = FeignConfiguration.class, fallbackFactory = ContentFallbackService.class)
public interface ContentRestService {
	@GetMapping("/contents/metadata")
	ApiResponse<ContentRestDto> getContentMetadata(@RequestParam("contentUUID") String contentUUID);

	@PutMapping("/contents/convert/{contentUUID}")
	ApiResponse<ContentInfoResponse> contentConvertHandler(
		@PathVariable("contentUUID") String contentUUID, @RequestParam("converted") YesOrNo converted
	);

	@DeleteMapping("/contents")
	ApiResponse<ContentDeleteListResponse> contentDeleteRequestHandler(ContentDeleteRequest contentDeleteRequest);

	@PostMapping("/contents/duplicate/{contentUUID}")
	ApiResponse<ContentUploadResponse> contentDuplicate(
		@PathVariable("contentUUID") String contentUUID
		, @RequestParam("workspaceUUID") String workspaceUUID
		, @RequestParam("userUUID") String userUUID
	);

	@GetMapping("/contents/{contentUUID}")
	ApiResponse<ContentInfoResponse> getContentInfo(@PathVariable("contentUUID") String contentUUID);

	@GetMapping("/contents/countContents")
	ApiResponse<List<ContentCountResponse>> countContents(
		@RequestParam("workspaceUUID") String workspaceUUID, @RequestParam("userUUIDList") List<String> userUUIDList
	);

	@GetMapping("/contents/download/contentUUID/{contentUUID}")
	ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(
		@PathVariable("contentUUID") String contentUUID, @RequestParam("memberUUID") String memberUUID,
		@RequestParam("workspaceUUID") String workspaceUUID
	);

	@GetMapping("/contents/download")
	ResponseEntity<byte[]> contentDownloadRequestForTargetHandler(
		@RequestParam(value = "targetData") String targetData, @RequestParam("memberUUID") String memberUUID,
		@RequestParam(value = "workspaceUUID") String workspaceUUID
	);

	@PostMapping("/contents/download/log")
	ApiResponse<DownloadLogAddResponse> contentDownloadLogForUUIDHandler(
		@RequestBody DownloadLogAddRequest downloadLogAddRequest
	);
}

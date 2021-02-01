package com.virnect.process.api;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

import com.virnect.process.application.DownloadService;
import com.virnect.process.exception.ProcessServiceException;
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
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class DownloadController {
	private final DownloadService downloadService;

	@ApiOperation(value = "콘텐츠UUID로 다운로드")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "contentUUID", value = "콘텐츠 식별자", dataType = "string", paramType = "path", required = true, defaultValue = "e1bd3914-2b69-475f-9f9d-117477dfae05"),
		@ApiImplicitParam(name = "memberUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608"),
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8")
	})
	@GetMapping("/download/contentUUID/{contentUUID}")
	public ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(
		@PathVariable("contentUUID") String contentUUID
		, @RequestParam("memberUUID") String memberUUID
		, @RequestParam("workspaceUUID") String workspaceUUID
	) throws IOException {
		if (!StringUtils.hasText(contentUUID) || !StringUtils.hasText(memberUUID) || !StringUtils.hasText(
			workspaceUUID)) {
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		log.info("[DOWNLOAD] USER: [{}], WORKSPACE [{}] => contentUUID: [{}]", memberUUID, workspaceUUID, contentUUID);

		return this.downloadService.contentDownloadForUUIDHandler(contentUUID, memberUUID, workspaceUUID);
	}

	@ApiOperation(value = "타겟 데이터로 다운로드")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "targetData", value = "타겟 데이터", dataType = "string", paramType = "query", required = true, defaultValue = "mgbvuA6RhUXL JPrK2Z7YoKi7HEp4K0XmmkLbV7SlBQX NzZxnldwTk/22rpebA4"),
		@ApiImplicitParam(name = "memberUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608"),
		@ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "4d6eab0860969a50acbfa4599fbb5ae8")
	})
	@GetMapping("/download")
	public ResponseEntity<byte[]> contentDownloadForTargetHandler(
		@RequestParam("targetData") String targetData
		, @RequestParam("memberUUID") String memberUUID
		, @RequestParam("workspaceUUID") String workspaceUUID
	) throws IOException {
		if (!StringUtils.hasText(targetData) || !StringUtils.hasText(memberUUID) || !StringUtils.hasText(
			workspaceUUID)) {
			throw new ProcessServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		log.info("[DOWNLOAD] USER: [{}], WORKSPACE [{}] => targetData: [{}]", memberUUID, workspaceUUID, targetData);

		return this.downloadService.contentDownloadForTargetHandler(targetData, memberUUID, workspaceUUID);
	}
}

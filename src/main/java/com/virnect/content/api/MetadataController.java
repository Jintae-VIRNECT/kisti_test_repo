package com.virnect.content.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.application.MetadataService;
import com.virnect.content.dto.response.MetadataInfoResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-08-25
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Controller
@RequestMapping("/contents")
@RequiredArgsConstructor
public class MetadataController {

	private final MetadataService metadataService;

	@ApiOperation(value = "컨텐츠 메타데이터 조회", notes = "컨텐츠의 작업으로 전환될 메타데이터를 조회. 제품 2.0에서는 속성 메타데이터로 대체됨")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "contentUUID", value = "컨텐츠 고유 번호", dataType = "string", paramType = "query")
	})
	@GetMapping("/metadata")
	public ResponseEntity<ApiResponse<MetadataInfoResponse>> getContentRawMetadata(
		@RequestParam(value = "contentUUID") String contentUUID
	) {
		if (contentUUID.isEmpty()) {
			throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
		}
		ApiResponse<MetadataInfoResponse> responseMessage = this.metadataService.getContentRawMetadata(contentUUID);
		return ResponseEntity.ok(responseMessage);
	}

}

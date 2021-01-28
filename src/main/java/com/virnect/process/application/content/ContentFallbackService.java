package com.virnect.process.application.content;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import feign.hystrix.FallbackFactory;

import com.virnect.process.domain.YesOrNo;
import com.virnect.process.dto.rest.request.content.ContentDeleteRequest;
import com.virnect.process.dto.rest.response.content.ContentCountResponse;
import com.virnect.process.dto.rest.response.content.ContentDeleteListResponse;
import com.virnect.process.dto.rest.response.content.ContentInfoResponse;
import com.virnect.process.dto.rest.response.content.ContentRestDto;
import com.virnect.process.dto.rest.response.content.ContentUploadResponse;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.error.ErrorCode;

@Service
public class ContentFallbackService implements FallbackFactory<ContentRestService> {

	@Override
	public ContentRestService create(Throwable cause) {
		System.out.println("이거 왜 안돌지");
		return new ContentRestService() {
			@Override
			public ApiResponse<ContentRestDto> getContentMetadata(String contentUUID) {
				return null;
			}

			@Override
			public ApiResponse<ContentInfoResponse> contentConvertHandler(
				String contentUUID, YesOrNo converted
			) {
				return null;
			}

			@Override
			public ApiResponse<ContentDeleteListResponse> contentDeleteRequestHandler(
				ContentDeleteRequest contentDeleteRequest
			) {
				return null;
			}

			@Override
			public ApiResponse<ContentUploadResponse> contentDuplicate(
				String contentUUID, String workspaceUUID, String userUUID
			) {
				return null;
			}

			@Override
			public ApiResponse<ContentInfoResponse> getContentInfo(String contentUUID) {
				return null;
			}

			@Override
			public ApiResponse<List<ContentCountResponse>> countContents(
				String workspaceUUID, List<String> userUUIDList
			) {
				return null;
			}

			@Override
			public ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(String contentUUID, String memberUUID) {
				return null;
			}

			@Override
			public ResponseEntity<byte[]> contentDownloadRequestForTargetHandler(
				String targetData, String memberUUID, String workspaceUUID
			) {
				System.out.println("ContentFallbackService.contentDownloadRequestForTargetHandler");
				throw new ProcessServiceException(ErrorCode.ERR_ALREADY_TRANSFORMED);
			}
		};
	}
}

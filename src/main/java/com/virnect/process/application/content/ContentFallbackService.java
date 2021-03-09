package com.virnect.process.application.content;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import feign.hystrix.FallbackFactory;

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

@Service
public class ContentFallbackService implements FallbackFactory<ContentRestService> {

	@Override
	public ContentRestService create(Throwable cause) {
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
			public ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(
				String contentUUID, String memberUUID, String workspaceUUID
			) {
				return null;
			}

			@Override
			public ResponseEntity<byte[]> contentDownloadRequestForTargetHandler(
				String targetData, String memberUUID, String workspaceUUID
			) {
				return null;
			}

			@Override
			public ApiResponse<DownloadLogAddResponse> contentDownloadLogForUUIDHandler(
				DownloadLogAddRequest downloadLogAddRequest
			) {
				DownloadLogAddResponse downloadLogAddResponse = new DownloadLogAddResponse(false);
				return new ApiResponse<>(downloadLogAddResponse);
			}
		};
	}
}

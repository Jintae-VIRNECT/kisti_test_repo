package com.virnect.process.application.content;

import com.virnect.process.domain.YesOrNo;
import com.virnect.process.dto.rest.request.content.ContentDeleteRequest;
import com.virnect.process.dto.rest.request.content.ContentStatusChangeRequest;
import com.virnect.process.dto.rest.response.content.*;
import com.virnect.process.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentFallbackService implements ContentRestService {
    @Override
    public ApiResponse<ContentRestDto> getContentMetadata(String contentUUID) {
        return null;
    }

    @Override
    public ApiResponse<ContentInfoResponse> contentConvertHandler(String contentUUID, YesOrNo converted) {
        return null;
    }

    @Override
    public ApiResponse<ContentUploadResponse> taskToContentConvertHandler(Long taskId, String userUUID) {
        return null;
    }

    @Override
    public ApiResponse<ContentDeleteListResponse> contentDeleteRequestHandler(ContentDeleteRequest contentDeleteRequest) {
        return null;
    }

    @Override
    public ApiResponse<ContentStatusInfoResponse> changeContentStatus(ContentStatusChangeRequest contentStatusChangeRequest) {
        return null;
    }

    @Override
    public ApiResponse<ContentUploadResponse> contentDuplicate(String contentUUID, String workspaceUUID, String userUUID) {
        return null;
    }

    @Override
    public ApiResponse<ContentInfoResponse> getContentInfo(String contentUUID) {
        return null;
    }

    @Override
    public ApiResponse<List<ContentCountResponse>> countContents(String workspaceUUID, List<String> userUUIDList) { return null; }

    @Override
    public ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(String contentUUID, String memberUUID) { return null; }
}

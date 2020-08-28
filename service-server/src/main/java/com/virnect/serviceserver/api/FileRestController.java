package com.virnect.serviceserver.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.api.IFileRestAPI;
import com.virnect.data.dto.file.request.FileDownloadRequest;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.dto.file.response.FileDeleteResponse;
import com.virnect.data.dto.file.response.FileInfoListResponse;
import com.virnect.data.dto.file.response.FileUploadResponse;
import com.virnect.data.dto.request.PageRequest;
import com.virnect.data.dto.request.RoomProfileUpdateRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.RoomProfileUpdateResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.serviceserver.data.DataRepository;
import com.virnect.serviceserver.data.FileDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileRestController implements IFileRestAPI {
    private static final String TAG = FileRestController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/file";

    private final FileDataRepository fileDataRepository;

    @Override
    public ResponseEntity<ApiResponse<FileUploadResponse>> fileUploadRequestHandler(@Valid FileUploadRequest fileUploadRequest, BindingResult result) {
        log.info("REST API: POST {}/upload", REST_PATH);
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<FileUploadResponse> apiResponse = this.fileDataRepository.uploadFile(fileUploadRequest);
        //log.info("[FILE UPLOAD RESPONSE] :: {}", apiResponse.getData().toString());

        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<RoomProfileUpdateResponse>> profileUploadRequestHandler(@Valid RoomProfileUpdateRequest roomProfileUpdateRequest, String workspaceId, String sessionId, BindingResult result) {
        log.info("REST API: POST {}/{}/{}/profile",
                REST_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}");
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        return ResponseEntity.ok(
                this.fileDataRepository.uploadProfile(workspaceId, sessionId, roomProfileUpdateRequest)
        );
    }

    @Override
    public ResponseEntity<byte[]> fileDownloadRequestHandler(String workspaceId, String sessionId, String userId, String filePath) throws IOException {
        log.info("REST API: GET {}/download/{}/{}",
                REST_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}");

        if(userId == null && filePath == null) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ResponseEntity<byte[]> responseEntity = this.fileDataRepository.downloadFile(workspaceId, sessionId, userId, filePath).getData();
        //eventPublisher.publishEvent(new ContentDownloadHitEvent(content));
        return responseEntity;
    }


    @Override
    public ResponseEntity<ApiResponse<FileInfoListResponse>> getFileList(
            String workspaceId,
            String sessionId,
            String userId,
            boolean deleted,
            PageRequest pageRequest) {
        log.info("REST API: GET {}/{}/{}/{}", REST_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}",
                userId != null ? userId : "{}");
        return ResponseEntity.ok(
                this.fileDataRepository.getFileInfoList(workspaceId, sessionId, userId, deleted, pageRequest.of())
        );
    }

    @Override
    public ResponseEntity<ApiResponse<FileDeleteResponse>> deleteFileRequestHandler(String workspaceId, String sessionId, String userId, String filePath) {
        log.info("REST API: GET {}/{}/{}/{}",
                REST_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}",
                filePath != null ? filePath : "{}");
        if (userId == null || filePath == null) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return ResponseEntity.ok(
                this.fileDataRepository.removeFile(workspaceId, sessionId, userId, filePath)
        );
    }
}

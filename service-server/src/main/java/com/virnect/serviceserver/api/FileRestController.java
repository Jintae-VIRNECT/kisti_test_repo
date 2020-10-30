package com.virnect.serviceserver.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.api.IFileRestAPI;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.dto.file.response.FileDeleteResponse;
import com.virnect.data.dto.file.response.FileInfoListResponse;
import com.virnect.data.dto.file.response.FilePreSignedResponse;
import com.virnect.data.dto.file.response.FileUploadResponse;
import com.virnect.data.dto.request.PageRequest;
import com.virnect.data.dto.request.RoomProfileUpdateRequest;
import com.virnect.data.dto.response.RoomProfileUpdateResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.data.FileDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final String REST_FILE_PATH = "/remote/file";
    private static final String REST_RECORD_PATH = "/remote/file";

    private final FileDataRepository fileDataRepository;

    @Autowired
    private RemoteServiceConfig remoteServiceConfig;

    @Override
    public ResponseEntity<ApiResponse<FileUploadResponse>> fileUploadRequestHandler(@Valid FileUploadRequest fileUploadRequest, BindingResult result) {
        log.info("REST API: POST {}/upload", REST_FILE_PATH);
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            ApiResponse<FileUploadResponse> apiResponse = this.fileDataRepository.uploadFile(fileUploadRequest);
            return ResponseEntity.ok(apiResponse);
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<FileUploadResponse>> recordFileUploadRequestHandler(@Valid FileUploadRequest fileUploadRequest, BindingResult result) {
        log.info("REST API: POST {}/upload", REST_RECORD_PATH);
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            ApiResponse<FileUploadResponse> apiResponse = this.fileDataRepository.uploadRecordFile(fileUploadRequest);
            return ResponseEntity.ok(apiResponse);
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<RoomProfileUpdateResponse>> profileUploadRequestHandler(@Valid RoomProfileUpdateRequest roomProfileUpdateRequest, String workspaceId, String sessionId, BindingResult result) {
        log.info("REST API: POST {}/{}/{}/profile",
                REST_FILE_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}");
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }

            return ResponseEntity.ok(
                    this.fileDataRepository.uploadProfile(workspaceId, sessionId, roomProfileUpdateRequest)
            );
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<FilePreSignedResponse>> fileDownloadUrlRequestHandler(String workspaceId, String sessionId, String userId, String objectName) throws IOException {
        log.info("REST API: GET {}/download/url/{}/{}",
                REST_FILE_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}");
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (userId == null && objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            return ResponseEntity.ok(
                    this.fileDataRepository.downloadFileUrl(workspaceId, sessionId, userId, objectName)
            );
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<FileInfoListResponse>> getFileList(
            String workspaceId,
            String sessionId,
            String userId,
            boolean deleted,
            PageRequest pageRequest) {
        log.info("REST API: GET {}/{}/{}/{}", REST_FILE_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}",
                userId != null ? userId : "{}");
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            return ResponseEntity.ok(
                    this.fileDataRepository.getFileInfoList(workspaceId, sessionId, userId, deleted, pageRequest.of())
            );
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<FileDeleteResponse>> deleteFileRequestHandler(String workspaceId, String sessionId, String userId, String objectName) {
        log.info("REST API: GET {}/{}/{}/{}",
                REST_FILE_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}",
                objectName != null ? objectName : "{}");
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (userId == null || objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            return ResponseEntity.ok(
                    this.fileDataRepository.removeFile(workspaceId, sessionId, userId, objectName)
            );
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }
}

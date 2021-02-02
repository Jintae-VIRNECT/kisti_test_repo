package com.virnect.serviceserver.api;

import com.virnect.service.ApiResponse;
import com.virnect.service.api.IFileRestAPI;
import com.virnect.service.dto.PageRequest;
import com.virnect.service.dto.ResultResponse;
import com.virnect.service.dto.file.request.FileUploadRequest;
import com.virnect.service.dto.file.request.RecordFileUploadRequest;
import com.virnect.service.dto.file.request.RoomProfileUpdateRequest;
import com.virnect.service.dto.file.response.*;
import com.virnect.service.error.ErrorCode;
import com.virnect.service.error.exception.RestServiceException;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.dao.FileDataRepository;
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
        log.info("REST API::POST::#fileUploadRequestHandler::{}/upload", REST_FILE_PATH);
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
    public ResponseEntity<ApiResponse<FileUploadResponse>> recordFileUploadRequestHandler(@Valid RecordFileUploadRequest recordFileUploadRequest, BindingResult result) {
        log.info("REST API::POST#recordFileUploadRequestHandler::{}/upload", REST_RECORD_PATH);
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            ApiResponse<FileUploadResponse> apiResponse = this.fileDataRepository.uploadRecordFile(recordFileUploadRequest);
            return ResponseEntity.ok(apiResponse);
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<RoomProfileUpdateResponse>> profileUploadRequestHandler(@Valid RoomProfileUpdateRequest roomProfileUpdateRequest, String workspaceId, String sessionId, BindingResult result) {
        log.info("REST API::POST::#profileUploadRequestHandler::{}/{}/{}/profile",
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
    public ResponseEntity<ApiResponse<ResultResponse>> profileDeleteRequestHandler(String workspaceId, String sessionId) {
        log.info("REST API::DELETE::#profileDeleteRequestHandler::{}/{}/{}/profile",
                REST_FILE_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}");
        return ResponseEntity.ok(
                this.fileDataRepository.deleteProfile(workspaceId, sessionId)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<FilePreSignedResponse>> fileDownloadUrlRequestHandler(String workspaceId, String sessionId, String userId, String objectName) throws IOException {
        log.info("REST API::GET::#fileDownloadUrlRequestHandler::{}/download/url/{}/{}",
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
    public ResponseEntity<ApiResponse<FilePreSignedResponse>> recordFileDownloadUrlRequestHandler(String workspaceId, String sessionId, String userId, String objectName) throws IOException {
        log.info("REST API::GET::#recordFileDownloadUrlRequestHandler::{}/download/url/{}/{}",
                REST_FILE_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}");
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (userId == null && objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            return ResponseEntity.ok(
                    this.fileDataRepository.downloadRecordFileUrl(workspaceId, sessionId, userId, objectName)
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
        log.info("REST API::GET::#getFileList::{}/{}/{}/{}", REST_FILE_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}",
                userId != null ? userId : "{}");
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            return ResponseEntity.ok(
                    this.fileDataRepository.getFileInfoList(workspaceId, sessionId, userId, deleted, pageRequest.ofSortBy())
            );
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<FileDetailInfoListResponse>> getDetailFileList(
            String workspaceId,
            String sessionId,
            String userId,
            boolean deleted,
            PageRequest pageRequest) {
        log.info("REST API::GET::#getDetailFileList::{}/{}/{}/{}", REST_FILE_PATH,
                workspaceId != null ? workspaceId : "{}",
                sessionId != null ? sessionId : "{}",
                userId != null ? userId : "{}");
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            return ResponseEntity.ok(
                    this.fileDataRepository.getRecordFileInfoList(workspaceId, sessionId, userId, deleted, pageRequest.ofSortBy())
            );
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<FileDeleteResponse>> deleteFileRequestHandler(String workspaceId, String sessionId, String userId, String objectName) {
        log.info("REST API::GET::#deleteFileRequestHandler::{}/{}/{}/{}",
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

    @Override
    public ResponseEntity<ApiResponse<String>> fileDownloadUrlRequestHandler(String objectName) throws IOException {
        log.info("REST API::GET::#fileDownloadUrlRequestHandler::{}/guide/{}",
                REST_FILE_PATH,
                objectName != null ? objectName : "{}");
        if(this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            return ResponseEntity.ok(
                    this.fileDataRepository.downloadFileUrl(objectName)
            );
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
    }
}

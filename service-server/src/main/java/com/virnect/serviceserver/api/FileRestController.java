package com.virnect.serviceserver.api;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.remote.dto.request.file.FileUploadRequest;
import com.virnect.remote.dto.request.file.RecordFileUploadRequest;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.remote.application.FileService;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;
import com.virnect.remote.dto.request.file.RoomProfileUpdateRequest;
import com.virnect.remote.dto.response.PageRequest;
import com.virnect.remote.dto.response.ResultResponse;
import com.virnect.remote.dto.response.file.FileDeleteResponse;
import com.virnect.remote.dto.response.file.FileDetailInfoListResponse;
import com.virnect.remote.dto.response.file.FileInfoListResponse;
import com.virnect.remote.dto.response.file.FilePreSignedResponse;
import com.virnect.remote.dto.response.file.FileUploadResponse;
import com.virnect.remote.dto.response.file.RoomProfileUpdateResponse;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class FileRestController {
    //private static final String TAG = FileRestController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_FILE_PATH = "/remote/file";
    private static final String REST_RECORD_PATH = "/remote/file";

    //private final FileDataRepository fileDataRepository;

    //private final FileServiceTemp fileService;

    private final FileService fileService;

    @Autowired
    private RemoteServiceConfig remoteServiceConfig;

    @ApiOperation(value = "Upload file", notes = "파일 식별자를 서버에서 발급하며, 식별자는 업로드 완료 후 반환됨.\n파일 첨부시 사용")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "48acaade22f3e2bba74bb6f1c44389a9"),
        @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "form", required = true, defaultValue = "ses_JRG7p1fFox"),
        @ApiImplicitParam(name = "userId", value = "업로드 사용자 고유 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "4218059539d944fca0a27fc5a57ce05b"),
        @ApiImplicitParam(name = "file", value = "업로드 파일", dataType = "__file", paramType = "form", required = true),
    })
    @PostMapping(value = "file/upload", headers = {
        "content-type=multipart/*"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponse>> fileUploadRequestHandler(
        @ModelAttribute @Valid FileUploadRequest fileUploadRequest,
        BindingResult result
    ) {
        log.info("REST API::POST::#fileUploadRequestHandler::{}/upload", REST_FILE_PATH);

        ApiResponse<FileUploadResponse> responseData;
        if (this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.uploadFile(fileUploadRequest);
            /*ApiResponse<FileUploadResponse> apiResponse = this.fileDataRepository.uploadFile(fileUploadRequest);*/
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Upload local record file", notes = "로컬 녹화 파일을 업로드 합니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "48acaade22f3e2bba74bb6f1c44389a9"),
        @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "form", required = true, defaultValue = "ses_JRG7p1fFox"),
        @ApiImplicitParam(name = "userId", value = "업로드 사용자 고유 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "4218059539d944fca0a27fc5a57ce05b"),
        @ApiImplicitParam(name = "file", value = "업로드 파일", dataType = "__file", paramType = "form", required = true),
    })
    @PostMapping(value = "record/upload", headers = {
        "content-type=multipart/*"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponse>> recordFileUploadRequestHandler(
        @ModelAttribute @Valid RecordFileUploadRequest recordFileUploadRequest,
        BindingResult result
    ) {
        log.info("REST API::POST#recordFileUploadRequestHandler::{}/upload", REST_RECORD_PATH);
        ApiResponse<FileUploadResponse> responseData;
        if (this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.uploadRecordFile(recordFileUploadRequest);

			/*ApiResponse<FileUploadResponse> apiResponse = this.fileDataRepository.uploadRecordFile(
				recordFileUploadRequest);
			return ResponseEntity.ok(apiResponse);*/
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Update a Remote Room profile image file", notes = "원격협업 방 프로필을 업데이트 합니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(value = "변경할 프로필 이미지", name = "profile", paramType = "form", dataType = "__file", required = true)
    })
    @PostMapping(value = "file/{workspaceId}/{sessionId}/profile", headers = {
        "content-type=multipart/*"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RoomProfileUpdateResponse>> profileUploadRequestHandler(
        @ModelAttribute @Valid RoomProfileUpdateRequest roomProfileUpdateRequest,
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId,
        BindingResult result
    ) {
        log.info(
            "REST API::POST::#profileUploadRequestHandler::{}/{}/{}/profile",
            REST_FILE_PATH,
            workspaceId != null ? workspaceId : "{}",
            sessionId != null ? sessionId : "{}"
        );

        ApiResponse<RoomProfileUpdateResponse> responseData;
        if (this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }

            responseData = fileService.profileUpload(workspaceId, sessionId, roomProfileUpdateRequest);
			/*return ResponseEntity.ok(
				this.fileDataRepository.uploadProfile(workspaceId, sessionId, roomProfileUpdateRequest)
			);*/
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Delete a Remote Room profile image file", notes = "원격협업 방 프로필을 삭제합니다.")
    @DeleteMapping(value = "file/{workspaceId}/{sessionId}/profile")
    public ResponseEntity<ApiResponse<ResultResponse>> profileDeleteRequestHandler(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId
    ) {
        log.info(
            "REST API::DELETE::#profileDeleteRequestHandler::{}/{}/{}/profile",
            REST_FILE_PATH,
            workspaceId != null ? workspaceId : "{}",
            sessionId != null ? sessionId : "{}"
        );

        ApiResponse<ResultResponse> responseData = fileService.deleteProfile(workspaceId, sessionId);
        return ResponseEntity.ok(responseData);
		/*return ResponseEntity.ok(
			this.fileDataRepository.deleteProfile(workspaceId, sessionId)
		);*/
    }

    @ApiOperation(value = "Get URL to download file", notes = "파일 다운로드 URL을 받습니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "path", required = true),
        @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "path", required = true),
        @ApiImplicitParam(name = "userId", value = "다운로드 사용자 고유 식별자", dataType = "string", paramType = "query", required = true),
        @ApiImplicitParam(name = "objectName", value = "다운로드 파일 고유 이름", dataType = "string", paramType = "query", required = true),
    })
    @GetMapping(value = "file/download/url/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<FilePreSignedResponse>> fileDownloadUrlRequestHandler(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "sessionId") String sessionId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "objectName") String objectName
    ) throws IOException {
        log.info(
            "REST API::GET::#fileDownloadUrlRequestHandler::{}/download/url/{}/{}",
            REST_FILE_PATH,
            workspaceId != null ? workspaceId : "{}",
            sessionId != null ? sessionId : "{}"
        );

        ApiResponse<FilePreSignedResponse> responseData;
        if (this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (userId == null && objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.downloadFileUrl(workspaceId, sessionId, userId, objectName);
			/*return ResponseEntity.ok(
				this.fileDataRepository.downloadFileUrl(workspaceId, sessionId, userId, objectName)
			);*/
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Get URL to download record file", notes = "레코드 파일 다운로드 URL을 받습니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "path", required = true),
        @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "path", required = true),
        @ApiImplicitParam(name = "userId", value = "다운로드 사용자 고유 식별자", dataType = "string", paramType = "query", required = true),
        @ApiImplicitParam(name = "objectName", value = "다운로드 파일 고유 이름", dataType = "string", paramType = "query", required = true),
    })
    @GetMapping(value = "file/record/download/url/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<FilePreSignedResponse>> recordFileDownloadUrlRequestHandler(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "sessionId") String sessionId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "objectName") String objectName
    ) throws IOException {
        log.info(
            "REST API::GET::#recordFileDownloadUrlRequestHandler::{}/download/url/{}/{}",
            REST_FILE_PATH,
            workspaceId != null ? workspaceId : "{}",
            sessionId != null ? sessionId : "{}"
        );

        ApiResponse<FilePreSignedResponse> responseData;

        if (this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (userId == null && objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.downloadRecordFileUrl(workspaceId, sessionId, userId, objectName);

			/*return ResponseEntity.ok(
				this.fileDataRepository.downloadRecordFileUrl(workspaceId, sessionId, userId, objectName)
			);*/
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Load Room File List", notes = "원격협업에서 등록된 파일 목록을 조회")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(createdDate, ASC or DESC)", paramType = "query", defaultValue = "createdDate, DESC"),
        @ApiImplicitParam(name = "deleted", value = "삭제 파일 필터 옵션 (YES, NO)", dataType = "boolean", defaultValue = "false"),
    })
    @GetMapping("file")
    public ResponseEntity<ApiResponse<FileInfoListResponse>> getFileList(
        @RequestParam(value = "workspaceId") String workspaceId,
        @RequestParam(value = "sessionId") String sessionId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(value = "deleted", required = false, defaultValue = "false") boolean deleted,
        @ApiIgnore PageRequest pageRequest
    ) {
        log.info("REST API::GET::#getFileList::{}/{}/{}/{}", REST_FILE_PATH,
            workspaceId != null ? workspaceId : "{}",
            sessionId != null ? sessionId : "{}",
            userId != null ? userId : "{}"
        );

        ApiResponse<FileInfoListResponse> responseData;

        if (this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {

            responseData = fileService.getFileInfoList(workspaceId, sessionId, userId, deleted, pageRequest.ofSortBy());

			/*return ResponseEntity.ok(
				this.fileDataRepository.getFileInfoList(workspaceId, sessionId, userId, deleted, pageRequest.ofSortBy())
			);*/
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Load Room Record File Detail Information List", notes = "원격협업에서 등록된 로컬 녹화 파일 목록을 상세 조회")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "size", value = "페이징 사이즈(최대 20)", dataType = "number", paramType = "query", defaultValue = "10"),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(createdDate, ASC or DESC)", paramType = "query", defaultValue = "createdDate, DESC"),
        @ApiImplicitParam(name = "deleted", value = "삭제 파일 필터 옵션 (YES, NO)", dataType = "boolean", defaultValue = "false"),
    })
    @GetMapping(value = "file/record/info")
    public ResponseEntity<ApiResponse<FileDetailInfoListResponse>> getDetailFileList(
        @RequestParam(value = "workspaceId") String workspaceId,
        @RequestParam(value = "sessionId", required = false) String sessionId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(value = "deleted", required = false, defaultValue = "false") boolean deleted,
        @ApiIgnore PageRequest pageRequest
    ) {
        log.info("REST API::GET::#getDetailFileList::{}/{}/{}/{}", REST_FILE_PATH,
            workspaceId != null ? workspaceId : "{}",
            sessionId != null ? sessionId : "{}",
            userId != null ? userId : "{}"
        );

        ApiResponse<FileDetailInfoListResponse> responseData;

        if (this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {

            responseData = fileService.getRecordFileInfoList(workspaceId, sessionId, userId, deleted, pageRequest.ofSortBy());

			/*return ResponseEntity.ok(
				this.fileDataRepository.getRecordFileInfoList(
					workspaceId, sessionId, userId, deleted, pageRequest.ofSortBy())
			);*/
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Delete the specific file", notes = "파일을 삭제")
    @DeleteMapping(value = "file/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<FileDeleteResponse>> deleteFileRequestHandler(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId,
        @RequestParam("userId") String userId,
        @RequestParam("objectName") String objectName
    ) {
        log.info(
            "REST API::GET::#deleteFileRequestHandler::{}/{}/{}/{}",
            REST_FILE_PATH,
            workspaceId != null ? workspaceId : "{}",
            sessionId != null ? sessionId : "{}",
            objectName != null ? objectName : "{}"
        );

        ApiResponse<FileDeleteResponse> responseData;

        if (this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (userId == null || objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }

            responseData = fileService.removeFile(workspaceId, sessionId, userId, objectName);

			/*return ResponseEntity.ok(
				this.fileDataRepository.removeFile(workspaceId, sessionId, userId, objectName)
			);*/
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Get URL to download guide file", notes = "가이드 파일 다운로드 URL을 받습니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "objectName", value = "다운로드 파일 고유 이름", dataType = "string", paramType = "query", required = true),
    })
    @GetMapping(value = "file/guide/")
    public ResponseEntity<ApiResponse<String>> fileDownloadUrlRequestHandler(
        @RequestParam(name = "objectName") String objectName
    ) throws IOException {
        log.info(
            "REST API::GET::#fileDownloadUrlRequestHandler::{}/guide/{}",
            REST_FILE_PATH,
            objectName != null ? objectName : "{}"
        );

        ApiResponse<String> responseData;

        if (this.remoteServiceConfig.remoteStorageProperties.isServiceEnabled()) {
            if (objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }

            responseData = fileService.downloadGuideFileUrl(objectName);

			/*return ResponseEntity.ok(
				this.fileDataRepository.downloadFileUrl(objectName)
			);*/
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }
}

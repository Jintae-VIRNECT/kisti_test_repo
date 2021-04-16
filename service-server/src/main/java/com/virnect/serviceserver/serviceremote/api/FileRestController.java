package com.virnect.serviceserver.serviceremote.api;

import javax.validation.Valid;

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

import com.virnect.data.domain.file.FileType;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.application.FileService;
import com.virnect.serviceserver.serviceremote.dto.request.file.FileUploadRequest;
import com.virnect.serviceserver.serviceremote.dto.request.file.RecordFileUploadRequest;
import com.virnect.serviceserver.serviceremote.dto.request.file.RoomProfileUpdateRequest;
import com.virnect.serviceserver.serviceremote.dto.response.PageRequest;
import com.virnect.serviceserver.serviceremote.dto.response.ResultResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileDeleteResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileDetailInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FilePreSignedResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.FileUploadResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.RoomProfileUpdateResponse;
import com.virnect.serviceserver.global.config.property.RemoteStorageProperties;
import com.virnect.serviceserver.serviceremote.dto.response.file.ShareFileInfoListResponse;
import com.virnect.serviceserver.serviceremote.dto.response.file.ShareFileUploadResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class FileRestController {

    private static final String TAG = FileRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/file";

    private final String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";

    private final FileService fileService;

    private final RemoteStorageProperties remoteStorageProperties;

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
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "::"
                + (fileUploadRequest.toString() != null ? fileUploadRequest.toString() : "{}"),
            "fileUploadRequestHandler"
        );
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                LogMessage.formedError(
                    TAG,
                    "REST API: POST " + REST_PATH,
                    "fileUploadRequestHandler",
                    LogMessage.PARAMETER_ERROR,
                    message.toString()
                )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<FileUploadResponse> responseData;
        if (remoteStorageProperties.isEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.uploadFile(fileUploadRequest, FileType.FILE);
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
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "::"
                + (recordFileUploadRequest.toString() != null ? recordFileUploadRequest.toString() : "{}"),
            "recordFileUploadRequestHandler"
        );

        if (result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                LogMessage.formedError(
                    TAG,
                    "REST API: POST " + REST_PATH,
                    "recordFileUploadRequestHandler",
                    LogMessage.PARAMETER_ERROR,
                    message.toString()
                )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<FileUploadResponse> responseData;
        if (remoteStorageProperties.isEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.uploadRecordFile(recordFileUploadRequest);
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
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (sessionId != null ? sessionId : "{}") + "::"
                + (roomProfileUpdateRequest.toString() != null ? roomProfileUpdateRequest.toString() : "{}"),
            "profileUploadRequestHandler"
        );
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                LogMessage.formedError(
                    TAG,
                    "REST API: POST " + REST_PATH,
                    "profileUploadRequestHandler",
                    LogMessage.PARAMETER_ERROR,
                    message.toString()
                )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<RoomProfileUpdateResponse> responseData;
        if (remoteStorageProperties.isEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }

            responseData = fileService.profileUpload(workspaceId, sessionId, roomProfileUpdateRequest);
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
        LogMessage.formedInfo(
            TAG,
            "REST API: DELETE "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (sessionId != null ? sessionId : "{}"),
            "profileDeleteRequestHandler"
        );

        ApiResponse<ResultResponse> responseData = fileService.deleteProfile(workspaceId, sessionId);
        return ResponseEntity.ok(responseData);
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
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (sessionId != null ? sessionId : "{}"),
            "fileDownloadUrlRequestHandler"
        );
        ApiResponse<FilePreSignedResponse> responseData;
        if (remoteStorageProperties.isEnabled()) {
            if (userId == null && objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.downloadFileUrl(workspaceId, sessionId, userId, objectName, FileType.FILE);
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
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (sessionId != null ? sessionId : "{}"),
            "recordFileDownloadUrlRequestHandler"
        );

        ApiResponse<FilePreSignedResponse> responseData;

        if (remoteStorageProperties.isEnabled()) {
            if (userId == null && objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.downloadRecordFileUrl(workspaceId, sessionId, userId, objectName);
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
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "::"
                + "workspaceId:" + (workspaceId != null ? workspaceId : "{}") + "/"
                + "sessionId:" + (sessionId != null ? sessionId : "{}") + "/"
                + "userId:" + (userId != null ? sessionId : "{}") + "/"
                + "deleted:" + deleted,
            "getFileList"
        );
        ApiResponse<FileInfoListResponse> responseData;

        if (remoteStorageProperties.isEnabled()) {
            responseData = fileService.getFileInfoList(
                workspaceId,
                sessionId,
                userId,
                deleted,
                pageRequest.ofSortBy(),
                FileType.FILE
            );
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
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "::"
                + "workspaceId:" + (workspaceId != null ? workspaceId : "{}") + "/"
                + "sessionId:" + (sessionId != null ? sessionId : "{}") + "/"
                + "userId:" + (userId != null ? sessionId : "{}") + "/"
                + "deleted:" + deleted,
            "getDetailFileList"
        );

        ApiResponse<FileDetailInfoListResponse> responseData;

        if (remoteStorageProperties.isEnabled()) {
            responseData = fileService.getRecordFileInfoList(workspaceId, sessionId, userId, deleted, pageRequest.ofSortBy());
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
        LogMessage.formedInfo(
            TAG,
            "REST API: DELETE "
                + REST_PATH + "::"
                + "workspaceId:" + (workspaceId != null ? workspaceId : "{}") + "/"
                + "sessionId:" + (sessionId != null ? sessionId : "{}") + "/"
                + "userId:" + (userId != null ? sessionId : "{}") + "/"
                + "objectName:" + (objectName != null ? objectName : "{}"),
            "deleteFileRequestHandler"
        );
        ApiResponse<FileDeleteResponse> responseData;

        if (remoteStorageProperties.isEnabled()) {
            if (userId == null || objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }

            responseData = fileService.removeFile(workspaceId, sessionId, userId, objectName, FileType.FILE);
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
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "::"
                + "objectName:" + (objectName != null ? objectName : "{}"),
            "fileDownloadUrlRequestHandler"
        );
        ApiResponse<String> responseData;

        if (remoteStorageProperties.isEnabled()) {
            if (objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.downloadGuideFileUrl(objectName);
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[Share] Upload file", notes = "[공유 파일] 파일 식별자를 서버에서 발급하며, 식별자는 업로드 완료 후 반환됨.\n파일 첨부시 사용")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "48acaade22f3e2bba74bb6f1c44389a9"),
        @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "form", required = true, defaultValue = "ses_JRG7p1fFox"),
        @ApiImplicitParam(name = "userId", value = "업로드 사용자 고유 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "4218059539d944fca0a27fc5a57ce05b"),
        @ApiImplicitParam(name = "file", value = "업로드 파일", dataType = "__file", paramType = "form", required = true),
    })
    @PostMapping(value = "file/upload/share", headers = {
        "content-type=multipart/*"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ShareFileUploadResponse>> shareFileUploadRequestHandler(
        @ModelAttribute @Valid FileUploadRequest fileUploadRequest,
        BindingResult result
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "::"
                + (fileUploadRequest.toString() != null ? fileUploadRequest.toString() : "{}"),
            "fileUploadRequestHandler"
        );
        if (result.hasErrors()) {
            result.getAllErrors().forEach(message ->
                LogMessage.formedError(
                    TAG,
                    "REST API: POST " + REST_PATH,
                    "fileUploadRequestHandler",
                    LogMessage.PARAMETER_ERROR,
                    message.toString()
                )
            );
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<ShareFileUploadResponse> responseData;
        if (remoteStorageProperties.isEnabled()) {
            if (result.hasErrors()) {
                result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.uploadShareFile(fileUploadRequest, FileType.SHARE);

        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[Share] Load Room File List", notes = "[공유 파일] 원격협업에서 등록된 파일 목록을 조회")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "paging", value = "검색 결과 페이지네이션 여부", dataType = "boolean", allowEmptyValue = true, defaultValue = "false"),
        @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
        @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
        @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(createdDate, ASC or DESC)", paramType = "query", defaultValue = "createdDate, DESC"),
        @ApiImplicitParam(name = "deleted", value = "삭제 파일 필터 옵션 (YES, NO)", dataType = "boolean", defaultValue = "false"),
    })
    @GetMapping("file/share")
    public ResponseEntity<ApiResponse<ShareFileInfoListResponse>> getShareFileList(
        @RequestParam(value = "workspaceId") String workspaceId,
        @RequestParam(value = "sessionId") String sessionId,
        @RequestParam(name = "paging") boolean paging,
        @RequestParam(value = "deleted", required = false, defaultValue = "false") boolean deleted,
        @ApiIgnore PageRequest pageRequest
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "::"
                + "workspaceId:" + (workspaceId != null ? workspaceId : "{}") + "/"
                + "sessionId:" + (sessionId != null ? sessionId : "{}") + "/"
                + "deleted:" + deleted,
            "getFileList"
        );
        ApiResponse<ShareFileInfoListResponse> responseData;

        if (remoteStorageProperties.isEnabled()) {
            responseData = fileService.getShareFileInfoList(
                workspaceId,
                sessionId,
                paging,
                pageRequest.ofSortBy()
            );
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[Share] Get URL to download file", notes = "[공유 파일] 파일 다운로드 URL을 받습니다.")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "path", required = true),
        @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "path", required = true),
        @ApiImplicitParam(name = "userId", value = "다운로드 사용자 고유 식별자", dataType = "string", paramType = "query", required = true),
        @ApiImplicitParam(name = "objectName", value = "다운로드 파일 고유 이름", dataType = "string", paramType = "query", required = true),
    })
    @GetMapping(value = "file/download/url/share/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<FilePreSignedResponse>> shareFileDownloadUrlRequestHandler(
        @PathVariable(name = "workspaceId") String workspaceId,
        @PathVariable(name = "sessionId") String sessionId,
        @RequestParam(name = "userId") String userId,
        @RequestParam(name = "objectName") String objectName
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET "
                + REST_PATH + "/"
                + (workspaceId != null ? workspaceId : "{}") + "/"
                + (sessionId != null ? sessionId : "{}"),
            "fileDownloadUrlRequestHandler"
        );
        ApiResponse<FilePreSignedResponse> responseData;
        if (remoteStorageProperties.isEnabled()) {
            if (userId == null && objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.downloadFileUrl(workspaceId, sessionId, userId, objectName, FileType.SHARE);
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[Share] Delete the specific file", notes = "[공유 파일] 파일을 삭제")
    @DeleteMapping(value = "file/share/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<FileDeleteResponse>> deleteShareFileRequestHandler(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId,
        @RequestParam("leaderUserId") String leaderUserId,
        @RequestParam("objectName") String objectName
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: DELETE "
                + REST_PATH + "::"
                + "workspaceId:" + (workspaceId != null ? workspaceId : "{}") + "/"
                + "sessionId:" + (sessionId != null ? sessionId : "{}") + "/"
                + "userId:" + (leaderUserId != null ? sessionId : "{}") + "/"
                + "objectName:" + (objectName != null ? objectName : "{}"),
            "deleteFileRequestHandler"
        );
        ApiResponse<FileDeleteResponse> responseData;

        if (remoteStorageProperties.isEnabled()) {
            if (leaderUserId == null || objectName == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.removeShareFile(workspaceId, sessionId, leaderUserId, objectName, FileType.SHARE);
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "[Share] Delete the specific files", notes = "[공유 파일] 파일을 전체 삭제")
    @DeleteMapping(value = "files/share/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<FileDeleteResponse>> deleteShareFilesRequestHandler(
        @PathVariable("workspaceId") String workspaceId,
        @PathVariable("sessionId") String sessionId,
        @RequestParam("userId") String leaderUserId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: DELETE "
                + REST_PATH + "::"
                + "workspaceId:" + (workspaceId != null ? workspaceId : "{}") + "/"
                + "sessionId:" + (sessionId != null ? sessionId : "{}") + "/"
                + "leaderUserId:" + (leaderUserId != null ? leaderUserId : "{}"),
            "deleteShareFilesRequestHandler"
        );
        ApiResponse<FileDeleteResponse> responseData;

        if (remoteStorageProperties.isEnabled()) {
            if (leaderUserId == null) {
                throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
            }
            responseData = fileService.removeShareFiles(workspaceId, sessionId, leaderUserId, FileType.SHARE);
        } else {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        return ResponseEntity.ok(responseData);
    }

}
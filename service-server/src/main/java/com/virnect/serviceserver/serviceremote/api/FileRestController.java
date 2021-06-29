package com.virnect.serviceserver.serviceremote.api;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
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
import com.virnect.data.dto.response.file.FileStorageInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.global.config.property.RemoteStorageProperties;
import com.virnect.serviceserver.serviceremote.application.FileService;
import com.virnect.data.dto.request.file.FileUploadRequest;
import com.virnect.data.dto.request.file.RecordFileUploadRequest;
import com.virnect.data.dto.request.file.RoomProfileUpdateRequest;
import com.virnect.data.dto.request.PageRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.file.FileDeleteResponse;
import com.virnect.data.dto.response.file.FileDetailInfoListResponse;
import com.virnect.data.dto.response.file.FileInfoListResponse;
import com.virnect.data.dto.response.file.FilePreSignedResponse;
import com.virnect.data.dto.response.file.FileUploadResponse;
import com.virnect.data.dto.response.file.RoomProfileUpdateResponse;
import com.virnect.data.dto.response.file.ShareFileInfoListResponse;
import com.virnect.data.dto.response.file.ShareFileUploadResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class FileRestController {

    private static final String TAG = FileRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/file";

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
                + fileUploadRequest.toString(),
            "fileUploadRequestHandler"
        );
        if (result.hasErrors()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<FileUploadResponse> responseData = fileService.uploadFile(fileUploadRequest, FileType.FILE);
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
                + recordFileUploadRequest.toString(),
            "recordFileUploadRequestHandler"
        );
        if (result.hasErrors()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<FileUploadResponse> responseData = fileService.uploadRecordFile(recordFileUploadRequest);
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
                + workspaceId + "/"
                + sessionId + "::"
                + roomProfileUpdateRequest.toString(),
            "profileUploadRequestHandler"
        );
        if (result.hasErrors()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<RoomProfileUpdateResponse> responseData = fileService.profileUpload(
            workspaceId,
            sessionId,
            roomProfileUpdateRequest
        );
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
                + workspaceId + "/"
                + sessionId,
            "profileDeleteRequestHandler"
        );
        if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
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
                + workspaceId + "/"
                + sessionId,
            "fileDownloadUrlRequestHandler"
        );
        if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId)
            || StringUtils.isBlank(userId) || StringUtils.isBlank(objectName)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<FilePreSignedResponse> responseData = fileService.downloadFileUrl(
            workspaceId,
            sessionId,
            userId,
            objectName
        );
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
                + workspaceId + "/"
                + sessionId,
            "recordFileDownloadUrlRequestHandler"
        );
        if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId)
            || StringUtils.isBlank(userId) || StringUtils.isBlank(objectName)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<FilePreSignedResponse> responseData = fileService.downloadRecordFileUrl(
            workspaceId,
            sessionId,
            userId,
            objectName
        );
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
                + "workspaceId:" + workspaceId + "/"
                + "sessionId:" + sessionId + "/"
                + "userId:" + userId + "/"
                + "deleted:" + deleted,
            "getFileList"
        );
        if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<FileInfoListResponse> responseData = fileService.getFileInfoList(
            workspaceId,
            sessionId,
            deleted,
            pageRequest.ofSortBy()
        );
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
                + "workspaceId:" + workspaceId + "/"
                + "sessionId:" + sessionId + "/"
                + "userId:" + userId + "/"
                + "deleted:" + deleted,
            "getDetailFileList"
        );
        if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(userId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<FileDetailInfoListResponse> responseData = fileService.getRecordFileInfoList(
            workspaceId,
            sessionId,
            userId,
            deleted,
            pageRequest.ofSortBy()
        );
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
                + "workspaceId:" + workspaceId + "/"
                + "sessionId:" + sessionId + "/"
                + "userId:" + userId + "/"
                + "objectName:" + objectName,
            "deleteFileRequestHandler"
        );
        if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId)
            || StringUtils.isBlank(userId) || StringUtils.isBlank(objectName)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<FileDeleteResponse> responseData = fileService.removeFile(
            workspaceId,
            sessionId,
            userId,
            objectName
        );
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
                + "objectName:" + objectName,
            "fileDownloadUrlRequestHandler"
        );
        if (StringUtils.isBlank(objectName)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<String> responseData = fileService.downloadGuideFileUrl(objectName);
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
    ) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        LogMessage.formedInfo(
            TAG,
            "REST API: POST "
                + REST_PATH + "::"
                + fileUploadRequest.toString(),
            "shareFileUploadRequestHandler"
        );
        if (result.hasErrors()) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<ShareFileUploadResponse> responseData = fileService.uploadShareFile(fileUploadRequest);
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
                + "workspaceId:" + workspaceId + "/"
                + "sessionId:" + sessionId + "/"
                + "deleted:" + deleted,
            "getShareFileList"
        );
        if (Strings.isBlank(workspaceId)|| Strings.isBlank(sessionId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<ShareFileInfoListResponse> responseData = fileService.getShareFileInfoList(
            workspaceId,
            sessionId,
            paging,
            pageRequest.ofSortBy()
        );
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
                + workspaceId + "/"
                + sessionId,
            "shareFileDownloadUrlRequestHandler"
        );
        if (Strings.isBlank(userId)|| Strings.isBlank(objectName)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<FilePreSignedResponse> responseData = fileService.downloadFileUrl(
            workspaceId,
            sessionId,
            userId,
            objectName
        );
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
                + "workspaceId:" + workspaceId + "/"
                + "sessionId:" + sessionId + "/"
                + "userId:" + leaderUserId + "/"
                + "objectName:" + objectName,
            "deleteShareFileRequestHandler"
        );
        if (StringUtils.isBlank(workspaceId) || StringUtils.isBlank(sessionId) || StringUtils.isBlank(leaderUserId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        ApiResponse<FileDeleteResponse> responseData = fileService.removeShareFile(
            workspaceId,
            sessionId,
            leaderUserId,
            objectName
        );
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
                + "workspaceId:" + workspaceId + "/"
                + "sessionId:" + sessionId + "/"
                + "leaderUserId:" + leaderUserId,
            "deleteShareFilesRequestHandler"
        );
        if (!remoteStorageProperties.isEnabled()) {
            throw new RestServiceException(ErrorCode.ERR_STORAGE_NOT_SUPPORTED);
        }
        if (StringUtils.isBlank(leaderUserId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<FileDeleteResponse> responseData = fileService.removeShareFiles(
            workspaceId,
            sessionId,
            leaderUserId,
            FileType.SHARE
        );
        return ResponseEntity.ok(responseData);
    }

    @ApiOperation(value = "Storage capacity information", notes = "리모트에서 사용 스토리지 용량 확인")
    @GetMapping(value = "file/storage/capacity/{workspaceId}")
    public ResponseEntity<ApiResponse<FileStorageInfoResponse>> checkRemoteStorageCapacity(
        @PathVariable("workspaceId") String workspaceId
    ) {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET
                + REST_PATH + "::"
                + "workspaceId:" + workspaceId,
            "checkRemoteStorageCapacity"
        );
        if (StringUtils.isBlank(workspaceId)) {
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<FileStorageInfoResponse> responseData = fileService.checkRemoteStorageCapacity(workspaceId);
        return ResponseEntity.ok(responseData);
    }

}
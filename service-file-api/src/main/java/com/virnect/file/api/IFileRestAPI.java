package com.virnect.file.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.dto.request.PageRequest;
import com.virnect.file.dto.request.FileUploadRequest;
import com.virnect.file.dto.request.RecordFileUploadRequest;
import com.virnect.file.dto.request.RoomProfileUpdateRequest;
import com.virnect.file.dto.response.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.IOException;

/**
 * Note that the values MUST be lower case.
 * path:: paramtype suggests that the type of implicitparam goes into requestUri
 * query:: means it is a type of url query params
 * body:: denotes it should be read from request body (payload)
 * header:: denotes the parameters coming in request header
 * form:: denotes field corresponds to the form parameter.
 */
@RequestMapping("/remote")
public interface IFileRestAPI {

    @ApiOperation(value = "Upload file", notes = "파일 식별자를 서버에서 발급하며, 식별자는 업로드 완료 후 반환됨.\n파일 첨부시 사용")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "48acaade22f3e2bba74bb6f1c44389a9"),
            @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "form", required = true, defaultValue = "ses_JRG7p1fFox"),
            @ApiImplicitParam(name = "userId", value = "업로드 사용자 고유 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "4218059539d944fca0a27fc5a57ce05b"),
            @ApiImplicitParam(name = "file", value = "업로드 파일", dataType = "__file", paramType = "form", required = true),
    })
    @PostMapping(value = "file/upload", headers = {"content-type=multipart/*"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<FileUploadResponse>> fileUploadRequestHandler(
            @ModelAttribute @Valid FileUploadRequest fileUploadRequest,
            BindingResult result);

    @ApiOperation(value = "Upload local record file", notes = "로컬 녹화 파일을 업로드 합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "48acaade22f3e2bba74bb6f1c44389a9"),
            @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "form", required = true, defaultValue = "ses_JRG7p1fFox"),
            @ApiImplicitParam(name = "userId", value = "업로드 사용자 고유 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "4218059539d944fca0a27fc5a57ce05b"),
            @ApiImplicitParam(name = "file", value = "업로드 파일", dataType = "__file", paramType = "form", required = true),
    })
    @PostMapping(value = "record/upload", headers = {"content-type=multipart/*"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<FileUploadResponse>> recordFileUploadRequestHandler(
            @ModelAttribute @Valid RecordFileUploadRequest recordFileUploadRequest,
            BindingResult result);

    @ApiOperation(value = "Update a Remote Room profile image file", notes = "원격협업 방 프로필을 업데이트 합니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "변경할 프로필 이미지", name = "profile", paramType = "form", dataType = "__file", required = true)
    })
    @PostMapping(value = "file/{workspaceId}/{sessionId}/profile", headers = {"content-type=multipart/*"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<RoomProfileUpdateResponse>> profileUploadRequestHandler(
            @ModelAttribute @Valid RoomProfileUpdateRequest roomProfileUpdateRequest,
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            BindingResult result);

    @ApiOperation(value = "Delete a Remote Room profile image file", notes = "원격협업 방 프로필을 삭제합니다.")
    @DeleteMapping(value = "file/{workspaceId}/{sessionId}/profile")
    ResponseEntity<ApiResponse<ResultResponse>> profileDeleteRequestHandler(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId);

    @ApiOperation(value = "Get URL to download file", notes = "파일 다운로드 URL을 받습니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "다운로드 사용자 고유 식별자", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "objectName", value = "다운로드 파일 고유 이름", dataType = "string", paramType = "query", required = true),
    })
    @GetMapping(value = "file/download/url/{workspaceId}/{sessionId}")
    ResponseEntity<ApiResponse<FilePreSignedResponse>> fileDownloadUrlRequestHandler(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "sessionId") String sessionId,
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "objectName") String objectName
    ) throws IOException;

    @ApiOperation(value = "Get URL to download record file", notes = "레코드 파일 다운로드 URL을 받습니다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "다운로드 사용자 고유 식별자", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "objectName", value = "다운로드 파일 고유 이름", dataType = "string", paramType = "query", required = true),
    })
    @GetMapping(value = "file/record/download/url/{workspaceId}/{sessionId}")
    ResponseEntity<ApiResponse<FilePreSignedResponse>> recordFileDownloadUrlRequestHandler(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "sessionId") String sessionId,
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "objectName") String objectName
    ) throws IOException;

    @ApiOperation(value = "Load Room File List", notes = "원격협업에서 등록된 파일 목록을 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(createdDate, ASC or DESC)", paramType = "query", defaultValue = "createdDate, DESC"),
            @ApiImplicitParam(name = "deleted", value = "삭제 파일 필터 옵션 (YES, NO)", dataType = "boolean", defaultValue = "false"),
    })
    @GetMapping("file")
    ResponseEntity<ApiResponse<FileInfoListResponse>> getFileList(
            @RequestParam(value = "workspaceId") String workspaceId,
            @RequestParam(value = "sessionId") String sessionId,
            @RequestParam(name = "userId") String userId,
            @RequestParam(value = "deleted", required = false, defaultValue = "false") boolean deleted,
            @ApiIgnore PageRequest pageRequest);

    @ApiOperation(value = "Load Room Record File Detail Information List", notes = "원격협업에서 등록된 로컬 녹화 파일 목록을 상세 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "size", value = "페이징 사이즈(최대 20)", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(createdDate, ASC or DESC)", paramType = "query", defaultValue = "createdDate, DESC"),
            @ApiImplicitParam(name = "deleted", value = "삭제 파일 필터 옵션 (YES, NO)", dataType = "boolean", defaultValue = "false"),
    })
    @GetMapping(value = "file/record/info")
    ResponseEntity<ApiResponse<FileDetailInfoListResponse>> getDetailFileList(
            @RequestParam(value = "workspaceId") String workspaceId,
            @RequestParam(value = "sessionId", required = false) String sessionId,
            @RequestParam(name = "userId") String userId,
            @RequestParam(value = "deleted", required = false, defaultValue = "false") boolean deleted,
            @ApiIgnore PageRequest pageRequest);


    @ApiOperation(value = "Delete the specific file", notes = "파일을 삭제")
    @DeleteMapping(value = "file/{workspaceId}/{sessionId}")
    ResponseEntity<ApiResponse<FileDeleteResponse>> deleteFileRequestHandler(
            @PathVariable("workspaceId") String workspaceId,
            @PathVariable("sessionId") String sessionId,
            @RequestParam("userId") String userId,
            @RequestParam("objectName") String objectName);
}

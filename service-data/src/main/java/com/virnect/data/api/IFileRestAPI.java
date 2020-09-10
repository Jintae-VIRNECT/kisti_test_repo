package com.virnect.data.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.dto.file.request.FileDownloadRequest;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.dto.file.response.FileDeleteResponse;
import com.virnect.data.dto.file.response.FileInfoListResponse;
import com.virnect.data.dto.file.response.FilePreSignedResponse;
import com.virnect.data.dto.file.response.FileUploadResponse;
import com.virnect.data.dto.request.PageRequest;
import com.virnect.data.dto.request.RoomProfileUpdateRequest;
import com.virnect.data.dto.response.ResultResponse;
import com.virnect.data.dto.response.RoomProfileUpdateResponse;
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
import java.util.List;

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

    @ApiOperation(value = "Upload file", notes = "컨텐츠 식별자를 서버에서 발급하며, 식별자는 업로드 완료 후 반환됨.\n컨텐츠 파일명은 컨텐츠 식별자와 동일한 파일명으로 저장.")
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

    @ApiOperation(value = "Download file", notes = "파일을 다운로드.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "userId", value = "다운로드 사용자 고유 식별자", dataType = "string", paramType = "query", required = true),
            @ApiImplicitParam(name = "objectName", value = "다운로드 파일 고유 이름", dataType = "string", paramType = "query", required = true),
    })
    @GetMapping(value = "file/download/{workspaceId}/{sessionId}")
    ResponseEntity<byte[]> fileDownloadRequestHandler(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "sessionId") String sessionId,
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "objectName") String objectName
    ) throws IOException;


    @ApiOperation(value = "Get URL to download ", notes = "파일 다운로드 URL을 받습니다.")
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

    @ApiOperation(value = "Load Room File List", notes = "원격협업에서 등록된 파일 목록을 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(Index 0 부터 시작)", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
            @ApiImplicitParam(name = "deleted", value = "삭제 파일 필터 옵션 (YES, NO)", dataType = "boolean", defaultValue = "false"),
    })
    @GetMapping("file")
    ResponseEntity<ApiResponse<FileInfoListResponse>> getFileList(
            @RequestParam(value = "workspaceId") String workspaceId,
            @RequestParam(value = "sessionId") String sessionId,
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

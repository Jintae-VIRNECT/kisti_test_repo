package com.virnect.data.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.dto.file.response.FileUploadResponse;
import com.virnect.data.dto.request.PageRequest;
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

@RequestMapping("/remote")
public interface IFileRestAPI {

    @ApiOperation(value = "Upload file", notes = "컨텐츠 식별자를 서버에서 발급하며, 식별자는 업로드 완료 후 반환됨.\n컨텐츠 파일명은 컨텐츠 식별자와 동일한 파일명으로 저장.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "48254844-235e-4421-b713-4ea682994a98"),
            @ApiImplicitParam(name = "sessionId", value = "원격협업 세션", dataType = "string", paramType = "form", required = true, defaultValue = "ses_Qqak8KEqPH"),
            @ApiImplicitParam(name = "userId", value = "업로드 사용자 고유 식별자(로그인 성공 응답으로 서버에서 사용자 데이터를 내려줌)", dataType = "string", paramType = "form", required = true, defaultValue = "48254844-235e-4421-b713-4ea682994a98"),
            @ApiImplicitParam(name = "file", value = "업로드 파일", dataType = "__file", paramType = "form", required = true),
    })
    @PostMapping(value = "file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<FileUploadResponse>> fileUploadRequestHandler(
            @ModelAttribute @Valid FileUploadRequest fileUploadRequest,
            BindingResult result);

    /*@ApiOperation(value = "워크스테이션 기준 사용자별 업로드한 컨텐츠 수 ", notes = "워크스페이션 내의 내가 등록한 컨텐츠의 목록을 조회함.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "userUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "")
    })
    @GetMapping("file/upload")
    public ResponseEntity<ApiResponse<List<ContentCountResponse>>> countContents(
            @RequestParam(value = "workspaceUUID") String workspaceUUID,
            @RequestParam(value = "userUUIDList") List<String> userUUIDList);*/


    /*@ApiOperation(value = "원격협업 방 파일 목록 조회", notes = "원격협업 방의 파일 목록을 조회.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceId", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "검색어(컨텐츠명/사용자명)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(콘텐츠 이름:name / 타겟유형:type(확인필요) / 콘텐츠 등록일:createdDate / 공유상태:shared)", paramType = "query", defaultValue = "createdDate,desc"),
            @ApiImplicitParam(name = "shareds", value = "공유 필터 옵션 (ALL, YES, NO)", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "converteds", value = "컨텐츠의 공정 전환 여부(ALL, YES, NO)", dataType = "string", paramType = "query", defaultValue = "ALL")
    })
    @GetMapping(value = "file")
    ResponseEntity<ApiResponse<ContentInfoListResponse>> getFileList(
            @RequestParam(value = "workspaceId", required = true) String workspaceId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "shareds", required = false, defaultValue = "ALL") String shareds,
            @RequestParam(value = "converteds", required = false, defaultValue = "ALL") String converteds,
            @ApiIgnore PageRequest pageable);

    @ApiOperation(value = "내 컨텐츠 목록 조회", notes = "워크스페이션 내의 내가 등록한 컨텐츠의 목록을 조회함.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "검색어(컨텐츠명)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
            @ApiImplicitParam(name = "shareds", value = "공유 필터 옵션 (ALL, YES, NO)", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "userUUID", value = "사용자 식별자", dataType = "string", paramType = "path", required = true, defaultValue = ""),
            @ApiImplicitParam(name = "converteds", value = "컨텐츠의 공정 전환 여부(ALL, YES, NO)", dataType = "string", paramType = "query", defaultValue = "ALL")
    })
    @GetMapping("file/{userUUID}")
    ResponseEntity<ApiResponse<ContentInfoListResponse>> getUserContentList(
            @PathVariable(value = "userUUID") String userUUID
            , @RequestParam(value = "search", required = false) String search
            , @RequestParam(value = "shareds", defaultValue = "ALL") String shareds
            , @RequestParam(value = "converteds", defaultValue = "ALL") String converteds
            , @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID
            , @ApiIgnore PageRequest pageable);





    @ApiOperation(value = "Duplicate file", notes = "컨텐츠 파일을 복제 후 컨텐츠 신규 생성. 공정서버에서 컨텐츠를 이용한 공정 생성에 사용되는 API임.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "testUUID"),
            @ApiImplicitParam(name = "userUUID", value = "요청 사용자의 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @PostMapping("/duplicate/{contentUUID}")
    ResponseEntity<ApiResponse<ContentUploadResponse>> contentDuplicateHandler(
            @PathVariable("contentUUID") String contentUUID,
            @RequestParam(value = "workspaceUUID") String workspaceUUID,
            @RequestParam(value = "userUUID") String userUUID);

    @ApiOperation(value = "컨텐츠 식별자로 컨텐츠 다운로드", notes = "컨텐츠 식별자를 통해 컨텐츠를 다운로드.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "memberUUID", value = "다운받는 사용자 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("file/download/")
    ResponseEntity<byte[]> contentDownloadForUUIDRequestHandler(
            @PathVariable("contentUUID") String contentUUID,
            @RequestParam(value = "memberUUID") String memberUUID) throws IOException;

    @ApiOperation(value = "타겟 데이터로 컨텐츠 다운로드", notes = "컨텐츠 식별자 또는 타겟 데이터를 통해 컨텐츠를 다운로드. 컨텐츠 식별자, 타겟 데이터 둘 중 하나는 필수.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터", dataType = "string", paramType = "query", required = true, defaultValue = "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d"),
            @ApiImplicitParam(name = "memberUUID", value = "다운받는 사용자 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("/download")
    ResponseEntity<byte[]> contentDownloadRequestForTargetHandler(
            @RequestParam(value = "targetData") String targetData,
            @RequestParam(value = "memberUUID") String memberUUID) throws IOException;

    @ApiOperation(value = "타겟 데이터로 컨텐츠 다운로드(K앱시스트용)", notes = "컨텐츠 식별자 또는 타겟 데이터를 통해 컨텐츠를 다운로드. 컨텐츠 식별자, 타겟 데이터 둘 중 하나는 필수.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터", dataType = "string", paramType = "path", required = true, defaultValue = "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d"),
            @ApiImplicitParam(name = "memberUUID", value = "다운받는 사용자 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("/download/targetData/{targetData}")
    ResponseEntity<byte[]> contentDownloadRequestForTargetHandler_temp(
            @PathVariable("targetData") String targetData
            , @RequestParam(value = "memberUUID") String memberUUID) throws IOException {
        log.info("[DOWNLOAD] USER: [{}] => targetData: [{}]", memberUUID, targetData);
        if (targetData.isEmpty() || memberUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        return this.contentService.contentDownloadForTargetHandler_temp(targetData, memberUUID);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .contentLength(resource.getFile().length())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
    }

    @ApiOperation(value = "컨텐츠 삭제 요청")
    @DeleteMapping
    ResponseEntity<ApiResponse<ContentDeleteListResponse>> contentDeleteRequestHandler(
            @RequestBody @Valid ContentDeleteRequest contentDeleteRequest, BindingResult result) {
        if (result.hasErrors()) {
            log.error("REQUEST BINDING contentDeleteRequest: {}", contentDeleteRequest.toString());
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentDeleteListResponse> responseMessage = this.contentService.contentDelete(contentDeleteRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "컨텐츠 상세 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", required = true, paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    })
    @GetMapping("/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentInfoResponse>> getContentInfo(@PathVariable("contentUUID") String contentUUID) {
        if (contentUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentInfoResponse> responseMessage = this.contentService.getContentInfo(contentUUID);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "워크스페이스 사용 용량 및 다운로드 횟수 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "워크스페이스 식별자", name = "workspaceId", required = true, paramType = "path", example = "4d6eab0860969a50acbfa4599fbb5ae8")
    })
    @GetMapping("/resources/report/{workspaceId}")
    public ResponseEntity<ApiResponse<ContentResourceUsageInfoResponse>> getContentResourceUsageInfoRequest(@PathVariable("workspaceId") String workspaceId) {
        if (StringUtils.isEmpty(workspaceId)) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentResourceUsageInfoResponse> responseMessage = contentService.getContentResourceUsageInfo(workspaceId);
        return ResponseEntity.ok(responseMessage);
    }*/
}

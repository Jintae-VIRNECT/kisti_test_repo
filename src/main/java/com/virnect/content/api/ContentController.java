package com.virnect.content.api;

import com.virnect.content.application.ContentService;
import com.virnect.content.domain.TargetType;
import com.virnect.content.domain.Types;
import com.virnect.content.domain.YesOrNo;
import com.virnect.content.dto.request.ContentPropertiesMetadataRequest;
import com.virnect.content.dto.request.ContentUpdateRequest;
import com.virnect.content.dto.request.ContentUploadRequest;
import com.virnect.content.dto.response.*;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.PageRequest;
import com.virnect.content.global.error.ErrorCode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-14
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Api Controller
 */
@CrossOrigin
@Slf4j
@Controller
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @ApiOperation(value = "콘텐츠 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "검색어(콘텐츠명/사용자명)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
            @ApiImplicitParam(name = "shared", value = "공유 필터 옵션 (ALL, YES, NO)", paramType = "query", defaultValue = "ALL")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ContentInfoListResponse>> getContentList(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "shared", defaultValue = "ALL") String shareds,
            @ApiIgnore PageRequest pageable) {
        ApiResponse<ContentInfoListResponse> responseMessage = this.contentService.getContentList(workspaceUUID, null, search, shareds, pageable.of());
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "내 콘텐츠 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "검색어(콘텐츠명)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
            @ApiImplicitParam(name = "shareds", value = "공유 필터 옵션 (ALL, YES, NO)", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "userUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "")
    })
    @GetMapping("/my/{userUUID}")
    public ResponseEntity<ApiResponse<ContentInfoListResponse>> getUserContentList(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "shareds", defaultValue = "ALL") String shareds,
            @RequestParam(value = "userUUID") String userUUID,
            @ApiIgnore PageRequest pageable) {
        if (userUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentInfoListResponse> responseMessage = this.contentService.getContentList(workspaceUUID, userUUID, search, shareds, pageable.of());
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "콘텐츠 파일 업로드", notes = "컨텐츠 식별자를 서버에서 발급하며, 식별자는 업로드 완료 후 반환됨.\n컨텐츠 파일명은 컨텐츠 식별자와 동일한 파일명으로 저장.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터(URL encoding된 데이터)", dataType = "string", paramType = "form", defaultValue = "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d"),
            @ApiImplicitParam(name = "targetType", value = "타겟 종류(QR)", dataType = "string", paramType = "form", defaultValue = "QR"),
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "48254844-235e-4421-b713-4ea682994a98"),
            @ApiImplicitParam(name = "content", value = "업로드 콘텐츠 파일", dataType = "__file", paramType = "form", required = true),
            @ApiImplicitParam(name = "contentType", value = "콘텐츠 종류(AUGMENTED_REALITY(default), ASSISTED_REALITY, CROCESS_PLATFORM, MIXED_REALITY)", dataType = "string", paramType = "form", required = true, defaultValue = "AUGMENTED_REALITY"),
            @ApiImplicitParam(name = "name", value = "콘텐츠 명", dataType = "string", paramType = "form", required = true, defaultValue = "test_content"),
            @ApiImplicitParam(name = "metadata", value = "공정 메타데이터", dataType = "string", paramType = "form", required = true, defaultValue = "{\"contents\":{\"id\":\"b5db6bb8-9976-4865-859c-1b98e57a3dc5\",\"name\":\"SampleContent\",\"managerUUID\":\"\",\"subProcessTotal\":1,\"sceneGroups\":[{\"id\":\"5f43e519-0f18-46c1-947e-198f801bf3cc\",\"priority\":1,\"name\":\"SceneGroup\",\"jobTotal\":4,\"scenes\":[{\"id\":\"0292b07c-414a-499d-82ee-ad14e2e40dc1\",\"priority\":1,\"name\":\"Scene\",\"subJobTotal\":1,\"reportObjects\":[]},{\"id\":\"7cfda7c8-3a62-404a-9375-b30c23e45637\",\"priority\":2,\"name\":\"Scene\",\"subJobTotal\":1,\"reportObjects\":[]},{\"id\":\"285c316d-d27c-4032-9cd0-638ab9f682e3\",\"priority\":3,\"name\":\"Scene\",\"subJobTotal\":7,\"reportObjects\":[{\"id\":\"e26735f0-3575-45ef-a9d5-4017ec4b01f1\",\"items\":[{\"id\":null,\"priority\":1,\"type\":\"TOGGLE\",\"title\":\"항목1\"},{\"id\":null,\"priority\":2,\"type\":\"INPUT_FIELD\",\"title\":\"항목2\"},{\"id\":null,\"priority\":3,\"type\":\"REPORT\",\"title\":\"항목3\"}]}]},{\"id\":\"c3604d08-cf2b-43f5-90df-b6b8715537d2\",\"priority\":4,\"name\":\"Scene\",\"subJobTotal\":1,\"reportObjects\":[]}]}]}}"),
            @ApiImplicitParam(name = "properties", value = "컨텐츠 속성 메타데이터", dataType = "string", paramType = "form", required = true, defaultValue = "{\"content\":[{\"PropertyInfo\":{\"ComponentName\":\"SceneGroup\",\"ComponentType\":\"SceneGroup\",\"identifier\":\"8b7860ef-7617-4c92-a272-50f4e60e127e\",\"sceneGroupDetail\":\"\",\"sceneGroupTitle\":\"\"},\"child\":[{\"PropertyInfo\":{\"ComponentName\":\"Scene\",\"ComponentType\":\"Scene\",\"identifier\":\"2f6b453a-f5b0-406e-8f45-04f222279f25\",\"sceneDetail\":\"\",\"sceneTitle\":\"\"},\"Transform\":{},\"child\":[{\"PropertyInfo\":{\"ComponentName\":\"Text\",\"ComponentType\":\"Text\",\"alignment\":\"MiddleLeft\",\"backGround\":\"TextBoxBg/0$1|1|1|1$0\",\"color\":\"1|1|1|1\",\"font\":\"NotoSansCJKkr-Bold (UnityEngine.Font)\",\"fontSize\":\"32\",\"identifier\":\"978a9d27-de13-4bfc-8a25-644e3b446c9a\",\"shadow\":\"0\",\"text\":\"텍스트를 입력해주세요\"},\"Transform\":{\"ScreenMode\":\"World\",\"screenPosition\":\"0|0|0\",\"screenRotation\":\"0|0|0\",\"screenScale\":\"1|1|1\",\"worldPosition\":\"0.2940716|0|0\",\"worldRotation\":\"0|0|0\",\"worldScale\":\"1|1|1\"}}]}]}]}"),
            @ApiImplicitParam(name = "userUUID", value = "업로드 사용자 고유 식별자(로그인 성공 응답으로 서버에서 사용자 데이터를 내려줌)", dataType = "string", paramType = "form", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608"),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ContentUploadResponse>> contentFileUploadRequestHandler(@ModelAttribute @Valid ContentUploadRequest uploadRequest, BindingResult result) {
        if (result.hasErrors()) {
            log.info("[ContentUploadRequest] => [{}]", uploadRequest);
            log.error("[FIELD ERROR] => [{}] [{}]", result.getFieldError().getField(), result.getFieldError().getDefaultMessage());
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentUploadResponse> uploadResponse = this.contentService.contentUpload(uploadRequest);
        return ResponseEntity.ok(uploadResponse);
    }
    
    @ApiOperation(value = "컨텐츠 복제", notes = "컨텐츠 파일을 복제 후 컨텐츠 신규 생성. 공정서버에서 컨텐츠를 이용한 공정 생성에 사용되는 API임.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "testUUID"),
            @ApiImplicitParam(name = "userUUID", value = "요청 사용자의 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @PostMapping("/duplicate/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentUploadResponse>> contentDuplicateHandler(
            @PathVariable("contentUUID") String contentUUID,
            @RequestParam(value = "workspaceUUID") String workspaceUUID,
            @RequestParam(value = "userUUID") String userUUID) {
        if (contentUUID.isEmpty() || workspaceUUID.isEmpty() || userUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentUploadResponse> uploadResponse = this.contentService.contentDuplicate(contentUUID, workspaceUUID, userUUID);
        return ResponseEntity.ok(uploadResponse);
    }

    @ApiOperation(value = "컨텐츠 식별자로 컨텐츠 다운로드", notes = "컨텐츠 식별자를 통해 컨텐츠를 다운로드.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "컨텐츠 식별자", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "memberUUID", value = "다운받는 사용자 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("/download/contentUUID/{contentUUID}")
    public ResponseEntity<Resource> contentDownloadForUUIDRequestHandler(
            @PathVariable("contentUUID") String contentUUID
            , @RequestParam(value = "memberUUID") String memberUUID) {
        log.info("[DOWNLOAD] USER: [{}] => contentUUID: [{}]", memberUUID, contentUUID);
        if (contentUUID.isEmpty() || memberUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        Resource resource = this.contentService.contentDownloadForUUIDhandler(contentUUID, memberUUID);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @ApiOperation(value = "타겟 데이터로 컨텐츠 다운로드", notes = "컨텐츠 식별자 또는 타겟 데이터를 통해 컨텐츠를 다운로드. 컨텐츠 식별자, 타겟 데이터 둘 중 하나는 필수.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터", dataType = "string", paramType = "path", required = true, defaultValue = "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d"),
            @ApiImplicitParam(name = "memberUUID", value = "다운받는 사용자 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("/download/targetData/{targetData}")
    public ResponseEntity<Resource> contentDownloadRequestForTargetHandler(
            @PathVariable("targetData") String targetData
            , @RequestParam(value = "memberUUID") String memberUUID) {
        log.info("[DOWNLOAD] USER: [{}] => targetData: [{}]", memberUUID, targetData);
        if (targetData.isEmpty() || memberUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        Resource resource = this.contentService.contentDownloadForTargethandler(targetData, memberUUID);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @ApiOperation(value = "콘텐츠 파일 업데이트")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "콘텐츠 고유 번호", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "content", value = "수정할 콘텐츠 ares 파일", dataType = "__file", paramType = "form", required = true),
            @ApiImplicitParam(name = "name", value = "수정할 콘텐츠 명", dataType = "string", paramType = "form", required = true),
            @ApiImplicitParam(name = "metadata", value = "수정할 콘텐츠 메타데이터", dataType = "string", paramType = "form", required = true),
            @ApiImplicitParam(name = "properties", value = "컨텐츠 속성 메타데이터", dataType = "string", paramType = "form", required = true, defaultValue = "{\"content\":[{\"PropertyInfo\":{\"ComponentName\":\"SceneGroup\",\"ComponentType\":\"SceneGroup\",\"identifier\":\"8b7860ef-7617-4c92-a272-50f4e60e127e\",\"sceneGroupDetail\":\"\",\"sceneGroupTitle\":\"\"},\"child\":[{\"PropertyInfo\":{\"ComponentName\":\"Scene\",\"ComponentType\":\"Scene\",\"identifier\":\"2f6b453a-f5b0-406e-8f45-04f222279f25\",\"sceneDetail\":\"\",\"sceneTitle\":\"\"},\"Transform\":{},\"child\":[{\"PropertyInfo\":{\"ComponentName\":\"Text\",\"ComponentType\":\"Text\",\"alignment\":\"MiddleLeft\",\"backGround\":\"TextBoxBg/0$1|1|1|1$0\",\"color\":\"1|1|1|1\",\"font\":\"NotoSansCJKkr-Bold (UnityEngine.Font)\",\"fontSize\":\"32\",\"identifier\":\"978a9d27-de13-4bfc-8a25-644e3b446c9a\",\"shadow\":\"0\",\"text\":\"텍스트를 입력해주세요\"},\"Transform\":{\"ScreenMode\":\"World\",\"screenPosition\":\"0|0|0\",\"screenRotation\":\"0|0|0\",\"screenScale\":\"1|1|1\",\"worldPosition\":\"0.2940716|0|0\",\"worldRotation\":\"0|0|0\",\"worldScale\":\"1|1|1\"}}]}]}]}"),
            @ApiImplicitParam(name = "userUUID", value = "수정 요청 사용자의 고유번호", dataType = "string", paramType = "form", required = true)
    })
    @PutMapping("/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentUploadResponse>> contentUpdateRequestHandler(
            @ModelAttribute @Valid ContentUpdateRequest updateRequestDto
            , @PathVariable("contentUUID") String contentUUID, BindingResult result) {
        if (result.hasErrors() || contentUUID.isEmpty()) {
            log.info("[ContentUpdateRequest] => [{}]", updateRequestDto.toString());
            log.error("[FIELD ERROR] => [{}] [{}]", result.getFieldError().getField(), result.getFieldError().getDefaultMessage());
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentUploadResponse> responseMessage = this.contentService.contentUpdate(contentUUID, updateRequestDto);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "콘텐츠 삭제 요청")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "콘텐츠 고유 번호 배열(ex : contentUUID=uuid,uuid,uuid,uuid)", allowMultiple = true, dataType = "array", paramType = "query", required = true),
            @ApiImplicitParam(name = "workerUUID", value = "삭제 요청 사용자 고유 번호", dataType = "string", paramType = "query", required = true)
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<ContentDeleteListResponse>> contentDeleteRequestHandler(
            @RequestParam(value = "contentUUID") String[] contentUUIDs
            , @RequestParam(value = "workerUUID") String workerUUID) {
        if (contentUUIDs.length < 1 || workerUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentDeleteListResponse> responseMessage = this.contentService.contentDelete(contentUUIDs, workerUUID);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "콘텐츠 메타데이터 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "콘텐츠 고유 번호", dataType = "string", paramType = "query")
    })
    @GetMapping("/metadata")
    public ResponseEntity<ApiResponse<MetadataInfoResponse>> getContentRawMetadata(@RequestParam(value = "contentUUID") String contentUUID) {
        if (contentUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<MetadataInfoResponse> responseMessage = this.contentService.getContentRawMetadata(contentUUID);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "씬그룹 목록 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", required = true, paramType = "query", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    })
    @GetMapping("/sceneGroups/metadata")
    public ResponseEntity<ApiResponse<SceneGroupInfoListResponse>> getContentSceneGroupInfoList(@RequestParam(value = "contentUUID") String contentUUID) {
        if (contentUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<SceneGroupInfoListResponse> responseMessage = this.contentService.getContentSceneGroups(contentUUID);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "컨텐츠 상세 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", required = true, paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    })
    @GetMapping("/info/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentInfoResponse>> getContentInfo(@PathVariable("contentUUID") String contentUUID) {
        if (contentUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentInfoResponse> responseMessage = this.contentService.getContentInfo(contentUUID);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "전체 컨텐츠 현황 정보 조회")
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<ContentStatisticResponse>> contentStatusInfoRequestHandler() {
        ApiResponse<ContentStatisticResponse> responseMessage = this.contentService.getContentStatusInfo();
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "컨텐츠 상세 정보 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", dataType = "string", required = true, paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29"),
            @ApiImplicitParam(name = "contentType", value = "콘텐츠 종류(AUGMENTED_REALITY(default), ASSISTED_REALITY, CROCESS_PLATFORM, MIXED_REALITY)", dataType = "string", paramType = "query", required = false, defaultValue = "AUGMENTED_REALITY"),
            @ApiImplicitParam(name = "shared", value = "컨텐츠 공유(YES, NO)", dataType = "string", paramType = "query", required = true, defaultValue = "NO"),
            @ApiImplicitParam(name = "userUUID", value = "요청 사용자의 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @PutMapping("/info/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentInfoResponse>> modifyContentInfo(
            @PathVariable("contentUUID") String contentUUID
            , @RequestParam(value = "contentType", defaultValue = "AUGMENTED_REALITY", required = false) Types contentType
            , @RequestParam(value = "shared", defaultValue = "NO") YesOrNo shared
            , @RequestParam(value = "userUUID") String userUUID
    ) {
        if (contentUUID.isEmpty() || userUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentInfoResponse> response = this.contentService.modifyContentInfo(contentUUID, shared, contentType, userUUID);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "컨텐츠 전환 수정", tags = "process server only")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", dataType = "string", required = true, paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29"),
            @ApiImplicitParam(name = "converted", value = "컨텐츠의 공정 전환 여부(YES, NO)", dataType = "string", paramType = "query", required = true, defaultValue = "NO")
    })
    @PutMapping("/convert/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentInfoResponse>> contentConvertHandler(
            @PathVariable("contentUUID") String contentUUID
            , @RequestParam(value = "converted", defaultValue = "NO") YesOrNo converted
    ) {
        if (contentUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentInfoResponse> response = this.contentService.setConverted(contentUUID, converted);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "작업에서 컨텐츠로의 전환", notes = "작업의 컨텐츠를 복제한 후 컨텐츠 목록에 노출. 중복 가능하며 컨텐츠 식별자가 신규발급됨.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "작업 식별자로 컨텐츠 파일을 판별.", dataType = "string", required = true, paramType = "path"),
            @ApiImplicitParam(name = "userUUID", value = "요청 사용자의 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("/convertTask/{taskId}")
    public ResponseEntity<ApiResponse<ContentUploadResponse>> taskToContentConvertHandler(
            @PathVariable("taskId") Long taskId
            , @RequestParam(value = "userUUID") String userUUID
    ) {
        if (taskId == null || userUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentUploadResponse> responseApiResponse = this.contentService.convertTaskToContent(taskId, userUUID);
        return ResponseEntity.ok(responseApiResponse);
    }

    @ApiOperation(value = "콘텐츠 속성 메타데이터 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", required = true, dataType = "string", paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29"),
            @ApiImplicitParam(name = "userUUID", value = "요청 사용자의 고유번호", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping("/properties/metadata/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentPropertiesResponse>> getContentPropertiesMetadata(@PathVariable("contentUUID") String contentUUID, @RequestParam(value = "userUUID") String userUUID) {
        if (contentUUID.isEmpty() || userUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentPropertiesResponse> responseMessage = this.contentService.getContentPropertiesMetadata(contentUUID, userUUID);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "콘텐츠 속성 메타데이터 수정", tags = "dev")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", required = true, dataType = "string", paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29"),
            @ApiImplicitParam(name = "properties", value = "컨텐츠 속성 메타데이터", dataType = "string", paramType = "form", required = true, defaultValue = "{\"content\":[{\"PropertyInfo\":{\"ComponentName\":\"SceneGroup\",\"ComponentType\":\"SceneGroup\",\"identifier\":\"8b7860ef-7617-4c92-a272-50f4e60e127e\",\"sceneGroupDetail\":\"\",\"sceneGroupTitle\":\"\"},\"child\":[{\"PropertyInfo\":{\"ComponentName\":\"Scene\",\"ComponentType\":\"Scene\",\"identifier\":\"2f6b453a-f5b0-406e-8f45-04f222279f25\",\"sceneDetail\":\"\",\"sceneTitle\":\"\"},\"Transform\":{},\"child\":[{\"PropertyInfo\":{\"ComponentName\":\"Text\",\"ComponentType\":\"Text\",\"alignment\":\"MiddleLeft\",\"backGround\":\"TextBoxBg/0$1|1|1|1$0\",\"color\":\"1|1|1|1\",\"font\":\"NotoSansCJKkr-Bold (UnityEngine.Font)\",\"fontSize\":\"32\",\"identifier\":\"978a9d27-de13-4bfc-8a25-644e3b446c9a\",\"shadow\":\"0\",\"text\":\"텍스트를 입력해주세요\"},\"Transform\":{\"ScreenMode\":\"World\",\"screenPosition\":\"0|0|0\",\"screenRotation\":\"0|0|0\",\"screenScale\":\"1|1|1\",\"worldPosition\":\"0.2940716|0|0\",\"worldRotation\":\"0|0|0\",\"worldScale\":\"1|1|1\"}}]}]}]}"),
            @ApiImplicitParam(name = "userUUID", value = "요청 사용자의 고유번호", required = true, dataType = "string", paramType = "form")
    })
    @PutMapping("/properties/metadata/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentPropertiesResponse>> contentPropertiesMetadataRequestHandler(
            @PathVariable("contentUUID") String contentUUID
            , @ModelAttribute @Valid ContentPropertiesMetadataRequest metadataRequest
            , BindingResult result) {
        if (contentUUID.isEmpty() || result.hasErrors()) {
            log.error("REQUEST BINDING contentUUID: {}, metadataRequest: {}", contentUUID, metadataRequest.toString());
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentPropertiesResponse> responseMessage = this.contentService.setContentPropertiesMetadata(contentUUID, metadataRequest);
        return ResponseEntity.ok(responseMessage);
    }
}

package com.virnect.content.api;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.content.application.ContentService;
import com.virnect.content.domain.YesOrNo;
import com.virnect.content.dto.request.ContentDeleteRequest;
import com.virnect.content.dto.request.ContentInfoRequest;
import com.virnect.content.dto.request.ContentUpdateRequest;
import com.virnect.content.dto.request.ContentUploadRequest;
import com.virnect.content.dto.response.ContentCountResponse;
import com.virnect.content.dto.response.ContentDeleteListResponse;
import com.virnect.content.dto.response.ContentInfoListResponse;
import com.virnect.content.dto.response.ContentInfoResponse;
import com.virnect.content.dto.response.ContentPropertiesResponse;
import com.virnect.content.dto.response.ContentResourceUsageInfoResponse;
import com.virnect.content.dto.response.ContentSecessionResponse;
import com.virnect.content.dto.response.ContentStatisticResponse;
import com.virnect.content.dto.response.ContentUploadResponse;
import com.virnect.content.dto.response.SceneGroupInfoListResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.PageRequest;
import com.virnect.content.global.error.ErrorCode;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-14
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Content Api Controller
 */

@Slf4j
@Controller
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @ApiOperation(value = "컨텐츠 목록 조회", notes = "컨텐츠의 목록을 조회. 워크스테이션은 옵션임.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "검색어(컨텐츠명/사용자명)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터(콘텐츠 이름:name / 타겟유형:type(확인필요) / 콘텐츠 등록일:createdDate / 공유상태:shared)", paramType = "query", defaultValue = "createdDate,desc"),
            @ApiImplicitParam(name = "shareds", value = "공유 필터 옵션 (ALL, YES, NO)", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "converteds", value = "컨텐츠의 공정 전환 여부(ALL, YES, NO)", dataType = "string", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "userUUID", value = "사용자 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "target", value = "컨텐츠의 타겟 타입(ALL, QR, VTarget)", dataType = "string", paramType = "query", defaultValue = "ALL"),
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ContentInfoListResponse>> getContentList(
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "shareds", required = false, defaultValue = "ALL") String shareds,
            @RequestParam(value = "converteds", required = false, defaultValue = "ALL") String converteds,
            @RequestParam(value = "userUUID", required = false) String userUUID,
            @RequestParam(value = "target", required = false, defaultValue = "ALL") String targetType,
            @ApiIgnore PageRequest pageable
    ) {
        ApiResponse<ContentInfoListResponse> responseMessage = this.contentService.getContentList(
                workspaceUUID, userUUID, search, shareds, converteds, pageable.of(), targetType);
        return ResponseEntity.ok(responseMessage);
    }

    //make, view에서 사용하고 있어서 못지움.
    @ApiOperation(value = "내 컨텐츠 목록 조회", notes = "워크스페이션 내의 내가 등록한 컨텐츠의 목록을 조회함.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "search", value = "검색어(컨텐츠명)", dataType = "string", allowEmptyValue = true, defaultValue = ""),
            @ApiImplicitParam(name = "size", value = "페이징 사이즈", dataType = "number", paramType = "query", defaultValue = "10"),
            @ApiImplicitParam(name = "page", value = "size 대로 나눠진 페이지를 조회할 번호(1부터 시작)", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "sort", value = "정렬 옵션 데이터", paramType = "query", defaultValue = "createdDate,desc"),
            @ApiImplicitParam(name = "shareds", value = "공유 필터 옵션 (ALL, YES, NO)", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "userUUID", value = "사용자 식별자", dataType = "string", paramType = "path", required = true, defaultValue = ""),
            @ApiImplicitParam(name = "converteds", value = "컨텐츠의 공정 전환 여부(ALL, YES, NO)", dataType = "string", paramType = "query", defaultValue = "ALL"),
            @ApiImplicitParam(name = "target", value = "컨텐츠의 타겟 타입(ALL, QR, VTarget)", dataType = "string", paramType = "query", defaultValue = "ALL")
    })
    @GetMapping("/my/{userUUID}")
    public ResponseEntity<ApiResponse<ContentInfoListResponse>> getUserContentList(
            @PathVariable(value = "userUUID") String userUUID,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "shareds", defaultValue = "ALL") String shareds,
            @RequestParam(value = "converteds", defaultValue = "ALL") String converteds,
            @RequestParam(value = "workspaceUUID", required = false) String workspaceUUID,
            @RequestParam(value = "target", required = false, defaultValue = "ALL") String targetType,
            @ApiIgnore PageRequest pageable
    ) {
        if (userUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentInfoListResponse> responseMessage = this.contentService.getContentList(
                workspaceUUID, userUUID, search, shareds, converteds, pageable.of(), targetType);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "워크스테이션 기준 사용자별 업로드한 컨텐츠 수 ", notes = "워크스페이션 내의 내가 등록한 컨텐츠의 목록을 조회함.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "userUUID", value = "사용자 식별자", dataType = "string", paramType = "query", required = true, defaultValue = "")
    })
    @GetMapping("/countContents")
    public ResponseEntity<ApiResponse<List<ContentCountResponse>>> countContents(
            @RequestParam(value = "workspaceUUID") String workspaceUUID,
            @RequestParam(value = "userUUIDList") List<String> userUUIDList
    ) {
        if (userUUIDList.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<List<ContentCountResponse>> responseMessage = this.contentService.countMyContents(
                workspaceUUID, userUUIDList);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "컨텐츠 파일 업로드", notes = "컨텐츠 식별자를 서버에서 발급하며, 식별자는 업로드 완료 후 반환됨.\n컨텐츠 파일명은 컨텐츠 식별자와 동일한 파일명으로 저장.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터(URL encoding된 데이터)", dataType = "string", paramType = "form", defaultValue = "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d"),
            @ApiImplicitParam(name = "targetType", value = "타겟 종류(QR, VTarget)", dataType = "string", paramType = "form", defaultValue = "QR"),
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", dataType = "string", paramType = "form", required = true, defaultValue = "48254844-235e-4421-b713-4ea682994a98"),
            @ApiImplicitParam(name = "content", value = "업로드 컨텐츠 파일", dataType = "__file", paramType = "form", required = true),
            @ApiImplicitParam(name = "contentType", value = "컨텐츠 종류(AUGMENTED_REALITY(default), ASSISTED_REALITY, CROCESS_PLATFORM, MIXED_REALITY)", dataType = "string", paramType = "form", required = true, defaultValue = "AUGMENTED_REALITY"),
            @ApiImplicitParam(name = "name", value = "컨텐츠 명", dataType = "string", paramType = "form", required = true, defaultValue = "test_content"),
            //@ApiImplicitParam(name = "metadata", value = "작업 메타데이터", dataType = "string", paramType = "form"),
            @ApiImplicitParam(name = "properties", value = "컨텐츠 속성 메타데이터", dataType = "string", paramType = "form", required = true, defaultValue = "{\"TargetID\":\"97802bc0-cc5e-436b-bdb3-2ba55f390406\",\"TargetSize\":10,\"PropertyInfo\":{\"c1eab583-8c05-4e4a-a590-484d5e94db7e\":{\"PropertyInfo\":{\"sceneGroupTitle\":\"\",\"sceneGroupDetail\":\"\",\"ComponentName\":\"씬 그룹\",\"ComponentType\":2,\"identifier\":\"c1eab583-8c05-4e4a-a590-484d5e94db7e\"},\"Child\":{\"9d011d83-2576-4fef-9cbc-13aca71f5410\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬\",\"ComponentType\":3,\"identifier\":\"9d011d83-2576-4fef-9cbc-13aca71f5410\"},\"Child\":{\"1a6ae16e-19fb-4363-9ae3-f80332468890\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"1a6ae16e-19fb-4363-9ae3-f80332468890\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}}}},\"0a7331a9-06a6-4edd-a454-ac324458576b\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬\",\"ComponentType\":3,\"identifier\":\"0a7331a9-06a6-4edd-a454-ac324458576b\"},\"Child\":{\"d1976492-190d-4010-8048-fdbf257486d6\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"d1976492-190d-4010-8048-fdbf257486d6\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}},\"d34a2dfe-4b6a-4e18-be14-8d4167b6447f\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"d34a2dfe-4b6a-4e18-be14-8d4167b6447f\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}}}},\"a27a995a-34f5-44a1-bf78-f67c10f79d6d\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬\",\"ComponentType\":3,\"identifier\":\"a27a995a-34f5-44a1-bf78-f67c10f79d6d\"},\"Child\":{\"f8da39d1-1540-4877-95d4-a4865b3d37a3\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"f8da39d1-1540-4877-95d4-a4865b3d37a3\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}},\"be2493ed-7523-4c78-9fc0-3d4ace20b887\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"be2493ed-7523-4c78-9fc0-3d4ace20b887\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}}}}}},\"0b5539a5-f34b-4d86-8258-12534a9c4623\":{\"PropertyInfo\":{\"sceneGroupTitle\":\"\",\"sceneGroupDetail\":\"\",\"ComponentName\":\"씬 그룹\",\"ComponentType\":2,\"identifier\":\"0b5539a5-f34b-4d86-8258-12534a9c4623\"},\"Child\":{\"dab58732-ce23-484f-9ae2-06805dbab721\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬\",\"ComponentType\":3,\"identifier\":\"dab58732-ce23-484f-9ae2-06805dbab721\"},\"Child\":{\"623d96cd-0c40-4d6e-b704-5f5252e1565d\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"623d96cd-0c40-4d6e-b704-5f5252e1565d\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}},\"e80b5a77-e784-426f-986e-fdd8f381f2a7\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"e80b5a77-e784-426f-986e-fdd8f381f2a7\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}}}},\"eb002e12-f797-44b2-b20f-37a0caff4181\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬\",\"ComponentType\":3,\"identifier\":\"eb002e12-f797-44b2-b20f-37a0caff4181\"},\"Child\":{\"5bd34aef-9352-4f00-863b-b83332af996a\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"5bd34aef-9352-4f00-863b-b83332af996a\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}},\"5d9a5fb7-5955-490d-8c3d-bbdfeb434d34\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"5d9a5fb7-5955-490d-8c3d-bbdfeb434d34\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}}}}}},\"2310ed96-3d86-4b6e-af9f-cb1d600bbdba\":{\"PropertyInfo\":{\"sceneGroupTitle\":\"\",\"sceneGroupDetail\":\"\",\"ComponentName\":\"씬 그룹\",\"ComponentType\":2,\"identifier\":\"2310ed96-3d86-4b6e-af9f-cb1d600bbdba\"},\"Child\":{\"593c35fb-d337-4cf8-b884-57f8b3e5a105\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬\",\"ComponentType\":3,\"identifier\":\"593c35fb-d337-4cf8-b884-57f8b3e5a105\"},\"Child\":{\"c345e399-bfa8-42c8-b163-e97c78aceba5\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"c345e399-bfa8-42c8-b163-e97c78aceba5\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}},\"cbb1e6fe-f1d1-44ea-806c-d91b9b31236e\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"cbb1e6fe-f1d1-44ea-806c-d91b9b31236e\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}}}},\"56015194-4fec-4d19-b6aa-091f7f7a1129\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬\",\"ComponentType\":3,\"identifier\":\"56015194-4fec-4d19-b6aa-091f7f7a1129\"},\"Child\":{\"a44b43e5-3dcc-4cdb-be52-f8de0d7aca1c\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"a44b43e5-3dcc-4cdb-be52-f8de0d7aca1c\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}}}},\"d33e669e-3137-4151-b513-069960b25ef5\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬\",\"ComponentType\":3,\"identifier\":\"d33e669e-3137-4151-b513-069960b25ef5\"},\"Child\":{\"00cca620-6e82-48bd-89d0-8b7b24e533dd\":{\"PropertyInfo\":{\"text\":\"텍스트를 입력해주세요\",\"fontSize\":32,\"alignment\":3,\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0,\"ComponentName\":\"텍스트\",\"ComponentType\":101,\"identifier\":\"00cca620-6e82-48bd-89d0-8b7b24e533dd\"},\"Transform\":{\"worldPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"worldScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"screenPosition\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenRotation\":{\"x\":0.0,\"y\":0.0,\"z\":0.0},\"screenScale\":{\"x\":1.0,\"y\":1.0,\"z\":1.0},\"ScreenMode\":0},\"BackGround\":{\"sprite\":\"TextBoxBg/0\",\"color\":{\"r\":1.0,\"g\":1.0,\"b\":1.0,\"a\":1.0},\"shadow\":0.0}}}}}}}}"),
            @ApiImplicitParam(name = "userUUID", value = "업로드 사용자 고유 식별자(로그인 성공 응답으로 서버에서 사용자 데이터를 내려줌)", dataType = "string", paramType = "form", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608"),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ContentUploadResponse>> contentFileUploadRequestHandler(
            @ModelAttribute @Valid ContentUploadRequest uploadRequest, BindingResult result
    ) {
        log.info("[CONTENT UPLOAD REQUEST] {}",uploadRequest.toString());
        if (result.hasErrors()) {
            log.info("[ContentUploadRequest] => [{}]", uploadRequest);
            log.error(
                    "[FIELD ERROR] => [{}] [{}]", result.getFieldError().getField(),
                    result.getFieldError().getDefaultMessage()
            );
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
            @RequestParam(value = "userUUID") String userUUID
    ) throws IOException {
        if (contentUUID.isEmpty() || workspaceUUID.isEmpty() || userUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentUploadResponse> uploadResponse = this.contentService.contentDuplicate(
                contentUUID, workspaceUUID, userUUID);
        return ResponseEntity.ok(uploadResponse);
    }

    @ApiOperation(value = "컨텐츠 파일 업데이트", notes = "컨텐츠의 파일을 업데이트하며, 컨텐츠명 속성 파라미터 타겟도 함께 업데이트가 가능함.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "contentUUID", value = "컨텐츠 고유 번호", dataType = "string", paramType = "path", required = true),
            @ApiImplicitParam(name = "targetData", value = "타겟 데이터(URL encoding된 데이터)", dataType = "string", paramType = "form", defaultValue = "0jXPVGTgaHBUXHFoTJwi0bLcK7XxmdrCXp0%2ft9pkT%2bQ%3d"),
            @ApiImplicitParam(name = "targetType", value = "타겟 종류(QR, VTarget)", dataType = "string", paramType = "form", defaultValue = "QR"),
            @ApiImplicitParam(name = "content", value = "수정할 컨텐츠 ares 파일", dataType = "__file", paramType = "form", required = true),
            @ApiImplicitParam(name = "name", value = "수정할 컨텐츠 명", dataType = "string", paramType = "form", required = true, defaultValue = "update"),
            //@ApiImplicitParam(name = "metadata", value = "수정할 컨텐츠 메타데이터", dataType = "string", paramType = "form", defaultValue = "{\"contents\":{\"id\":\"b5db6bb8-9976-4865-859c-1b98e57a3dc5\",\"name\":\"SampleContent\",\"managerUUID\":\"\",\"subProcessTotal\":1,\"sceneGroups\":[{\"id\":\"5f43e519-0f18-46c1-947e-198f801bf3cc\",\"priority\":1,\"name\":\"SceneGroup\",\"jobTotal\":4,\"scenes\":[{\"id\":\"0292b07c-414a-499d-82ee-ad14e2e40dc1\",\"priority\":1,\"name\":\"Scene\",\"subJobTotal\":1,\"reportObjects\":[]},{\"id\":\"7cfda7c8-3a62-404a-9375-b30c23e45637\",\"priority\":2,\"name\":\"Scene\",\"subJobTotal\":1,\"reportObjects\":[]},{\"id\":\"285c316d-d27c-4032-9cd0-638ab9f682e3\",\"priority\":3,\"name\":\"Scene\",\"subJobTotal\":7,\"reportObjects\":[{\"id\":\"e26735f0-3575-45ef-a9d5-4017ec4b01f1\",\"items\":[{\"id\":null,\"priority\":1,\"type\":\"TOGGLE\",\"title\":\"항목1\"},{\"id\":null,\"priority\":2,\"type\":\"INPUT_FIELD\",\"title\":\"항목2\"},{\"id\":null,\"priority\":3,\"type\":\"REPORT\",\"title\":\"항목3\"}]}]},{\"id\":\"c3604d08-cf2b-43f5-90df-b6b8715537d2\",\"priority\":4,\"name\":\"Scene\",\"subJobTotal\":1,\"reportObjects\":[]}]}]}}"),
            @ApiImplicitParam(name = "properties", value = "컨텐츠 속성 메타데이터", dataType = "string", paramType = "form", required = true, defaultValue = "{\"TargetID\":\"c1772a71-dd71-4c2e-b313-fa029fa2e89e\",\"PropertyInfo\":{\"54d3ea12-062e-4a2c-9922-eb1f4440ffa3\":{\"PropertyInfo\":{\"sceneGroupTitle\":\"\",\"sceneGroupDetail\":\"\",\"ComponentName\":\"씬 그룹\",\"ComponentType\":2,\"identifier\":\"54d3ea12-062e-4a2c-9922-eb1f4440ffa3\"},\"Child\":{\"167afc43-4426-400f-8333-2890d78b1282\":{\"PropertyInfo\":{\"sceneTitle\":\"\",\"sceneDetail\":\"\",\"ComponentName\":\"씬\",\"ComponentType\":3,\"identifier\":\"167afc43-4426-400f-8333-2890d78b1282\"}}}}}}"),
            @ApiImplicitParam(name = "userUUID", value = "수정 요청 사용자의 고유번호", dataType = "string", paramType = "form", required = true, defaultValue = "498b1839dc29ed7bb2ee90ad6985c608")
    })
    @PutMapping("/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentUploadResponse>> contentUpdateRequestHandler(
            @ModelAttribute @Valid ContentUpdateRequest updateRequestDto,
            @PathVariable("contentUUID") String contentUUID, BindingResult result
    ) {
        log.info("[CONTENT UPDATE REQUEST] {}",updateRequestDto.toString());
        if (result.hasErrors() || contentUUID.isEmpty()) {
            log.info("[ContentUpdateRequest] => [{}]", updateRequestDto.toString());
            log.error(
                    "[FIELD ERROR] => [{}] [{}]", result.getFieldError().getField(),
                    result.getFieldError().getDefaultMessage()
            );
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentUploadResponse> responseMessage = this.contentService.contentUpdate(
                contentUUID, updateRequestDto);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "컨텐츠 삭제 요청")
    @DeleteMapping
    public ResponseEntity<ApiResponse<ContentDeleteListResponse>> contentDeleteRequestHandler(
            @RequestBody @Valid ContentDeleteRequest contentDeleteRequest, BindingResult result
    ) {
        if (result.hasErrors()) {
            log.error("REQUEST BINDING contentDeleteRequest: {}", contentDeleteRequest.toString());
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentDeleteListResponse> responseMessage = this.contentService.contentDelete(
                contentDeleteRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "컨텐츠 내 씬그룹 목록 조회", notes = "컨텐츠 내에 구성되어 있는 씬그룹의 목록을 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", required = true, paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    })
    @GetMapping("/sceneGroups/content/{contentUUID}")
    public ResponseEntity<ApiResponse<SceneGroupInfoListResponse>> getContentSceneGroupInfoList(
            @PathVariable("contentUUID") String contentUUID
    ) {
        if (contentUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<SceneGroupInfoListResponse> responseMessage = this.contentService.getContentSceneGroups(
                contentUUID);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "컨텐츠 상세 정보 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", required = true, paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    })
    @GetMapping("/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentInfoResponse>> getContentInfo(
            @PathVariable("contentUUID") String contentUUID
    ) {
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

    @ApiOperation(value = "컨텐츠 공유", notes = "컨텐츠의 공유 여부를 변경. 2.0기능인 컨텐츠 타입 변경. 차후 컨텐츠의 세부정보들을 변경할 수 있는 기능으로 확장 예정")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", dataType = "string", required = true, paramType = "path", example = "73624941-8c65-4e0a-8e81-9f52547fa8d0"),
            @ApiImplicitParam(value = "컨텐츠 수정 정보", name = "contentInfoRequest", dataType = "ContentInfoRequest", required = true, paramType = "body")
    })
    @PutMapping("/info/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentInfoResponse>> modifyContentInfo(
            @PathVariable("contentUUID") String contentUUID,
            @RequestBody @Valid ContentInfoRequest contentInfoRequest
    ) {
        if (contentUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentInfoResponse> response = this.contentService.modifyContentInfo(
                contentUUID, contentInfoRequest);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "작업에서 컨텐츠로의 전환", notes = "작업의 컨텐츠를 복제한 후 컨텐츠 목록에 노출. 중복 가능하며 컨텐츠 식별자가 신규발급됨.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "작업 식별자로 컨텐츠 파일을 판별.", dataType = "string", required = true, paramType = "path"),
            @ApiImplicitParam(name = "userUUID", value = "요청 사용자의 고유번호", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("/convertTask/{taskId}")
    public ResponseEntity<ApiResponse<ContentUploadResponse>> taskToContentConvertHandler(
            @PathVariable("taskId") Long taskId,
            @RequestParam(value = "userUUID") String userUUID
    ) {
        if (taskId == null || userUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentUploadResponse> responseApiResponse = this.contentService.convertTaskToContent(
                taskId, userUUID);
        return ResponseEntity.ok(responseApiResponse);
    }

    @ApiOperation(value = "컨텐츠 속성 메타데이터 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", required = true, dataType = "string", paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29"),
            @ApiImplicitParam(name = "userUUID", value = "요청 사용자의 고유번호", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping("/properties/metadata/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentPropertiesResponse>> getContentPropertiesMetadata(
            @PathVariable("contentUUID") String contentUUID, @RequestParam(value = "userUUID") String userUUID
    ) {
        if (contentUUID.isEmpty() || userUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentPropertiesResponse> responseMessage = this.contentService.getContentPropertiesMetadata(
                contentUUID, userUUID);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "타겟 데이터 존재 유무", notes = "타겟 데이터의 존재 유무 확인 (true : 존재함, false : 존재하지 않음)")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "타겟 데이터", name = "targetData", required = true, paramType = "query", example = "mgbvuA6RhUXL%2bJPrK2Z7YoKi7HEp4K0XmmkLbV7SlBRXN%2fJJAuzDX1%2bNyyt7%2fLCM")
    })
    @GetMapping("/target/isExist")
    public ResponseEntity<ApiResponse<Boolean>> isExistTargetData(
            @RequestParam(value = "targetData") String targetData
    ) {
        if (targetData.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        ApiResponse<Boolean> responseMessage = this.contentService.checkTargetData(targetData);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "워크스페이스 사용 용량 및 다운로드 횟수 조회")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "워크스페이스 식별자", name = "workspaceId", required = true, paramType = "path", example = "4d6eab0860969a50acbfa4599fbb5ae8"),
            @ApiImplicitParam(value = "사용랑 조회 시작 일자", name = "startDate", required = true, paramType = "query", example = "2020-07-24T10:00:00"),
            @ApiImplicitParam(value = "사용랑 조회 종료 일자", name = "endDate", required = true, paramType = "query", example = "2020-08-24T23:59:59")
    })
    @GetMapping("/resources/report/{workspaceId}")
    public ResponseEntity<ApiResponse<ContentResourceUsageInfoResponse>> getContentResourceUsageInfoRequest(
            @PathVariable("workspaceId") String workspaceId,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @RequestParam(name = "startDate") LocalDateTime startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            @RequestParam(name = "endDate") LocalDateTime endDate
    ) {
        if (StringUtils.isEmpty(workspaceId)) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentResourceUsageInfoResponse> responseMessage = contentService.getContentResourceUsageInfo(
                workspaceId, startDate, endDate);
        return ResponseEntity.ok(responseMessage);
    }

    @ApiOperation(value = "컨텐츠 전환 수정", tags = "process server only", notes = "컨텐츠를 작업 전환시 공정서버에서 컨텐츠에 전환되었음을 저장")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "컨텐츠 식별자", name = "contentUUID", dataType = "string", required = true, paramType = "path", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29"),
            @ApiImplicitParam(name = "converted", value = "컨텐츠의 공정 전환 여부(YES, NO)", dataType = "string", paramType = "query", required = true, defaultValue = "NO")
    })
    @PutMapping("/convert/{contentUUID}")
    public ResponseEntity<ApiResponse<ContentInfoResponse>> contentConvertHandler(
            @PathVariable("contentUUID") String contentUUID,
            @RequestParam(value = "converted", defaultValue = "NO") YesOrNo converted
    ) {
        if (contentUUID.isEmpty()) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<ContentInfoResponse> response = this.contentService.setConverted(contentUUID, converted);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "컨텐츠 관련 정보 삭제 - 회원탈퇴", tags = "user server only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "workspaceUUID", value = "워크스페이스 식별자", paramType = "path", example = "4d6eab0860969a50acbfa4599fbb5ae8"),
            @ApiImplicitParam(name = "serviceID", value = "요청 서버 명", paramType = "header", example = "user-server")
    })
    @DeleteMapping("/secession/{workspaceUUID}")
    public ResponseEntity<ApiResponse<ContentSecessionResponse>> contentSecessionRequest(
            @PathVariable("workspaceUUID") String workspaceUUID,
            @RequestHeader("serviceID") String requestServiceID
    ) {
        if (!StringUtils.hasText(workspaceUUID) || !StringUtils.hasText(requestServiceID) || !requestServiceID.equals(
                "user-server")) {
            throw new ContentServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ContentSecessionResponse responseMessage = contentService.deleteAllContentInfo(workspaceUUID);
        return ResponseEntity.ok(new ApiResponse<>(responseMessage));
    }
}
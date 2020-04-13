package com.virnect.content.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.content.application.user.UserRestService;
import com.virnect.content.dao.ContentRepository;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.SceneGroup;
import com.virnect.content.dto.MetadataDto;
import com.virnect.content.dto.UserDto;
import com.virnect.content.dto.request.ContentUpdateRequest;
import com.virnect.content.dto.request.ContentUploadRequest;
import com.virnect.content.dto.response.*;
import com.virnect.content.dto.rest.UserInfoResponse;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.PageMetadataResponse;
import com.virnect.content.global.common.ResponseMessage;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.infra.file.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-01-15
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {
    private final FileUploadService fileUploadService;
    private final ContentRepository contentRepository;
    private final UserRestService userRestService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Value("${upload.dir}")
    private String uploadPath;

    /**
     * 콘텐츠 업로드
     *
     * @param uploadRequest - 콘텐츠 업로드 요청 데이터
     * @return - 업로드된 콘텐츠 정보
     */
    @Transactional
    public ApiResponse<ContentUploadResponse> contentUpload(final ContentUploadRequest uploadRequest) {
        // 1. 콘텐츠 업로드 파일 저장
        try {
            String fileUploadPath = this.fileUploadService.upload(uploadRequest.getContent(), uploadRequest.getAruco() + "");

            // 2. 업로드 컨텐츠 정보 수집
            Content content = Content.builder()
                    .name(uploadRequest.getName())
                    .metadata(uploadRequest.getMetadata())
                    .size(uploadRequest.getContent().getSize())
                    .userUUID(uploadRequest.getUserUUID())
                    .path(fileUploadPath)
                    .aruco(uploadRequest.getAruco())
                    .contentUUID(uploadRequest.getContentUUID())
                    .build();


            // 3. 컨텐츠 씬그룹 관련 정보 파싱 및 컨텐츠 정보에 추가
            addSceneGroupToContent(content, content.getMetadata());


            // 4. 업로드 요청 컨텐츠 정보 저장
            this.contentRepository.save(content);
            ContentUploadResponse result = this.modelMapper.map(content, ContentUploadResponse.class);
            return new ApiResponse<>(result);
        } catch (IOException e) {
            log.info("CONTENT UPLOAD ERROR: {}", e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }
    }

    private void addSceneGroupToContent(Content content, String metadata) {
        try {
            // 3-1. 메타데이터 파싱
            MetadataDto metadataDto = objectMapper.readValue(metadata, MetadataDto.class);
            metadataDto.getContents().getSceneGroups().forEach(sceneGroupDto -> {
                // 3-2. 씬그룹 데이터 파싱
                SceneGroup sceneGroup = SceneGroup.builder()
                        .name(sceneGroupDto.getName())
                        .jobTotal(sceneGroupDto.getJobTotal())
                        .priority(sceneGroupDto.getPriority())
                        .uuid(sceneGroupDto.getId())
                        .build();
                // 3-3. 씬그룹 데이터 컨텐츠에 추가
                content.addSceneGroup(sceneGroup);
            });
        } catch (JsonProcessingException e) {
            log.info("CONTENT UPLOAD ERROR: {}", e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }
    }

    /**
     * 콘텐츠 수정 요청 처리
     *
     * @param contentUUID   - 콘텐츠 고유 식별자
     * @param updateRequest - 콘텐츠 수정 요청 데이터
     * @return -  수정된 콘텐츠 정보
     */
    @Transactional
    public ApiResponse<ContentUploadResponse> contentUpdate(final String contentUUID, final ContentUpdateRequest updateRequest) {
        // 1. 수정 대상 컨텐츠 데이터 조회
        Content targetContent = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_UPDATE));

        // 2. 수정 컨텐츠 저장
        String fileUploadPath = null;
        try {
            fileUploadPath = this.fileUploadService.upload(updateRequest.getContent(), targetContent.getAruco() + "");
        } catch (IOException e) {
            log.info("CONTENT UPLOAD ERROR: {}", e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }

        // 3. 기존 컨텐츠 파일 삭제
        this.fileUploadService.delete(targetContent.getPath());

        // 4 수정 컨텐츠 경로 반영
        targetContent.setPath(fileUploadPath);

        // 5 수정 컨텐츠 파일 크기 반영
        targetContent.setSize(byteToMegaByte(updateRequest.getContent().getSize()));

        // 6. 컨텐츠명 변경
        targetContent.setName(updateRequest.getName());

        // 7. 컨텐츠 메타데이터 변경
        targetContent.setMetadata(updateRequest.getMetadata());

        // 7-1. 컨텐츠 씬그룹 수정
        targetContent.getSceneGroupList().clear();
        addSceneGroupToContent(targetContent, updateRequest.getMetadata());


        // 8. 수정 반영
        this.contentRepository.save(targetContent);

        ContentUploadResponse updateResult = this.modelMapper.map(targetContent, ContentUploadResponse.class);
        return new ApiResponse<>(updateResult);
    }

    /**
     * 콘텐츠 파일 다운로드 요청 처리
     *
     * @param fileName - 콘텐츠 파일 이름
     * @return
     */
    public Resource loadContentFile(final String fileName) {
        try {
            Path file = load(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
        } catch (Exception e) {
            log.error("FILE LOAD ERROR: [{}]", e.getMessage());
        }
        return null;
    }

    /**
     * 콘텐츠 삭제 요청 처리
     *
     * @param contentUUID - 콘텐츠 고유 번호
     * @param uuid        - 사용자 고유 번호
     * @return - 파일 삭제 결과
     */
    @Transactional
    public ApiResponse<ContentDeleteResponse> contentDelete(final String contentUUID, final String uuid) {
        // 1. 콘텐츠 정보 가져오기
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
        log.info("USER: [{}] , REMOTE CONTENT: [{}]", uuid, content.getName());

        long affectRows = this.contentRepository.deleteByUuid(content.getUuid());

        if (affectRows <= 0) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DELETE);
        }

        boolean fileDeleteResult = this.fileUploadService.delete(content.getPath());

        // aruco 할당해제 관련 프로세스 필요
//        boolean deallocateResult = this.arucoRepository.deallocatate(content.getUuid());

        if (!fileDeleteResult) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_DELETE);
        }

        return new ApiResponse<>(new ContentDeleteResponse(true));
    }

    /**
     * 콘텐츠 목록 조회
     *
     * @param search   - 조회 검색어
     * @param pageable - 페이징 요청 처리 데이터
     * @return - 콘텐츠 정보 목록
     */
    @Transactional(readOnly = true)
    public ApiResponse<ContentInfoListResponse> getContentList(String search, Pageable pageable) {
        // 1. 사용자 식별번호 조회
        ResponseMessage responseMessage = this.userRestService.getUserInfoSearch(search, false);
        Map<String, Object> data = responseMessage.getData();
        log.info("GET USER INFO BY SEARCH KEYWORD: [{}]", data);
        List<Object> results = (List<Object>) data.get("userInfoList");

        Map<String, UserDto.UserInfo> userInfoMap = new HashMap<>();
        List<UserDto.UserInfo> userInfoList = results.stream()
                .map(object -> {
                    UserDto.UserInfo userInfo = modelMapper.map(object, UserDto.UserInfo.class);
                    userInfoMap.put(userInfo.getUuid(), userInfo);
                    return userInfo;
                }).collect(Collectors.toList());
        List<String> userUUIDList = userInfoList.stream().map(UserDto.UserInfo::getUuid).collect(Collectors.toList());
        log.info("[{}]", userInfoList);

        // 2. 콘텐츠 조회
        Page<Content> contentPage;
        if (search == null) {
            contentPage = this.contentRepository.findAll(pageable);
        } else {
            contentPage = this.contentRepository.findByNameIsContainingOrUserUUIDIsIn(search, userUUIDList, pageable);
        }

        List<ContentInfoResponse> contentInfoList = contentPage.stream().map(content -> {
            ContentInfoResponse contentInfo = new ContentInfoResponse();
            contentInfo.setContentName(content.getName());
            contentInfo.setContentSize(content.getSize());
            contentInfo.setContentUUID(content.getUuid());
            contentInfo.setPath(content.getPath());
            contentInfo.setStatus(content.getStatus());
            contentInfo.setCreatedDate(content.getUpdatedDate());
            contentInfo.setSceneGroupTotal(content.getSceneGroupList().size());
            contentInfo.setTarget(content.getAruco());

            if (userInfoMap.containsKey(content.getUserUUID())) {
                contentInfo.setUploaderName(userInfoMap.get(content.getUserUUID()).getName());
                contentInfo.setUploaderUUID(userInfoMap.get(content.getUserUUID()).getUuid());
                contentInfo.setUploaderProfile(userInfoMap.get(content.getUserUUID()).getProfile());
            } else {
                ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(content.getUserUUID());
                contentInfo.setUploaderProfile(userInfoResponse.getData().getProfile());
                contentInfo.setUploaderName(userInfoResponse.getData().getName());
                contentInfo.setUploaderUUID(userInfoResponse.getData().getUuid());
            }
            return contentInfo;
        }).collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(contentPage.getTotalPages())
                .totalElements(contentPage.getTotalElements())
                .build();

        return new ApiResponse<>(new ContentInfoListResponse(contentInfoList, pageMetadataResponse));
    }

    /**
     * 콘텐츠 메타데이터 조회 요청 처리
     *
     * @param contentUUID - 콘텐츠 식별자
     * @return - 콘텐츠 로우 메타데이터 및 콘텐츠 식별자 데이터
     */
    @Transactional(readOnly = true)
    public ApiResponse<MetadataInfoResponse> getContentRawMetadata(String contentUUID) {
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
        try {
            MetadataInfoResponse metadataInfoResponse = this.objectMapper.readValue(content.getMetadata(), MetadataInfoResponse.class);
            log.info("{}", content.toString());
            return new ApiResponse<>(metadataInfoResponse);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_METADATA_READ);
        }
    }


    private Path load(final String fileName) {
        Path rootLocation = Paths.get(uploadPath);
        return rootLocation.resolve(fileName);
    }

    private long byteToMegaByte(long size) {
        return size / (1024L * 1024L);
    }

    /**
     * 씬그룹 목록 가져오기
     *
     * @param contentUUID - 컨텐츠 UUID
     * @return
     */
    public ApiResponse<SceneGroupInfoListResponse> getContentSceneGroups(String contentUUID) {
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        List<SceneGroup> sceneGroupList = content.getSceneGroupList();

        if (sceneGroupList == null || sceneGroupList.isEmpty()) {
            return new ApiResponse<>(new SceneGroupInfoListResponse(content.getUuid(), new ArrayList<>()));
        }

        List<SceneGroupInfoResponse> sceneGroupInfoResponseList = content.getSceneGroupList().stream()
                .map(sceneGroup -> {
                    SceneGroupInfoResponse sceneGroupInfo = new SceneGroupInfoResponse();
                    sceneGroupInfo.setId(sceneGroup.getUuid());
                    sceneGroupInfo.setJobTotal(sceneGroup.getJobTotal());
                    sceneGroupInfo.setName(sceneGroup.getName());
                    sceneGroupInfo.setPriority(sceneGroup.getPriority());
                    return sceneGroupInfo;
                }).collect(Collectors.toList());
        return new ApiResponse<>(new SceneGroupInfoListResponse(content.getUuid(), sceneGroupInfoResponseList));
    }

    public ApiResponse<ContentInfoResponse> getContentInfo(String contentUUID) {
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
        ContentInfoResponse contentInfoResponse = new ContentInfoResponse();
        contentInfoResponse.setContentName(content.getName());
        contentInfoResponse.setContentSize(content.getSize());
        contentInfoResponse.setContentUUID(content.getUuid());
        contentInfoResponse.setPath(content.getPath());
        contentInfoResponse.setStatus(content.getStatus());
        contentInfoResponse.setCreatedDate(content.getUpdatedDate());
        contentInfoResponse.setSceneGroupTotal(content.getSceneGroupList().size());
        ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(content.getUserUUID());
        contentInfoResponse.setUploaderProfile(userInfoResponse.getData().getProfile());
        contentInfoResponse.setUploaderName(userInfoResponse.getData().getName());
        contentInfoResponse.setUploaderUUID(userInfoResponse.getData().getUuid());
        contentInfoResponse.setTarget(content.getAruco());
        return new ApiResponse<>(contentInfoResponse);
    }
}

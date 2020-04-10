package com.virnect.content.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.content.application.user.UserRestService;
import com.virnect.content.dao.ContentRepository;
import com.virnect.content.dao.TargetQRCodeRepository;
import com.virnect.content.dao.TargetRepository;
import com.virnect.content.domain.*;
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
import java.util.*;
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
    private final TargetRepository targetRepository;
    private final TargetQRCodeRepository targetQRCodeRepository;
    private final UserRestService userRestService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Value("${upload.dir}")
    private String uploadPath;

    private static final YesOrNo INIT_IS_SHARED = YesOrNo.NO;
    private static final YesOrNo INIT_IS_CONVERTED = YesOrNo.NO;

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
            /**
             * 중요!
             * QR코드 데이터 : QR코드에 사용되는 데이터를 서버에서 컨텐츠UUID를 발급하여 함께 사용.
             * QR코드 생성 : 서버는 QR코드를 발급하지 않음. 서버에서 발급한 QR코드 데이터(컨텐츠UUID)를 각 클라이언트에서 QR코드로 생성하여 사용.
             */
            // 컨텐츠 식별자 생성 - 파일명과 타겟데이터에 함께 사용.
            String contentUUID = UUID.randomUUID().toString();

            // 파일명은 컨텐츠 식별자(contentUUID)와 동일
            String fileUploadPath = this.fileUploadService.upload(uploadRequest.getContent(), contentUUID + "");

            // 컨텐츠 타겟 - 타겟 종류
            Target target = Target.builder()
                    .contentList(new ArrayList<>())
                    .targetQRCodeList(new ArrayList<>())
                    .type(uploadRequest.getTargetType())
                    .build();

            this.targetRepository.save(target);

            // 타겟의 데이터 - QR코드
            TargetQRCode targetQRCode = TargetQRCode.builder()
                    .target(target)
                    // 타겟과 컨텐츠 식별자(contentUUID)를 동일하게 씀. 향후 바뀔 여지가 있음.
                    .data(contentUUID)
                    .build();

            this.targetQRCodeRepository.save(targetQRCode);

            List<TargetQRCode> targetQRCodes = new ArrayList<>();
            targetQRCodes.add(targetQRCode);
            target.setTargetQRCodeList(targetQRCodes);

            // 2. 업로드 컨텐츠 정보 수집
            Content content = Content.builder()
                    // TODO : 유효한 워크스페이스 인지 검증 필요.
                    .workspaceUUID(uploadRequest.getWorkspaceUUID())
                    .uuid(contentUUID)
                    .type(uploadRequest.getType())
                    .name(uploadRequest.getName())
                    .metadata(uploadRequest.getMetadata())
                    .userUUID(uploadRequest.getUserUUID())
                    .target(target)
                    .shared(INIT_IS_SHARED)
                    .converted(INIT_IS_CONVERTED)
                    .size(byteToMegaByte(uploadRequest.getContent().getSize()))
                    .path(fileUploadPath)
                    .build();

            // 3. 컨텐츠 씬그룹 관련 정보 파싱 및 컨텐츠 정보에 추가
            addSceneGroupToContent(content, content.getMetadata());

            // 4. 업로드 요청 컨텐츠 정보 저장
            this.contentRepository.save(content);

            List<Content> contents = new ArrayList<>();
            contents.add(content);
            target.setContentList(contents);

            this.targetRepository.save(target);

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
            fileUploadPath = this.fileUploadService.upload(updateRequest.getContent(), targetContent.getTarget() + "");
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
            ContentInfoResponse contentInfo = ContentInfoResponse.builder()
                    .workspaceUUID(content.getWorkspaceUUID())
                    .contentUUID(content.getUuid())
                    .contentName(content.getName())
                    .shared(content.getShared())
                    .sceneGroupTotal(content.getSceneGroupList().size())
                    .contentSize(content.getSize())
                    .path(content.getPath())
                    .converted(content.getConverted())
                    .target(this.modelMapper.map(content.getTarget(), ContentTargetResponse.class))
                    .createdDate(content.getUpdatedDate())
                    .build();

            if (userInfoMap.containsKey(content.getUserUUID())) {
                contentInfo.setUploaderName(userInfoMap.get(content.getUserUUID()).getName());
                contentInfo.setUploaderUUID(userInfoMap.get(content.getUserUUID()).getUuid());
                contentInfo.setUploaderProfile(userInfoMap.get(content.getUserUUID()).getProfile());
            } else {
                ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(content.getUserUUID());
                contentInfo.setUploaderName(userInfoResponse.getData().getName());
                contentInfo.setUploaderUUID(userInfoResponse.getData().getUuid());
                contentInfo.setUploaderProfile(userInfoResponse.getData().getProfile());
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

        return getContentInfoResponseApiResponse(content);
    }

    public ApiResponse<ContentInfoResponse> modifyContentInfo(String contentUUID, ContentType type, YesOrNo shared) {
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        content.setType(type);
        content.setShared(shared);

        this.contentRepository.save(content);

        return getContentInfoResponseApiResponse(content);
    }

    private ApiResponse<ContentInfoResponse> getContentInfoResponseApiResponse(Content content) {
        ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(content.getUserUUID());
        ContentInfoResponse contentInfoResponse = ContentInfoResponse.builder()
                .workspaceUUID(content.getWorkspaceUUID())
                .contentUUID(content.getUuid())
                .contentName(content.getName())
                .shared(content.getShared())
                .sceneGroupTotal(content.getSceneGroupList().size())
                .contentSize(content.getSize())
                .uploaderUUID(userInfoResponse.getData().getUuid())
                .uploaderName(userInfoResponse.getData().getName())
                .uploaderProfile(userInfoResponse.getData().getProfile())
                .path(content.getPath())
                .converted(content.getConverted())
                .target(this.modelMapper.map(content.getTarget(), ContentTargetResponse.class))
                .createdDate(content.getUpdatedDate())
                .build();
        return new ApiResponse<>(contentInfoResponse);
    }

}

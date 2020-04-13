package com.virnect.content.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.content.application.user.UserRestService;
import com.virnect.content.dao.ContentRepository;
import com.virnect.content.dao.TargetRepository;
import com.virnect.content.dao.TypeRepository;
import com.virnect.content.domain.*;
import com.virnect.content.dto.MetadataDto;
import com.virnect.content.dto.request.ContentStatusChangeRequest;
import com.virnect.content.dto.request.ContentUpdateRequest;
import com.virnect.content.dto.request.ContentUploadRequest;
import com.virnect.content.dto.response.*;
import com.virnect.content.dto.rest.UserInfoListResponse;
import com.virnect.content.dto.rest.UserInfoResponse;
import com.virnect.content.event.ContentUpdateFileRollbackEvent;
import com.virnect.content.exception.ContentServiceException;
import com.virnect.content.global.common.ApiResponse;
import com.virnect.content.global.common.PageMetadataResponse;
import com.virnect.content.global.error.ErrorCode;
import com.virnect.content.infra.file.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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
    private final TypeRepository typeRepository;
    private final UserRestService userRestService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

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
            // 컨텐츠 식별자 생성 - 파일명과 함께 사용.
            String contentUUID = UUID.randomUUID().toString();

            // 파일명은 컨텐츠 식별자(contentUUID)와 동일
            String fileUploadPath = this.fileUploadService.upload(uploadRequest.getContent(), contentUUID + "");

            // 2. 업로드 컨텐츠 정보 수집
            Content content = Content.builder()
                    // TODO : 유효한 워크스페이스 인지 검증 필요.
                    .workspaceUUID(uploadRequest.getWorkspaceUUID())
                    .uuid(contentUUID)
                    .name(uploadRequest.getName())
                    .metadata(uploadRequest.getMetadata())
                    .userUUID(uploadRequest.getUserUUID())
                    .shared(INIT_IS_SHARED)
                    .converted(INIT_IS_CONVERTED)
                    .size(uploadRequest.getContent().getSize())
                    .path(fileUploadPath)
                    .build();

            // 3. 컨텐츠 씬그룹 관련 정보 파싱 및 컨텐츠 정보에 추가
            addSceneGroupToContent(content, content.getMetadata());

            // 타겟 저장 후 타겟데이터 반환
            String targetData = addTargetToContent(content, uploadRequest);

            // 4. 업로드 요청 컨텐츠 정보 저장
            this.contentRepository.save(content);

            ContentUploadResponse result = this.modelMapper.map(content, ContentUploadResponse.class);
            // 반환할 타겟정보
            result.setTargetData(targetData);
            result.setTargetType(uploadRequest.getTargetType());
            return new ApiResponse<>(result);
        } catch (IOException e) {
            log.info("CONTENT UPLOAD ERROR: {}", e.getMessage());
            /**
             * TODO : 컨텐츠 업로드가 실패 했을 때의 처리
             * - 컨텐츠 신규 업로드시 실패 : ARUCO와 컨텐츠 연결이 끊어짐. 그냥 업로드 실패 메시지. 그러므로 신규로 발급 받아야 함.
             * - 컨텐츠 업데이트로 업로드 실패시 : ARUCO와 컨텐츠 연결은 끊어지지 않음. 그냥 업로드 실패 메시지.
             */
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

    private String addTargetToContent(Content content, final ContentUploadRequest contentUploadRequest) {
        // 타겟데이터
        String targetData = UUID.randomUUID().toString();

        Target target = Target.builder()
                .type(contentUploadRequest.getTargetType())
                .content(content)
                .data(targetData)
                .build();
        content.addTarget(target);


        this.targetRepository.save(target);

        return targetData;
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

        // 컨텐츠 소유자 확인
        if (!targetContent.getUserUUID().equals(updateRequest.getUserUUID())) new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        // 수정할 수 없는 조건
        if (targetContent.getConverted() != YesOrNo.NO || targetContent.getShared() != YesOrNo.NO || targetContent.getDeleted() != YesOrNo.NO) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_MANAGED);
        }

        // 2. 저장된 파일 가져오기
        File oldContent = this.fileUploadService.getFile(targetContent.getPath());

        // 2. 기존 컨텐츠 파일 삭제
        this.fileUploadService.delete(targetContent.getPath());

        // 3. 수정 컨텐츠 저장
        String fileUploadPath = null;
        try {
            fileUploadPath = this.fileUploadService.upload(updateRequest.getContent(), targetContent.getUuid() + "");
        } catch (IOException e) {
            log.info("CONTENT UPLOAD ERROR: {}", e.getMessage());
            // 3-1. Recover Deleted File.
            eventPublisher.publishEvent(new ContentUpdateFileRollbackEvent(oldContent));
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }

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
     * @param contentUUIDs - 콘텐츠 고유 번호
     * @param workerUUID   - 사용자 고유 번호
     * @return - 파일 삭제 결과
     */
    @Transactional
    public ApiResponse<ContentDeleteListResponse> contentDelete(final String[] contentUUIDs, final String workerUUID) {
        List<ContentDeleteResponse> deleteResponseList = new ArrayList<>();
        for (String contentUUID : contentUUIDs) {
            // 1. 컨텐츠들 조회
            Content content = this.contentRepository.findByUuid(contentUUID)
                    .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

            ContentDeleteResponse contentDeleteResponse = ContentDeleteResponse.builder()
                    .workspaceUUID(content.getWorkspaceUUID())
                    .contentUUID(content.getUuid())
                    .contentName(content.getName())
                    .shared(content.getShared())
                    .uploaderUUID(content.getUserUUID())
                    .converted(content.getConverted())
                    .updatedDate(content.getUpdatedDate())
                    .build();

            // 1-1 권한확인 - 권한이 맞지 않다면 continue.
            // TODO : 관리자 관련 처리 되어있지 않음
            log.info("Content Delete : contentUploader {}, workerUUID {}", content.getUserUUID(), workerUUID);
            if (!content.getUserUUID().equals(workerUUID)) {
                contentDeleteResponse.setMsg(ErrorCode.ERR_CONTENT_DELETE_OWNERSHIP.getMessage());
                contentDeleteResponse.setResult(false);
                deleteResponseList.add(contentDeleteResponse);
                continue;
            }
            // 1-2 삭제조건 확인 - 전환/공유/삭제 세가지 모두 아니어야 함.
            log.info("Content Delete : getConverted {}, getShared {}, getDeleted {}", content.getConverted(), content.getShared(), content.getDeleted());
            if (!(content.getConverted() == YesOrNo.NO && content.getShared() == YesOrNo.NO && content.getDeleted() == YesOrNo.NO)) {
                // TODO : 실패 원인 반환
                contentDeleteResponse.setMsg(ErrorCode.ERR_CONTENT_MANAGED.getMessage());
                contentDeleteResponse.setResult(false);
                deleteResponseList.add(contentDeleteResponse);
                continue;
            }
            // 파일을 실제 삭제하지 않을 경우. 복구 프로세스가 필요할 수도 있어 일부 구현해 놓음.
            if (false) {
                // 2 컨텐츠 삭제 - 삭제여부 YES로 변경, 목록조회시 deleted가 YES인 것은 조회하지 않음.
                content.setDeleted(YesOrNo.YES);
                this.contentRepository.save(content);
            } else {
                // 2 컨텐츠 삭제
                long affectRows = this.contentRepository.deleteByUuid(content.getUuid());
                log.info("deleteByUuid affectRows = {}", affectRows);

                if (affectRows <= 0) {
                    throw new ContentServiceException(ErrorCode.ERR_CONTENT_DELETE);
                }

                // 3 파일 존재 유무 확인 및 파일 삭제
                log.info("content.getPath() = {}", content.getPath());
                if (this.fileUploadService.getFile(content.getPath()).exists()) {
                    // 파일이 없다면 파일삭제는 무시함.
                    boolean fileDeleteResult = this.fileUploadService.delete(content.getPath());

                    if (!fileDeleteResult) {
                        throw new ContentServiceException(ErrorCode.ERR_DELETE_CONTENT);
                    }
                }
            }
            // 5 삭제 성공 반환
            contentDeleteResponse.setMsg(ErrorCode.ERR_CONTENT_DELETE_SUCCEED.getMessage());
            contentDeleteResponse.setResult(true);
            deleteResponseList.add(contentDeleteResponse);
        }
        // 6 최종 결과 반환
        return new ApiResponse<>(new ContentDeleteListResponse(deleteResponseList));
    }

    /**
     * 콘텐츠 목록 조회
     *
     * @param workspaceUUID
     * @param search
     * @param shareds
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public ApiResponse<ContentInfoListResponse> getContentList(String workspaceUUID, String userUUID, String search, String shareds, Pageable pageable) {
        List<ContentInfoResponse> contentInfoList;
        Map<String, UserInfoResponse> userInfoMap = new HashMap<>();
        List<String> userUUIDList = new ArrayList<>();

        if (search != null) {
            // 1. 사용자 식별번호 조회
            ApiResponse<UserInfoListResponse> userInfoListResult = this.userRestService.getUserInfoSearch(search, false);
            UserInfoListResponse userInfoList = userInfoListResult.getData();
            log.info("GET USER INFO BY SEARCH KEYWORD: [{}]", userInfoList);

            userInfoList.getUserInfoList().forEach(userInfoResponse -> {
                userInfoMap.put(userInfoResponse.getUuid(), userInfoResponse);
            });

            userUUIDList = userInfoList.getUserInfoList().stream()
                    .map(UserInfoResponse::getUuid).collect(Collectors.toList());
            log.info("[{}]", userInfoList);
        }


        // 2. 콘텐츠 조회
        Page<Content> contentPage = this.contentRepository.getContent(workspaceUUID, userUUID, search, shareds, userUUIDList, pageable);

        contentInfoList = contentPage.stream().map(content -> {
            ContentInfoResponse contentInfoResponse = ContentInfoResponse.builder()
                    .workspaceUUID(content.getWorkspaceUUID())
                    .contentUUID(content.getUuid())
                    .contentName(content.getName())
                    .shared(content.getShared())
                    .sceneGroupTotal(content.getSceneGroupList().size())
                    .contentSize(content.getSize())
                    .path(content.getPath())
                    .converted(content.getConverted())
                    .target(this.modelMapper.map(content.getTargetList().get(0), ContentTargetResponse.class))
                    .createdDate(content.getUpdatedDate())
                    .build();

            if (userInfoMap.containsKey(content.getUserUUID())) {
                contentInfoResponse.setUploaderName(userInfoMap.get(content.getUserUUID()).getName());
                contentInfoResponse.setUploaderUUID(userInfoMap.get(content.getUserUUID()).getUuid());
                contentInfoResponse.setUploaderProfile(userInfoMap.get(content.getUserUUID()).getProfile());
            } else {
                ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(content.getUserUUID());
                contentInfoResponse.setUploaderProfile(userInfoResponse.getData().getProfile());
                contentInfoResponse.setUploaderName(userInfoResponse.getData().getName());
                contentInfoResponse.setUploaderUUID(userInfoResponse.getData().getUuid());
            }

            return contentInfoResponse;
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
        long megaByte = size / (1024L * 1024L);
        return megaByte == 0 ? 1 : megaByte;
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

    @Transactional
    public ApiResponse<ContentInfoResponse> modifyContentInfo(final String contentUUID, final YesOrNo shared, final Types contentType, final String userUUID) {
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        // 컨텐츠 소유자 확인
        if (!content.getUserUUID().equals(userUUID)) throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        // TODO : 제품 2.0 기능
        Type type = this.typeRepository.findByType(contentType).orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_NOT_FOUND_CONTENT_TYPE));
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
                .target(this.modelMapper.map(content.getTargetList().get(0), ContentTargetResponse.class))
                .createdDate(content.getUpdatedDate())
                .build();
        // 공정 정보에 컨텐츠정보를 넣음
//        setContentProcessInfo(content, contentInfoResponse);
        return new ApiResponse<>(contentInfoResponse);
    }

    /**
     * 전체 콘텐츠 수 및 공정으로 등록된 콘텐츠 수 조회
     *
     * @return - 전체 콘텐츠 수 및 공정 등록 상태 콘텐츠 수
     */
    public ApiResponse<ContentStatisticResponse> getContentStatusInfo() {
        long numberOfContents = this.contentRepository.count();
        long numberOfManagedContents = 0;
//        long numberOfManagedContents = this.contentRepository.countByStatus(ContentStatus.MANAGED);
        return new ApiResponse<>(new ContentStatisticResponse(numberOfContents, numberOfManagedContents));
    }

    public ApiResponse<ContentStatusInfoResponse> updateContentStatus(ContentStatusChangeRequest statusChangeRequest) {
        Content content = this.contentRepository.findByUuid(statusChangeRequest.getContentUUID())
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        // 컨텐츠 소유자 확인
        if (!content.getUserUUID().equals(statusChangeRequest.getUserUUID())) throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

//        ContentStatus status = ContentStatus.valueOf(statusChangeRequest.getStatus().toUpperCase());
//        content.setStatus(status);

        this.contentRepository.save(content);

        ContentStatusInfoResponse contentStatusInfo = new ContentStatusInfoResponse();
        contentStatusInfo.setContentName(content.getName());
        contentStatusInfo.setContentUUID(content.getUuid());
//        contentStatusInfo.setStatus(content.getStatus());

        return new ApiResponse<>(contentStatusInfo);
    }

}

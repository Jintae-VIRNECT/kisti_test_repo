package com.virnect.content.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.content.application.process.ProcessRestService;
import com.virnect.content.application.user.UserRestService;
import com.virnect.content.dao.ContentRepository;
import com.virnect.content.dao.SceneGroupRepository;
import com.virnect.content.dao.TargetRepository;
import com.virnect.content.dao.TypeRepository;
import com.virnect.content.domain.*;
import com.virnect.content.dto.MetadataDto;
import com.virnect.content.dto.request.ContentPropertiesMetadataRequest;
import com.virnect.content.dto.request.ContentTargetRequest;
import com.virnect.content.dto.request.ContentUpdateRequest;
import com.virnect.content.dto.request.ContentUploadRequest;
import com.virnect.content.dto.response.*;
import com.virnect.content.dto.rest.ProcessInfoResponse;
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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
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
    private final SceneGroupRepository sceneGroupRepository;
    private final TargetRepository targetRepository;
    private final TypeRepository typeRepository;

    private final UserRestService userRestService;
    private final ProcessRestService processRestService;

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Value("${upload.dir}")
    private String uploadPath;
    private static final String ARES_FILE_EXTENSION = ".Ares";

    private static final YesOrNo INIT_IS_SHARED = YesOrNo.NO;
    private static final YesOrNo INIT_IS_CONVERTED = YesOrNo.NO;
    private static final YesOrNo INIT_IS_DELETED = YesOrNo.NO;

    /**
     * 콘텐츠 업로드
     * 컨텐츠UUID는 컨텐츠를 관리하기 위한 서버에서의 식별자이면서 동시에 파일명
     * 타겟데이터는 컨텐츠를 뷰에서 인식하여 컨텐츠를 다운로드하거나 정보를 가져오기 위한 키.
     *
     * @param uploadRequest - 콘텐츠 업로드 요청 데이터
     * @return - 업로드된 콘텐츠 정보
     */
    @Transactional
    public ApiResponse<ContentUploadResponse> contentUpload(final ContentUploadRequest uploadRequest) {
        // 1. 콘텐츠 업로드 파일 저장
        try {
            String contentUUID = UUID.randomUUID().toString();

            log.info("CONTENT UPLOAD - contentUUID : {}, request : {}", contentUUID, uploadRequest.toString());

            // 파일명은 컨텐츠 식별자(contentUUID)와 동일
            String fileUploadPath = this.fileUploadService.upload(uploadRequest.getContent(), contentUUID + "");

            // 2. 업로드 컨텐츠 정보 수집
            Content content = Content.builder()
                    // TODO : 유효한 워크스페이스 인지 검증 필요.
                    .workspaceUUID(uploadRequest.getWorkspaceUUID())
                    .uuid(contentUUID)
                    .name(uploadRequest.getName())
                    .metadata(uploadRequest.getMetadata())
                    .properties(uploadRequest.getProperties())
                    .userUUID(uploadRequest.getUserUUID())
                    .shared(INIT_IS_SHARED)
                    .converted(INIT_IS_CONVERTED)
                    .deleted(INIT_IS_DELETED)
                    .size(uploadRequest.getContent().getSize())
                    .path(fileUploadPath)
                    .build();

            // 3. 컨텐츠 씬그룹 관련 정보 파싱 및 컨텐츠 정보에 추가
            addSceneGroupToContent(content, content.getMetadata());

            // 타겟 저장 후 타겟데이터 반환
            String targetData = addTargetToContent(content, uploadRequest.getTargetType(), uploadRequest.getTargetData());

            // 4. 업로드 요청 컨텐츠 정보 저장
            this.contentRepository.save(content);

            ContentUploadResponse result = this.modelMapper.map(content, ContentUploadResponse.class);

            List<Target> targets = content.getTargetList();
            List<ContentTargetResponse> contentTargetResponseList = targets.stream().map(target -> {
                return this.modelMapper.map(target, ContentTargetResponse.class);
            }).collect(Collectors.toList());

            // 반환할 타겟정보
            result.setTargets(contentTargetResponseList);
            result.setContentUUID(contentUUID);
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
            log.info("SCENEGROUP UPLOAD ERROR: {}", e.getMessage());
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }
    }

    private String addTargetToContent(Content content, TargetType targetType, String targetData) {
        Target target = Target.builder()
                .type(targetType)
                .content(content)
                .data(targetData)
                .build();
        content.addTarget(target);

        this.targetRepository.save(target);

        return targetData;
    }

    private String updateTargetToContent(Content content, TargetType targetType, String targetData) {
        Target target = Target.builder()
                .type(targetType)
                .content(content)
                .data(targetData)
                .build();
        content.addTarget(target);

        this.targetRepository.save(target);

        return targetData;
    }

    public ApiResponse<ContentInfoResponse> contentAddTarget(final String contentUUID, final ContentTargetRequest targetRequest) {
        // 컨텐츠의 타겟을 신규 추가 - 다수 추가 가능
        // 1. 수정 대상 컨텐츠 데이터 조회
        Content targetContent = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_UPDATE));

        // 컨텐츠 소유자 확인
        if (!targetContent.getUserUUID().equals(targetRequest.getUserUUID()))
            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        // 수정할 수 없는 조건
        if (targetContent.getConverted() != YesOrNo.NO || targetContent.getShared() != YesOrNo.NO || targetContent.getDeleted() != YesOrNo.NO) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_MANAGED);
        }

        // 타겟 저장 후 타겟데이터 반환
        String targetData = addTargetToContent(targetContent, TargetType.valueOf(targetRequest.getTargetType()), targetRequest.getTargetData());

        // 반환할 타겟정보
        List<ContentTargetResponse> contentTargetResponseList = new ArrayList<>();
        ContentTargetResponse contentTargetResponse = ContentTargetResponse.builder()
                .type(TargetType.valueOf(targetRequest.getTargetType()))
                .data(targetData)
                .build();
        contentTargetResponseList.add(contentTargetResponse);

        ContentInfoResponse updateResult = this.modelMapper.map(targetContent, ContentInfoResponse.class);

        updateResult.setTargets(contentTargetResponseList);

        return new ApiResponse<>(updateResult);
    }

    public ApiResponse<ContentInfoResponse> contentUpdateTarget(final String contentUUID, final Long oldTargetId, final ContentTargetRequest targetRequest) {
        // 컨텐츠의 타겟을 업데이트
        // 1. 수정 대상 컨텐츠 데이터 조회
        Content targetContent = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_UPDATE));

        // 컨텐츠 소유자 확인
        if (!targetContent.getUserUUID().equals(targetRequest.getUserUUID()))
            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        // 수정할 수 없는 조건
        if (targetContent.getConverted() != YesOrNo.NO || targetContent.getShared() != YesOrNo.NO || targetContent.getDeleted() != YesOrNo.NO) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_MANAGED);
        }

        // 타겟 조회
        Target target = this.targetRepository.findById(oldTargetId).orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_NOT_FOUND_TARGET));
        // 변경
        target.setType(TargetType.valueOf(targetRequest.getTargetType()));
        target.setData(targetRequest.getTargetData());
        this.targetRepository.save(target);

        ContentInfoResponse updateResult = this.modelMapper.map(targetContent, ContentInfoResponse.class);

        List<ContentTargetResponse> contentTargetResponseList = new ArrayList<>();
        ContentTargetResponse contentTargetResponse = this.modelMapper.map(target, ContentTargetResponse.class);
        contentTargetResponseList.add(contentTargetResponse);

        updateResult.setTargets(contentTargetResponseList);

        return new ApiResponse<>(updateResult);
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
        if (!targetContent.getUserUUID().equals(updateRequest.getUserUUID()))
            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        // 수정할 수 없는 조건
        if (targetContent.getConverted() != YesOrNo.NO || targetContent.getShared() != YesOrNo.NO || targetContent.getDeleted() != YesOrNo.NO) {
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_MANAGED);
        }

        // 2. 저장된 파일 가져오기
        File oldContent = this.fileUploadService.getFile(targetContent.getPath());

        // 2. 기존 컨텐츠 파일 삭제
        this.fileUploadService.delete(targetContent.getPath());

        // 3. 수정 컨텐츠 저장
        try {
            String fileUploadPath = this.fileUploadService.upload(updateRequest.getContent(), targetContent.getUuid() + "");

            // 4 수정 컨텐츠 경로 반영
            targetContent.setPath(fileUploadPath);
        } catch (IOException e) {
            log.info("CONTENT UPDATE ERROR: {}", e.getMessage());
            // 3-1. Recover Deleted File.
            eventPublisher.publishEvent(new ContentUpdateFileRollbackEvent(oldContent));
            throw new ContentServiceException(ErrorCode.ERR_CONTENT_UPLOAD);
        }


        // 5 수정 컨텐츠 파일 크기 반영
        targetContent.setSize(updateRequest.getContent().getSize());

        // 6. 컨텐츠명 변경
        targetContent.setName(updateRequest.getName());

        // 7. 컨텐츠 메타데이터 변경
        targetContent.setMetadata(updateRequest.getMetadata());

        // 속성 메타데이터 변경
        targetContent.setProperties(updateRequest.getProperties());

        // 7-1. 컨텐츠 씬그룹 수정
        targetContent.getSceneGroupList().clear();
        addSceneGroupToContent(targetContent, updateRequest.getMetadata());

        // 타겟 저장 후 타겟데이터 반환
        String targetData = addTargetToContent(targetContent, updateRequest.getTargetType(), updateRequest.getTargetData());

        // 8. 수정 반영
        this.contentRepository.save(targetContent);

        // 반환할 타겟정보
        List<ContentTargetResponse> contentTargetResponseList = new ArrayList<>();
        ContentTargetResponse contentTargetResponse = ContentTargetResponse.builder()
                .type(updateRequest.getTargetType())
                .data(targetData)
                .build();
        contentTargetResponseList.add(contentTargetResponse);

        ContentUploadResponse updateResult = this.modelMapper.map(targetContent, ContentUploadResponse.class);

        updateResult.setTargets(contentTargetResponseList);

        return new ApiResponse<>(updateResult);
    }

    public Resource contentDownloadForUUIDhandler(final String contentUUID, final String memberUUID) {
        // 1. 컨텐츠 데이터 조회
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        if (!content.getUserUUID().equals(memberUUID))
            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        String regex = "/";
        String[] parts = content.getPath().split(regex);

        return loadContentFile(parts[parts.length - 1]);
    }

    public Resource contentDownloadForTargethandler(final String targetData, final String memberUUID) {
        // 컨텐츠 데이터 조회
        Content content = this.contentRepository.getContentOfTarget(targetData);

        if (content == null)
            throw new ContentServiceException(ErrorCode.ERR_MISMATCH_TARGET);

        if (!content.getUserUUID().equals(memberUUID))
            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        String regex = "/";
        String[] parts = content.getPath().split(regex);

        return loadContentFile(parts[parts.length - 1]);
    }

    /**
     * 콘텐츠 파일 다운로드 요청 처리
     *
     * @param fileName - 콘텐츠 파일 이름
     * @return - 리소스
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
     * @param workspaceUUID - 워크스페이스 식별자
     * @param userUUID      - 사용자 식별자
     * @param search        - 검색어(컨텐츠명 / 사용자명)
     * @param shared        - 공유여부
     * @param pageable      - 페이징
     * @return - 컨텐츠 목록
     */
    @Transactional(readOnly = true)
    public ApiResponse<ContentInfoListResponse> getContentList(String workspaceUUID, String userUUID, String search, String shared, Pageable pageable) {
        List<ContentInfoResponse> contentInfoList;
        Map<String, UserInfoResponse> userInfoMap = new HashMap<>();
        List<String> userUUIDList = new ArrayList<>();

        if (search != null) {
            // 1. 사용자 식별번호 조회
            ApiResponse<UserInfoListResponse> userInfoListResult = this.userRestService.getUserInfoSearch(search, false);
            UserInfoListResponse userInfoList = userInfoListResult.getData();
            log.info("GET USER INFO BY SEARCH KEYWORD: [{}]", userInfoList);

            userInfoList.getUserInfoList().forEach(userInfoResponse -> userInfoMap.put(userInfoResponse.getUuid(), userInfoResponse));

            userUUIDList = userInfoList.getUserInfoList().stream()
                    .map(UserInfoResponse::getUuid).collect(Collectors.toList());
            log.info("[{}]", userInfoList);
        }


        // 2. 콘텐츠 조회
        Page<Content> contentPage = this.contentRepository.getContent(workspaceUUID, userUUID, search, shared, userUUIDList, pageable);

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
            metadataInfoResponse.getContents().setUuid(contentUUID);
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

    public ApiResponse<WorkspaceSceneGroupListResponse> getSceneGroupsInWorkspace(String workspaceUUID, String userUUID, String search, Pageable pageable) {
        Page<SceneGroup> sceneGroupPage = this.sceneGroupRepository.getSceneGroupInWorkspace(workspaceUUID, userUUID, search, pageable);

        List<SceneGroupInfoResponse> responseList = sceneGroupPage.stream().map(sceneGroup -> {
            return SceneGroupInfoResponse.builder()
                    .id(sceneGroup.getUuid())
                    .name(sceneGroup.getName())
                    .jobTotal(sceneGroup.getJobTotal())
                    .priority(sceneGroup.getPriority())
                    .build();
        }).collect(Collectors.toList());

        PageMetadataResponse pageMetadataResponse = PageMetadataResponse.builder()
                .currentPage(pageable.getPageNumber())
                .currentSize(pageable.getPageSize())
                .totalPage(sceneGroupPage.getTotalPages())
                .totalElements(sceneGroupPage.getTotalElements())
                .build();

        return new ApiResponse<>(new WorkspaceSceneGroupListResponse(responseList, pageMetadataResponse));
    }

    /**
     * 씬그룹 목록 가져오기
     *
     * @param contentUUID - 컨텐츠 UUID
     * @return - 씬그룹리스트
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
//        Type type = this.typeRepository.findByType(contentType).orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_NOT_FOUND_CONTENT_TYPE));
//        content.setType(type);
        content.setShared(shared);

        this.contentRepository.save(content);

        return getContentInfoResponseApiResponse(content);
    }

    @Transactional
    public ApiResponse<ContentInfoResponse> setConverted(final String contentUUID, final YesOrNo converted) {
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        content.setConverted(converted);

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
                .targets(content.getTargetList().stream().map(target -> {
                    return this.modelMapper.map(target, ContentTargetResponse.class);
                }).collect(Collectors.toList()))
                .createdDate(content.getUpdatedDate())
                .build();
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
        long numberOfConvertedContents = this.contentRepository.countByConverted(YesOrNo.YES);
        long numberOfSharedContents = this.contentRepository.countByShared(YesOrNo.YES);
        long numberOfDeletedContents = this.contentRepository.countByDeleted(YesOrNo.YES);
//        long numberOfManagedContents = this.contentRepository.countByStatus(ContentStatus.MANAGED);
        return new ApiResponse<>(new ContentStatisticResponse(numberOfContents, numberOfManagedContents, numberOfConvertedContents, numberOfSharedContents, numberOfDeletedContents));
    }

    @Transactional
    public ApiResponse<ContentUploadResponse> convertTaskToContent(Long taskId, String userUUID) {
        // 작업 가져오기
        ApiResponse<ProcessInfoResponse> response = this.processRestService.getProcessInfo(taskId);
        // 작업의 컨텐츠 식별자 가져오기
        String contentUUID = response.getData().getContentUUID();
        // 컨텐츠 가져오기
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));
        // 컨텐츠 소유자 확인
        if (!content.getUserUUID().equals(userUUID))
            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        ContentUploadRequest uploadRequest = ContentUploadRequest.builder()
                .workspaceUUID(content.getWorkspaceUUID())
                .content(convertFileToMultipart(uploadPath.concat(contentUUID).concat(ARES_FILE_EXTENSION)))
                // TODO : 공정 수정 후 반영 예정
//                .contentType(content.getType().getType())
                .name(response.getData().getName())
                .metadata(content.getMetadata())
                .userUUID(content.getUserUUID())
                // 컨텐츠:타겟은 1:1이므로
                .targetType(content.getTargetList().get(0).getType())
                .build();

        return contentUpload(uploadRequest);
    }

    private MultipartFile convertFileToMultipart(String fileUrl) {
        File file = new File(fileUrl);
        log.debug("MULTIPART FILE SOURCE - fileUrl: {}, path: {}", fileUrl, file.getPath());
        try {
            FileItem fileItem = new DiskFileItem("targetFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = fileItem.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
            return new CommonsMultipartFile(fileItem);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ERROR!! CONVERT FILE TO MULTIPARTFILE : {}", e.getMessage());
        }
        return null;
    }

    @Transactional
    public ApiResponse<ContentUploadResponse> contentDuplicate(final String contentUUID, final String workspaceUUID, final String userUUID) {
        // 컨텐츠 가져오기
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        // 컨텐츠의 워크스페이스 확인
        if (!content.getWorkspaceUUID().equals(workspaceUUID))
            throw new ContentServiceException(ErrorCode.ERROR_WORKSPACE);

        // 컨텐츠 소유자 확인
        if (!content.getUserUUID().equals(userUUID))
            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        ContentUploadRequest uploadRequest = ContentUploadRequest.builder()
                .workspaceUUID(workspaceUUID)
                .content(convertFileToMultipart(uploadPath.concat(contentUUID).concat(ARES_FILE_EXTENSION)))
                // TODO : 공정 수정 후 반영 예정
//                .contentType(content.getType().getType())
                .name(content.getName())
                .metadata(content.getMetadata())
                .properties(content.getProperties())
                .userUUID(userUUID)
                .build();

        return contentUpload(uploadRequest);
    }

    public ApiResponse<ContentPropertiesResponse> getContentPropertiesMetadata(String contentUUID, String userUUID) {
        // 컨텐츠 조회
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        if (!content.getUserUUID().equals(userUUID))
            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        // 사용자 조회
        ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(content.getUserUUID());

        // 속성 메타데이터 저장
        return new ApiResponse<>(ContentPropertiesResponse.builder()
                .workspaceUUID(content.getWorkspaceUUID())
                .contentUUID(contentUUID)
                .contentName(content.getName())
                .shared(content.getShared())
                .sceneGroupTotal(content.getSceneGroupList().size())
                .contentSize(content.getSize())
                .uploaderUUID(content.getUserUUID())
                .uploaderName(userInfoResponse.getData().getName())
                .uploaderProfile(userInfoResponse.getData().getProfile())
                .path(content.getPath())
                .converted(content.getConverted())
                .createdDate(content.getUpdatedDate())
                .propertiesMetadata(content.getProperties())
                .build());
    }

    public ApiResponse<ContentPropertiesResponse> setContentPropertiesMetadata(String contentUUID, ContentPropertiesMetadataRequest metadataRequest) {
        // 컨텐츠 조회
        Content content = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        if (!metadataRequest.getUserUUID().equals(content.getUserUUID()))
            throw new ContentServiceException(ErrorCode.ERR_OWNERSHIP);

        content.setProperties(metadataRequest.getProperties());
        this.contentRepository.save(content);

        Content returnContent = this.contentRepository.findByUuid(contentUUID)
                .orElseThrow(() -> new ContentServiceException(ErrorCode.ERR_CONTENT_NOT_FOUND));

        // 사용자 조회
        ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserUUID(returnContent.getUserUUID());

        // 속성 메타데이터 저장
        return new ApiResponse<>(ContentPropertiesResponse.builder()
                .workspaceUUID(returnContent.getWorkspaceUUID())
                .contentUUID(contentUUID)
                .contentName(returnContent.getName())
                .shared(returnContent.getShared())
                .sceneGroupTotal(returnContent.getSceneGroupList().size())
                .contentSize(returnContent.getSize())
                .uploaderUUID(returnContent.getUserUUID())
                .uploaderName(userInfoResponse.getData().getName())
                .uploaderProfile(userInfoResponse.getData().getProfile())
                .path(returnContent.getPath())
                .converted(returnContent.getConverted())
                .createdDate(returnContent.getUpdatedDate())
                .propertiesMetadata(returnContent.getProperties())
                .build());
    }
}

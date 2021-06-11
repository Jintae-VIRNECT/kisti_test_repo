package com.virnect.workspace.application.workspace;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.history.HistoryRepository;
import com.virnect.workspace.dao.setting.SettingRepository;
import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.WorkspaceRepository;
import com.virnect.workspace.dao.workspace.WorkspaceUserPermissionRepository;
import com.virnect.workspace.dao.workspace.WorkspaceUserRepository;
import com.virnect.workspace.domain.setting.*;
import com.virnect.workspace.domain.workspace.Workspace;
import com.virnect.workspace.domain.workspace.WorkspaceUser;
import com.virnect.workspace.domain.workspace.WorkspaceUserPermission;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.onpremise.*;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.dto.rest.MailRequest;
import com.virnect.workspace.dto.rest.PageMetadataRestResponse;
import com.virnect.workspace.dto.rest.UserInfoRestResponse;
import com.virnect.workspace.dto.rest.WorkspaceLicensePlanInfoResponse;
import com.virnect.workspace.event.cache.UserWorkspacesDeleteEvent;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.RedirectProperty;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.common.mapper.workspace.WorkspaceMapStruct;
import com.virnect.workspace.global.constant.LicenseProduct;
import com.virnect.workspace.global.constant.Mail;
import com.virnect.workspace.global.constant.MailSender;
import com.virnect.workspace.global.constant.Role;
import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.infra.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: PF-Workspace
 * DATE: 2021-05-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public abstract class WorkspaceService {
    private static final String serviceID = "workspace-server";
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final UserRestService userRestService;
    private final MessageRestService messageRestService;
    private final FileService fileUploadService;
    private final SpringTemplateEngine springTemplateEngine;
    private final HistoryRepository historyRepository;
    private final MessageSource messageSource;
    private final LicenseRestService licenseRestService;
    private final RedirectProperty redirectProperty;
    private final WorkspaceMapStruct workspaceMapStruct;
    private final RestMapStruct restMapStruct;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SettingRepository settingRepository;
    private final WorkspaceCustomSettingRepository workspaceCustomSettingRepository;

    /**
     * 워크스페이스 생성
     *
     * @param workspaceCreateRequest - 생성 할 워크스페이스 정보
     * @return - 생성 된 워크스페이스 정보
     */
    //@CacheEvict(value = "userWorkspaces", key = "#workspaceCreateRequest.userId")
    @Transactional
    public abstract WorkspaceInfoDTO createWorkspace(WorkspaceCreateRequest workspaceCreateRequest);

    /**
     * 사용자 소속 워크스페이스 조회
     *
     * @param userId - 사용자 uuid
     * @return - 소속된 워크스페이스 정보
     */
    /*@Cacheable(value = "userWorkspaces", key = "{#userId" +
            ".concat(',pageSize=').concat(#pageRequest.of().pageSize)" +
            ".concat(',pageNumber=').concat(#pageRequest.of().pageNumber)" +
            ".concat(',pageSort=').concat(#pageRequest.of().sort.toString()).replace(':',',')}", unless = "#result.workspaceList.size()==0")*/
    public WorkspaceInfoListResponse getUserWorkspaces(
            String userId, com.virnect.workspace.global.common.PageRequest pageRequest
    ) {
        Page<WorkspaceUserPermission> workspaceUserPermissionPage = workspaceUserPermissionRepository.findByWorkspaceUser_UserId(userId, pageRequest.of());
        List<WorkspaceInfoListResponse.WorkspaceInfo> workspaceList = new ArrayList<>();

        for (WorkspaceUserPermission workspaceUserPermission : workspaceUserPermissionPage) {
            WorkspaceUser workspaceUser = workspaceUserPermission.getWorkspaceUser();
            Workspace workspace = workspaceUser.getWorkspace();

            WorkspaceInfoListResponse.WorkspaceInfo workspaceInfo = workspaceMapStruct.workspaceToWorkspaceInfo(workspace);
            workspaceInfo.setJoinDate(workspaceUser.getCreatedDate());

            UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
            workspaceInfo.setMasterName(userInfoRestResponse.getName());
            workspaceInfo.setMasterProfile(userInfoRestResponse.getProfile());
            workspaceInfo.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            workspaceInfo.setMasterNickName(userInfoRestResponse.getNickname());
            workspaceInfo.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
            workspaceList.add(workspaceInfo);
        }

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(workspaceUserPermissionPage.getTotalElements());
        pageMetadataResponse.setTotalPage(workspaceUserPermissionPage.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber());
        pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());

        return new WorkspaceInfoListResponse(workspaceList, pageMetadataResponse);
    }

    /**
     * 워크스페이스 정보 조회
     *
     * @param workspaceId - 워크스페이스 uuid
     * @return - 워크스페이스 정보
     */
    public WorkspaceInfoResponse getWorkspaceDetailInfo(String workspaceId) {
        //workspace 정보 set
        Workspace workspace = workspaceRepository.findByUuid(workspaceId)
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceInfoDTO workspaceInfo = workspaceMapStruct.workspaceToWorkspaceInfoDTO(workspace);
        workspaceInfo.setMasterUserId(workspace.getUserId());

        //user 정보 set
        List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_Workspace(workspace);
        List<WorkspaceUserInfoResponse> userInfoList = workspaceUserPermissionList.stream().map(workspaceUserPermission -> {
            UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId()).getData();
            WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(userInfoRestResponse);
            workspaceUserInfoResponse.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            return workspaceUserInfoResponse;
        }).collect(Collectors.toList());


        //role 정보 set
        long masterUserCount = workspaceUserPermissionList.stream().filter(workspaceUserPermission -> workspaceUserPermission.getWorkspaceRole().getRole().equals(Role.MASTER.name())).count();
        long managerUserCount = workspaceUserPermissionList.stream().filter(workspaceUserPermission -> workspaceUserPermission.getWorkspaceRole().getRole().equals(Role.MANAGER.name())).count();
        long memberUserCount = workspaceUserPermissionList.stream().filter(workspaceUserPermission -> workspaceUserPermission.getWorkspaceRole().getRole().equals(Role.MEMBER.name())).count();

        //plan 정보 set
        int remotePlanCount = 0, makePlanCount = 0, viewPlanCount = 0;
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(workspaceId).getData();
        if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() != null && !workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            for (WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse licenseProductInfoResponse : workspaceLicensePlanInfoResponse.getLicenseProductInfoList()) {
                if (licenseProductInfoResponse.getProductName().equals(LicenseProduct.REMOTE.toString())) {
                    remotePlanCount = licenseProductInfoResponse.getUseLicenseAmount();
                }
                if (licenseProductInfoResponse.getProductName().equals(LicenseProduct.MAKE.toString())) {
                    makePlanCount = licenseProductInfoResponse.getUseLicenseAmount();
                }
                if (licenseProductInfoResponse.getProductName().equals(LicenseProduct.VIEW.toString())) {
                    viewPlanCount = licenseProductInfoResponse.getUseLicenseAmount();
                }
            }
        }
        return new WorkspaceInfoResponse(workspaceInfo, userInfoList, masterUserCount, managerUserCount, memberUserCount, remotePlanCount, makePlanCount, viewPlanCount);
    }

    /**
     * 워크스페이스 정보 조회
     *
     * @param workspaceId - 워크스페이스 식별자
     * @return - 워크스페이스 정보
     */
    public WorkspaceInfoDTO getWorkspaceInfo(String workspaceId) {
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        return workspaceMapStruct.workspaceToWorkspaceInfoDTO(workspace);
    }

    /**
     * 유저 정보 조회(User Service)
     *
     * @param userId - 유저 uuid
     * @return - 유저 정보
     */
    UserInfoRestResponse getUserInfo(String userId) {
        ApiResponse<UserInfoRestResponse> userInfoResponse = userRestService.getUserInfoByUserId(userId);
        return userInfoResponse.getData();
    }

    /**
     * pf-message 서버로 보낼 메일 전송 api body
     *
     * @param html      - 본문
     * @param receivers - 수신정보
     * @param sender    - 발신정보
     * @param subject   - 제목
     */
    private void sendMailRequest(String html, List<String> receivers, String sender, String subject) {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setHtml(html);
        mailRequest.setReceivers(receivers);
        mailRequest.setSender(sender);
        mailRequest.setSubject(subject);
        messageRestService.sendMail(mailRequest);
    }

    /**
     * 워크스페이스 정보 변경
     *
     * @param workspaceUpdateRequest
     * @param locale
     * @return
     */
    public WorkspaceInfoDTO setWorkspace(WorkspaceUpdateRequest workspaceUpdateRequest, Locale locale) {
        if (!StringUtils.hasText(workspaceUpdateRequest.getUserId()) || !StringUtils.hasText(workspaceUpdateRequest.getName())
                || !StringUtils.hasText(workspaceUpdateRequest.getDescription()) || !StringUtils.hasText(
                workspaceUpdateRequest.getWorkspaceId())) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        //마스터 유저 체크
        Workspace workspace = workspaceRepository.findByUuid(workspaceUpdateRequest.getWorkspaceId())
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        String oldWorkspaceName = workspace.getName();
        if (!workspace.getUserId().equals(workspaceUpdateRequest.getUserId())) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        if (!oldWorkspaceName.equals(workspaceUpdateRequest.getName())) {
            List<String> receiverEmailList = new ArrayList<>();
            Context context = new Context();
            context.setVariable("beforeWorkspaceName", oldWorkspaceName);
            context.setVariable("afterWorkspaceName", workspaceUpdateRequest.getName());
            context.setVariable("supportUrl", redirectProperty.getSupportWeb());

            List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findByWorkspace_Uuid(
                    workspace.getUuid());
            workspaceUserList.forEach(workspaceUser -> {
                applicationEventPublisher.publishEvent(new UserWorkspacesDeleteEvent(workspaceUser.getUserId()));//캐싱 삭제
                UserInfoRestResponse userInfoRestResponse = getUserInfo(workspaceUser.getUserId());
                receiverEmailList.add(userInfoRestResponse.getEmail());
                if (userInfoRestResponse.getUuid().equals(workspace.getUserId())) {
                    context.setVariable("workspaceMasterNickName", userInfoRestResponse.getNickname());
                }
            });

            String subject = messageSource.getMessage(Mail.WORKSPACE_INFO_UPDATE.getSubject(), null, locale);
            String template = messageSource.getMessage(Mail.WORKSPACE_INFO_UPDATE.getTemplate(), null, locale);
            String html = springTemplateEngine.process(template, context);
            sendMailRequest(html, receiverEmailList, MailSender.MASTER.getValue(), subject);
        }
        workspace.setName(workspaceUpdateRequest.getName());
        workspace.setDescription(workspaceUpdateRequest.getDescription());

        if (workspaceUpdateRequest.getProfile() != null) {
            String oldProfile = workspace.getProfile();
            //기존 프로필 이미지 삭제
            if (StringUtils.hasText(oldProfile)) {
                fileUploadService.delete(oldProfile);
            }
            //새 프로필 이미지 등록
            try {
                workspace.setProfile(fileUploadService.upload(workspaceUpdateRequest.getProfile()));
            } catch (Exception e) {
                throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
            }

        }

        workspaceRepository.save(workspace);

        return workspaceMapStruct.workspaceToWorkspaceInfoDTO(workspace);
    }

    public WorkspaceLicenseInfoResponse getWorkspaceLicenseInfo(String workspaceId) {
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(
                workspaceId).getData();
		/*if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() == null) {
			throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_WORKSPACE_LICENSE_PLAN);
		}*/

        WorkspaceLicenseInfoResponse workspaceLicenseInfoResponse = new WorkspaceLicenseInfoResponse();
        workspaceLicenseInfoResponse.setLicenseInfoList(new ArrayList<>());

        if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() != null
                && !workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            List<WorkspaceLicenseInfoResponse.LicenseInfo> licenseInfoList = workspaceLicensePlanInfoResponse.getLicenseProductInfoList()
                    .stream()
                    .map(licenseProductInfoResponse -> {
                        WorkspaceLicenseInfoResponse.LicenseInfo licenseInfo = new WorkspaceLicenseInfoResponse.LicenseInfo();
                        licenseInfo.setLicenseType(licenseProductInfoResponse.getLicenseType());
                        licenseInfo.setProductName(licenseProductInfoResponse.getProductName());
                        licenseInfo.setUseLicenseAmount(licenseProductInfoResponse.getUseLicenseAmount());
                        licenseInfo.setLicenseAmount(licenseProductInfoResponse.getUnUseLicenseAmount()
                                + licenseProductInfoResponse.getUseLicenseAmount());
                        return licenseInfo;
                    })
                    .collect(Collectors.toList());
            workspaceLicenseInfoResponse.setLicenseInfoList(licenseInfoList);
        }

        DecimalFormat decimalFormat = new DecimalFormat("0");
        long size = workspaceLicensePlanInfoResponse.getMaxStorageSize();
        workspaceLicenseInfoResponse.setMaxStorageSize(Long.parseLong(decimalFormat.format(size / 1024.0))); //MB -> GB
        workspaceLicenseInfoResponse.setMaxDownloadHit(workspaceLicensePlanInfoResponse.getMaxDownloadHit());
        workspaceLicenseInfoResponse.setMaxCallTime(workspaceLicenseInfoResponse.getMaxCallTime());

        return workspaceLicenseInfoResponse;
    }

    /***
     * 워크스페이스 정보 전체 삭제 처리
     * @param workspaceUUID - 삭제할 워크스페이스의 마스터 사용자 식별자
     * @return - 삭제 처리 결과
     */
    @Transactional
    public WorkspaceSecessionResponse deleteAllWorkspaceInfo(String workspaceUUID) {
        Optional<Workspace> workspaceInfo = workspaceRepository.findByUuid(workspaceUUID);

        // workspace 정보가 없는 경우
        if (!workspaceInfo.isPresent()) {
            return new WorkspaceSecessionResponse(workspaceUUID, true, LocalDateTime.now());
        }

        Workspace workspace = workspaceInfo.get();

        List<WorkspaceUser> workspaceUserList = workspace.getWorkspaceUserList();

        // workspace user permission 삭제
        workspaceUserPermissionRepository.deleteAllWorkspaceUserPermissionByWorkspaceUser(workspaceUserList);

        // workspace user 삭제
        workspaceUserRepository.deleteAllWorkspaceUserByWorkspace(workspace);

        // workspace history 삭제
        historyRepository.deleteAllHistoryInfoByWorkspace(workspace);

        // workspace profile 삭제 (기본 이미지인 경우 제외)
       /* if (!workspace.getProfile().equals("default")) {
            fileUploadService.delete(workspace.getProfile());
        }*/
        //file service로 default 이미지 체크 옮김
        fileUploadService.delete(workspace.getProfile());

        // workspace 삭제
        workspaceRepository.delete(workspace);

        return new WorkspaceSecessionResponse(workspaceUUID, true, LocalDateTime.now());
    }

    @Profile("onpremise")
    public abstract WorkspaceTitleUpdateResponse updateWorkspaceTitle(String workspaceId, WorkspaceTitleUpdateRequest workspaceTitleUpdateRequest);

    @Profile("onpremise")
    public abstract WorkspaceLogoUpdateResponse updateWorkspaceLogo(String workspaceId, WorkspaceLogoUpdateRequest workspaceLogoUpdateRequest);

    @Profile("onpremise")
    public abstract WorkspaceFaviconUpdateResponse updateWorkspaceFavicon(String workspaceId, WorkspaceFaviconUpdateRequest workspaceFaviconUpdateRequest);

    @Profile("onpremise")
    public abstract WorkspaceCustomSettingResponse getWorkspaceCustomSetting();

    public WorkspaceSettingInfoListResponse addWorkspaceSetting(String workspaceId, WorkspaceSettingAddRequest workspaceSettingAddRequest) {
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        List<WorkspaceSettingInfoResponse> workspaceSettingInfoResponseList = workspaceSettingAddRequest.getSettingNameList().stream().map(settingName -> {
            Setting setting = settingRepository.findByName(settingName).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
            SettingValue defaultValue = SettingName.valueOf(settingName.toString()).getDefaultSettingValue();
            WorkspaceCustomSetting workspaceCustomSetting = WorkspaceCustomSetting.builder()
                    .setting(setting)
                    .settingValue(defaultValue)
                    .workspace(workspace)
                    .build();
            workspaceCustomSettingRepository.save(workspaceCustomSetting);
            return workspaceMapStruct.workspaceCustomSettingToWorkspaceSettingInfoResponse(workspaceCustomSetting);
        }).collect(Collectors.toList());
        return new WorkspaceSettingInfoListResponse(workspaceSettingInfoResponseList);
    }

    /**
     * 워크스페이스 설정 조회
     *
     * @param workspaceId - 조회 대상 워크스페이스 식별자
     * @param product     - 조회 대상 product
     * @return - 워크스페이스 설정 목록
     */
    public WorkspaceSettingInfoListResponse getWorkspaceSettingList(String workspaceId, Product product) {
        //유효성 검증
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

        //조회
        List<WorkspaceCustomSetting> workspaceCustomSettingList = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Product(workspaceId, product);
        List<Setting> settingList = settingRepository.findByStatusAndProduct(Status.ACTIVE, product);
        List<WorkspaceSettingInfoResponse> workspaceSettingInfoResponseList = settingList.stream().map(setting -> {
            Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingList.stream().findAny().filter(workspaceCustomSetting -> workspaceCustomSetting.getSetting() == setting);
            if (workspaceCustomSettingOptional.isPresent()) {
                return workspaceMapStruct.workspaceCustomSettingToWorkspaceSettingInfoResponse(workspaceCustomSettingOptional.get());
            } else {
                WorkspaceSettingInfoResponse workspaceSettingInfoResponse = new WorkspaceSettingInfoResponse();
                workspaceSettingInfoResponse.setSettingName(setting.getName());
                workspaceSettingInfoResponse.setPaymentType(setting.getPaymentType());
                return workspaceSettingInfoResponse;
            }
        }).collect(Collectors.toList());
        return new WorkspaceSettingInfoListResponse(workspaceSettingInfoResponseList);
    }


    public WorkspaceSettingInfoListResponse updateWorkspaceSetting(String workspaceId, String userId, WorkspaceSettingUpdateRequest workspaceSettingUpdateRequest) {
        //유효성 검증
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        if (!workspace.getUserId().equals(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //업데이트
        List<WorkspaceSettingInfoResponse> workspaceSettingInfoResponseList = workspaceSettingUpdateRequest.getWorkspaceSettingUpdateInfoList().stream().map(workspaceSettingUpdateInfo -> {
            //변경 대상 설정이 유료인경우, 그설정을 사용할 수 있는 워크스페이스인지 체크
            Setting setting = settingRepository.findByName(workspaceSettingUpdateInfo.getSettingName()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
            if (setting.getPaymentType() != PaymentType.FREE) {
                //워크스페이스 라이선스 체크
            }
            Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, workspaceSettingUpdateInfo.getSettingName());

            //원래 있던 값이면 value만 수정
            if (workspaceCustomSettingOptional.isPresent()) {
                workspaceCustomSettingOptional.get().setValue(workspaceSettingUpdateInfo.getSettingValue());
                workspaceCustomSettingRepository.save(workspaceCustomSettingOptional.get());
                return workspaceMapStruct.workspaceCustomSettingToWorkspaceSettingInfoResponse(workspaceCustomSettingOptional.get());
            } else {
                // 없던 값이면
                Setting newSetting = settingRepository.findByNameAndStatus(workspaceSettingUpdateInfo.getSettingName(), Status.ACTIVE)
                        .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
                WorkspaceCustomSetting workspaceCustomSetting = WorkspaceCustomSetting.builder()
                        .workspace(workspace)
                        .setting(newSetting)
                        .settingValue(workspaceSettingUpdateInfo.getSettingValue())
                        .build();
                workspaceCustomSettingRepository.save(workspaceCustomSetting);
                return workspaceMapStruct.workspaceCustomSettingToWorkspaceSettingInfoResponse(workspaceCustomSetting);
            }
        }).collect(Collectors.toList());
        return new WorkspaceSettingInfoListResponse(workspaceSettingInfoResponseList);
    }

    public SettingUpdateResponse updateSetting(SettingUpdateRequest settingUpdateRequest) {
        Setting setting = settingRepository.findByName(settingUpdateRequest.getSettingName()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        setting.setStatus(settingUpdateRequest.getStatus());
        setting.setPaymentType(settingUpdateRequest.getPaymentType());
        settingRepository.save(setting);
        return new SettingUpdateResponse(setting.getName(), setting.getUpdatedDate());
    }

    public SettingInfoListResponse getSettingList() {
        List<SettingInfoResponse> settingInfoResponseList =
                settingRepository.findAll().stream().map(setting -> {
                    SettingInfoResponse settingInfoResponse = workspaceMapStruct.settingToSettingInfoResponse(setting);
                    settingInfoResponse.setSettingDefaultValue(SettingName.valueOf(setting.getName().toString()).getDefaultSettingValue());
                    settingInfoResponse.setSettingValueList(Arrays.asList(SettingName.valueOf(setting.getName().toString()).getSettingValues()));
                    settingInfoResponse.setProduct(SettingName.valueOf(setting.getName().toString()).getProduct());
                    return settingInfoResponse;
                }).collect(Collectors.toList());
        return new SettingInfoListResponse(settingInfoResponseList);
    }
}

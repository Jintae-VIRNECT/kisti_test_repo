package com.virnect.workspace.application;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.*;
import com.virnect.workspace.dao.redis.UserInviteRepository;
import com.virnect.workspace.domain.*;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.domain.rest.LicenseStatus;
import com.virnect.workspace.dto.MemberInfoDTO;
import com.virnect.workspace.dto.UserInfoDTO;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.WorkspaceNewMemberInfoDTO;
import com.virnect.workspace.dto.onpremise.*;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.CustomPageHandler;
import com.virnect.workspace.global.common.CustomPageResponse;
import com.virnect.workspace.global.common.RedirectProperty;
import com.virnect.workspace.global.constant.*;
import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.global.util.RandomStringTokenUtil;
import com.virnect.workspace.infra.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private static final String serviceID = "workspace-server";
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspacePermissionRepository workspacePermissionRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final UserRestService userRestService;
    private final ModelMapper modelMapper;
    private final MessageRestService messageRestService;
    private final FileService fileUploadService;
    private final UserInviteRepository userInviteRepository;
    private final SpringTemplateEngine springTemplateEngine;
    private final HistoryRepository historyRepository;
    private final MessageSource messageSource;
    private final LicenseRestService licenseRestService;
    private final WorkspaceSettingRepository workspaceSettingRepository;
    private final RedirectProperty redirectProperty;
    private final CacheManager cacheManager;

    /**
     * 워크스페이스 생성
     *
     * @param workspaceCreateRequest - 생성 할 워크스페이스 정보
     * @return - 생성 된 워크스페이스 정보
     */
    @CacheEvict(value = "userWorkspaces", key = "#workspaceCreateRequest.userId")
    @Transactional
    public WorkspaceInfoDTO createWorkspace(WorkspaceCreateRequest workspaceCreateRequest) {
        //필수 값 체크
        if (!StringUtils.hasText(workspaceCreateRequest.getUserId()) || !StringUtils.hasText(
                workspaceCreateRequest.getName()) || !StringUtils.hasText(workspaceCreateRequest.getDescription())) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        //User Service 에서 유저 조회
        UserInfoRestResponse userInfoRestResponse = getUserInfo(workspaceCreateRequest.getUserId());

        //서브유저(유저가 만들어낸 유저)는 워크스페이스를 가질 수 없다.
        if (userInfoRestResponse.getUserType().equals("SUB_USER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //이미 생성한 워크스페이스가 있는지 확인(사용자가 마스터로 소속되는 워크스페이스는 단 1개다.)
        boolean userHasWorkspace = workspaceRepository.existsByUserId(workspaceCreateRequest.getUserId());

        if (userHasWorkspace) {
            throw new WorkspaceException(ErrorCode.ERR_MASTER_WORKSPACE_ALREADY_EXIST);
        }
        //워크스페이스 생성
        String uuid = RandomStringTokenUtil.generate(UUIDType.UUID_WITH_SEQUENCE, 0);
        String pinNumber = RandomStringTokenUtil.generate(UUIDType.PIN_NUMBER, 0);

        String profile;
        if (workspaceCreateRequest.getProfile() != null) {
            try {
                profile = fileUploadService.upload(workspaceCreateRequest.getProfile());
            } catch (IOException e) {
                throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
            }
        } else {
            profile = fileUploadService.getFileUrl("workspace-profile.png");
        }

        Workspace newWorkspace = Workspace.builder()
                .uuid(uuid)
                .userId(workspaceCreateRequest.getUserId())
                .name(workspaceCreateRequest.getName())
                .description(workspaceCreateRequest.getDescription())
                .profile(profile)
                .pinNumber(pinNumber)
                .build();

        workspaceRepository.save(newWorkspace);

        // 워크스페이스 소속 할당
        WorkspaceUser newWorkspaceUser = WorkspaceUser.builder()
                .userId(workspaceCreateRequest.getUserId())
                .workspace(newWorkspace)
                .build();
        workspaceUserRepository.save(newWorkspaceUser);

        // 워크스페이스 권한 할당
        WorkspaceRole workspaceRole = workspaceRoleRepository.findById(Role.MASTER.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
        WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_PERMISSION_NOT_FOUND));
        WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceRole(workspaceRole)
                .workspacePermission(workspacePermission)
                .workspaceUser(newWorkspaceUser)
                .build();
        workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

        WorkspaceInfoDTO workspaceInfoDTO = modelMapper.map(newWorkspace, WorkspaceInfoDTO.class);
        workspaceInfoDTO.setMasterUserId(newWorkspace.getUserId());
        return workspaceInfoDTO;
    }

    /**
     * 사용자 소속 워크스페이스 조회
     *
     * @param userId - 사용자 uuid
     * @return - 소속된 워크스페이스 정보
     */
    @Cacheable(value = "userWorkspaces", key = "#userId", unless = "#result.workspaceList.size()==0")
    public WorkspaceInfoListResponse getUserWorkspaces(
            String userId, com.virnect.workspace.global.common.PageRequest pageRequest
    ) {
        Page<WorkspaceUserPermission> workspaceUserPermissionPage = workspaceUserPermissionRepository.findByWorkspaceUser_UserId(userId, pageRequest.of());

        List<WorkspaceInfoListResponse.WorkspaceInfo> workspaceList = new ArrayList<>();

        for (WorkspaceUserPermission workspaceUserPermission : workspaceUserPermissionPage) {
            WorkspaceUser workspaceUser = workspaceUserPermission.getWorkspaceUser();
            Workspace workspace = workspaceUser.getWorkspace();

            WorkspaceInfoListResponse.WorkspaceInfo workspaceInfo = modelMapper.map(workspace, WorkspaceInfoListResponse.WorkspaceInfo.class);
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
     * 멤버 조회
     *
     * @param workspaceId - 조회 대상 워크스페이스 식별자
     * @param search      - 검색 필터링
     * @param filter      - 조건 필터링
     * @param pageRequest - 페이징 정보
     * @return - 워크스페이스 소속 멤버 목록
     */
    public ApiResponse<MemberListResponse> getMembers(
            String workspaceId, String search, String filter, com.virnect.workspace.global.common.PageRequest pageRequest
    ) {
        //workspace 서버에서 sort 처리할 수 있는 것들만 한다.
        Pageable newPageable = PageRequest.of(pageRequest.of().getPageNumber(), pageRequest.of().getPageSize());
        String sortName = pageRequest.of().getSort().toString().split(":")[0].trim();
        if (sortName.equalsIgnoreCase("workspaceRole") || sortName.equalsIgnoreCase("workspaceUser.createdDate")) {
            newPageable = pageRequest.of();
        }

        Page<WorkspaceUserPermission> workspaceUserPermissionPage = workspaceUserPermissionRepository.getWorkspaceUserList(newPageable, workspaceId);
        String[] userIds = workspaceUserPermissionPage.stream().map(workspaceUserPermission -> workspaceUserPermission.getWorkspaceUser().getUserId()).toArray(String[]::new);

        //권한으로 필터를 건 경우
        if (StringUtils.hasText(filter) && (filter.toUpperCase().contains("MASTER") || filter.toUpperCase().contains("MANAGER") || filter.toUpperCase().contains("MEMBER"))) {
            workspaceUserPermissionPage = workspaceUserPermissionRepository.getRoleFilteredUserList(filter, newPageable, workspaceId);
            userIds = workspaceUserPermissionPage.stream().map(workspaceUserPermission -> workspaceUserPermission.getWorkspaceUser().getUserId()).toArray(String[]::new);
        }

        //라이선스로 필터를 건 경우
        if (StringUtils.hasText(filter) && (filter.toUpperCase().contains("REMOTE") || filter.toUpperCase().contains("MAKE") || filter.toUpperCase().contains("VIEW"))) {
            List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findByWorkspace_Uuid(workspaceId);
            List<String> userIdList = new ArrayList<>();
            for (WorkspaceUser workspaceUser : workspaceUserList) {
                MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(
                        workspaceId, workspaceUser.getUserId()).getData();
                if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
                    myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                        if (filter.toUpperCase().contains(myLicenseInfoResponse.getProductName())) {
                            userIdList.add(workspaceUser.getUserId());
                        }
                    });
                }
            }

            workspaceUserPermissionPage = workspaceUserPermissionRepository.getContainedUserIdList(userIdList, newPageable, workspaceId);
            userIds = workspaceUserPermissionPage.stream().map(workspaceUserPermission -> workspaceUserPermission.getWorkspaceUser().getUserId()).toArray(String[]::new);
        }
        if (userIds.length == 0) {
            PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
            pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
            pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());
            return new ApiResponse<>(new MemberListResponse(new ArrayList<>(), pageMetadataResponse));
        }
        UserInfoListRestResponse userInfoListRestResponse = userRestService.getUserInfoList(search, userIds).getData();

        List<MemberInfoDTO> memberInfoDTOList = new ArrayList<>();
        for (UserInfoRestResponse userInfoRestResponse : userInfoListRestResponse.getUserInfoList()) {
            for (WorkspaceUserPermission workspaceUserPermission : workspaceUserPermissionPage) {
                if (userInfoRestResponse.getUuid().equalsIgnoreCase(workspaceUserPermission.getWorkspaceUser().getUserId())) {
                    MemberInfoDTO memberInfoDTO = modelMapper.map(userInfoRestResponse, MemberInfoDTO.class);
                    memberInfoDTO.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
                    memberInfoDTO.setJoinDate(workspaceUserPermission.getWorkspaceUser().getCreatedDate());
                    memberInfoDTO.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
                    String[] userLicenseProducts = getUserLicenseProductList(workspaceId, workspaceUserPermission.getWorkspaceUser().getUserId());
                    memberInfoDTO.setLicenseProducts(userLicenseProducts);
                    memberInfoDTOList.add(memberInfoDTO);
                }
            }
        }

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(workspaceUserPermissionPage.getTotalElements());
        pageMetadataResponse.setTotalPage(workspaceUserPermissionPage.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
        pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());

        List<MemberInfoDTO> resultMemberListResponse = getSortedMemberList(pageRequest, memberInfoDTOList);
        return new ApiResponse<>(new MemberListResponse(resultMemberListResponse, pageMetadataResponse));
    }

    /**
     * 워크스페이스에 소속된 유저가 보유한 제품 라이선스 목록 조회
     *
     * @param workspaceId - 워크스페이스 식별자
     * @param userId      - 유저 식별자
     * @return - 제품 라이선스 목록
     */
    public String[] getUserLicenseProductList(String workspaceId, String userId) {
        MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(
                workspaceId, userId).getData();

        String[] licenseProducts = new String[0];
        if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
            licenseProducts = myLicenseInfoListResponse.getLicenseInfoList()
                    .stream()
                    .map(MyLicenseInfoResponse::getProductName)
                    .toArray(String[]::new);
        }
        return licenseProducts;
    }

    /**
     * 워크스페이스 멤버 정렬
     *
     * @param pageRequest       - 정렬 정보
     * @param memberInfoDTOList - 정렬할 대상 리스트
     * @return - 정렬된 멤버 리스트
     */
    public List<MemberInfoDTO> getSortedMemberList(
            com.virnect.workspace.global.common.PageRequest
                    pageRequest, List<MemberInfoDTO> memberInfoDTOList
    ) {
        String sortName = pageRequest.of().getSort().toString().split(":")[0].trim();//sort의 기준이 될 열
        String sortDirection = pageRequest.of().getSort().toString().split(":")[1].trim();//sort의 방향 : 내림차순 or 오름차순
        if (sortName.equalsIgnoreCase("workspaceRole") && sortDirection.equalsIgnoreCase("asc")) {
            return memberInfoDTOList.stream()
                    .sorted(
                            Comparator.comparing(MemberInfoDTO::getRoleId, Comparator.nullsFirst(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("workspaceRole") && sortDirection.equalsIgnoreCase("desc")) {
            return memberInfoDTOList.stream()
                    .sorted(
                            Comparator.comparing(MemberInfoDTO::getRoleId, Comparator.nullsFirst(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("asc")) {
            return memberInfoDTOList.stream()
                    .sorted(Comparator.comparing(MemberInfoDTO::getEmail, Comparator.nullsFirst(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("desc")) {
            return memberInfoDTOList.stream()
                    .sorted(Comparator.comparing(MemberInfoDTO::getEmail, Comparator.nullsFirst(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("workspaceUser.createdDate") && sortDirection.equalsIgnoreCase("asc")) {
            return memberInfoDTOList.stream()
                    .sorted(
                            Comparator.comparing(MemberInfoDTO::getJoinDate, Comparator.nullsFirst(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("workspaceUser.createdDate") && sortDirection.equalsIgnoreCase("desc")) {
            return memberInfoDTOList.stream()
                    .sorted(
                            Comparator.comparing(MemberInfoDTO::getJoinDate, Comparator.nullsFirst(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("asc")) {
            List<MemberInfoDTO> koList = memberInfoDTOList.stream().filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1).matches("[가-힣\\s]"))
                    .sorted(Comparator.comparing(MemberInfoDTO::getNickName)).collect(Collectors.toList());
            List<MemberInfoDTO> enList = memberInfoDTOList.stream().filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1).matches("[a-zA-Z\\s]"))
                    .sorted(Comparator.comparing(MemberInfoDTO::getNickName)).collect(Collectors.toList());
            List<MemberInfoDTO> etcList = memberInfoDTOList.stream().filter(memberInfoDTO -> !koList.contains(memberInfoDTO)).filter(memberInfoDTO -> !enList.contains(memberInfoDTO))
                    .sorted(Comparator.comparing(MemberInfoDTO::getNickName)).collect(Collectors.toList());
            List<MemberInfoDTO> nullList = memberInfoDTOList.stream().filter(memberInfoDTO -> !StringUtils.hasText(memberInfoDTO.getNickName())).collect(Collectors.toList());
            enList.addAll(etcList);
            koList.addAll(enList);
            nullList.addAll(koList);
            return nullList;
        }
        if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("desc")) {
            List<MemberInfoDTO> koList = memberInfoDTOList.stream().filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1).matches("[가-힣\\s]"))
                    .sorted(Comparator.comparing(MemberInfoDTO::getNickName).reversed()).collect(Collectors.toList());
            List<MemberInfoDTO> enList = memberInfoDTOList.stream().filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1).matches("[a-zA-Z\\s]"))
                    .sorted(Comparator.comparing(MemberInfoDTO::getNickName).reversed()).collect(Collectors.toList());
            List<MemberInfoDTO> etcList = memberInfoDTOList.stream().filter(memberInfoDTO -> !koList.contains(memberInfoDTO)).filter(memberInfoDTO -> !enList.contains(memberInfoDTO))
                    .sorted(Comparator.comparing(MemberInfoDTO::getNickName).reversed()).collect(Collectors.toList());
            List<MemberInfoDTO> nullList = memberInfoDTOList.stream().filter(memberInfoDTO -> !StringUtils.hasText(memberInfoDTO.getNickName())).collect(Collectors.toList());
            enList.addAll(etcList);
            koList.addAll(enList);
            nullList.addAll(koList);
            return nullList;
        } else {
            return memberInfoDTOList.stream()
                    .sorted(Comparator.comparing(
                            MemberInfoDTO::getUpdatedDate,
                            Comparator.nullsFirst(Comparator.reverseOrder())
                    ))
                    .collect(Collectors.toList());
        }
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
        WorkspaceInfoDTO workspaceInfo = modelMapper.map(workspace, WorkspaceInfoDTO.class);
        workspaceInfo.setMasterUserId(workspace.getUserId());

        //user 정보 set
        List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_Workspace(workspace);
        List<UserInfoDTO> userInfoList = workspaceUserPermissionList.stream().map(workspaceUserPermission -> {
            UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId()).getData();
            UserInfoDTO userInfoDTO = modelMapper.map(userInfoRestResponse, UserInfoDTO.class);
            userInfoDTO.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            return userInfoDTO;
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
        WorkspaceInfoDTO workspaceInfoDTO = modelMapper.map(workspace, WorkspaceInfoDTO.class);
        workspaceInfoDTO.setMasterUserId(workspace.getUserId());
        return workspaceInfoDTO;
    }

    /**
     * 유저 정보 조회(User Service)
     *
     * @param userId - 유저 uuid
     * @return - 유저 정보
     */
    private UserInfoRestResponse getUserInfo(String userId) {
        ApiResponse<UserInfoRestResponse> userInfoResponse = userRestService.getUserInfoByUserId(userId);
        return userInfoResponse.getData();
    }

    /**
     * 워크스페이스 유저 초대
     *
     * @param workspaceId            - 초대 할 워크스페이스 uuid
     * @param workspaceInviteRequest - 초대 유저 정보
     * @return - 초대 결과
     *//*
    public ApiResponse<Boolean> inviteWorkspace(
            String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale
    ) {
        // 워크스페이스 플랜 조회하여 최대 초대 가능 명 수를 초과했는지 체크
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(
                workspaceId).getData();
        if (workspaceLicensePlanInfoResponse == null || workspaceLicensePlanInfoResponse.getLicenseProductInfoList() == null
                || workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_WORKSPACE_LICENSE_PLAN);
        }
        int workspaceUserAmount = workspaceUserRepository.findByWorkspace_Uuid(workspaceId).size();
        if (workspaceLicensePlanInfoResponse.getMaxUserAmount()
                < workspaceUserAmount + workspaceInviteRequest.getUserInfoList().size()) {
            throw new WorkspaceException(ErrorCode.ERR_NOMORE_JOIN_WORKSPACE);
        }

        //초대요청 라이선스
        int requestRemote = 0, requestMake = 0, requestView = 0;
        for (WorkspaceInviteRequest.UserInfo userInfo : workspaceInviteRequest.getUserInfoList()) {
            //초대받는 사람에게 부여되는 라이선스는 최소 1개 이상이도록 체크
            userLicenseValidCheck(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView());
            if (userInfo.isPlanRemote()) {
                requestRemote++;
            }
            if (userInfo.isPlanMake()) {
                requestMake++;
            }
            if (userInfo.isPlanView()) {
                requestView++;
            }
        }

        //초대받는 사람에게 할당할 라이선스가 있는 지 체크.(useful license check)
        for (WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse licenseProductInfo : workspaceLicensePlanInfoResponse.getLicenseProductInfoList()) {
            if (licenseProductInfo.getProductName().equals(LicenseProduct.REMOTE.toString())) {
                log.debug(
                        "[WORKSPACE INVITE USER] Workspace Useful License Check. Workspace Unuse Remote License count >> {}, Request REMOTE License count >> {}",
                        licenseProductInfo.getUnUseLicenseAmount(),
                        requestRemote
                );
                if (!licenseProductInfo.getProductStatus().equals(LicenseProductStatus.ACTIVE)) {
                    log.error(
                            "[WORKSPACE INVITE USER] REMOTE License Product Status is not active. Product Status >>[{}]",
                            licenseProductInfo.getProductStatus()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
                if (licenseProductInfo.getUnUseLicenseAmount() < requestRemote) {
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
            }
            if (licenseProductInfo.getProductName().equals(LicenseProduct.MAKE.toString())) {
                log.debug(
                        "[WORKSPACE INVITE USER] Workspace Useful License Check. Workspace Unuse Make License count >> {}, Request MAKE License count >> {}",
                        licenseProductInfo.getUnUseLicenseAmount(),
                        requestMake
                );
                if (!licenseProductInfo.getProductStatus().equals(LicenseProductStatus.ACTIVE)) {
                    log.error(
                            "[WORKSPACE INVITE USER] MAKE License Product Status is not active. Product Status >>[{}]",
                            licenseProductInfo.getProductStatus()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
                if (licenseProductInfo.getUnUseLicenseAmount() < requestMake) {
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
            }
            if (licenseProductInfo.getProductName().equals(LicenseProduct.VIEW.toString())) {
                log.debug(
                        "[WORKSPACE INVITE USER] Workspace Useful License Check. Workspace Unuse View License count >> {}, Request VIEW License count >> {}",
                        licenseProductInfo.getUnUseLicenseAmount(),
                        requestView
                );
                if (!licenseProductInfo.getProductStatus().equals(LicenseProductStatus.ACTIVE)) {
                    log.error(
                            "[WORKSPACE INVITE USER] VIEW License Product Status is not active. Product Status >>[{}]",
                            licenseProductInfo.getProductStatus()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
                if (licenseProductInfo.getUnUseLicenseAmount() < requestView) {
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
            }
        }

        //라이선스 플랜 타입 구하기 -- basic, pro..(한 워크스페이스에서 다른 타입의 라이선스 플랜을 동시에 가지고 있을 수 없으므로, 아무 플랜이나 잡고 타입을 구함.)
        String licensePlanType = workspaceLicensePlanInfoResponse.getLicenseProductInfoList()
                .stream()
                .map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getLicenseType)
                .collect(Collectors.toList())
                .get(0);

        */

    /**
     * 권한체크
     * 초대하는 사람 권한 - 마스터, 매니저만 가능
     * 초대받는 사람 권한 - 매니저, 멤버만 가능
     * 초대하는 사람이 매니저일때 - 멤버만 초대할 수 있음.
     *//*
        WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, workspaceInviteRequest.getUserId()).orElse(null);
        if (requestUserPermission == null || requestUserPermission.getWorkspaceRole().getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        Workspace workspace = requestUserPermission.getWorkspaceUser().getWorkspace();
        workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            log.debug("[WORKSPACE INVITE USER] Invite request user role >> [{}], response user role >> [{}]",
                    requestUserPermission.getWorkspaceRole().getRole(), userInfo.getRole());
            if (userInfo.getRole().equalsIgnoreCase("MASTER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            if (requestUserPermission.getWorkspaceRole().getRole().equals("MANAGER") && userInfo.getRole().equalsIgnoreCase("MANAGER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        });

        // 초대할 유저의 계정 유효성 체크(user 서비스)
        String[] inviteEmails = workspaceInviteRequest.getUserInfoList().stream().map(WorkspaceInviteRequest.UserInfo::getEmail).toArray(String[]::new);
        InviteUserInfoRestResponse responseUserList = userRestService.getUserInfoByEmailList(inviteEmails).getData();
        if (responseUserList == null || inviteEmails.length != responseUserList.getInviteUserInfoList().size()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_USER_EXIST);
        }
        //TODO : 서브유저로 등록되어 있는 사용자가 포함되어 있는 경우.

        //마스터 유저 정보
        UserInfoRestResponse materUser = userRestService.getUserInfoByUserId(workspace.getUserId()).getData();

        Long duration = Duration.ofDays(7).getSeconds();
        responseUserList.getInviteUserInfoList().forEach(inviteUserResponse -> workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            //이미 이 워크스페이스에 소속되어 있는 경우
            if (workspaceUserRepository.findByUserIdAndWorkspace(inviteUserResponse.getUserUUID(), workspace) != null) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ALREADY_EXIST);
            }
            if (inviteUserResponse.getEmail().equals(userInfo.getEmail())) {
                //redis 긁어서 이미 초대한 정보 있는지 확인하고, 있으면 시간과 초대 정보 업데이트
                UserInvite userInvite = userInviteRepository.findById(
                        inviteUserResponse.getUserUUID() + "-" + workspaceId).orElse(null);
                if (userInvite != null) {
                    userInvite.setRole(userInfo.getRole());
                    userInvite.setPlanRemote(userInfo.isPlanRemote());
                    userInvite.setPlanMake(userInfo.isPlanMake());
                    userInvite.setPlanView(userInfo.isPlanView());
                    userInvite.setUpdatedDate(LocalDateTime.now());
                    userInvite.setExpireTime(duration);
                    userInviteRepository.save(userInvite);
                    log.debug(
                            "[WORKSPACE INVITE USER] Workspace Invite Info Redis Update >> {}", userInvite.toString());
                } else {
                    UserInvite newUserInvite = UserInvite.builder()
                            .inviteId(inviteUserResponse.getUserUUID() + "-" + workspaceId)
                            .responseUserId(inviteUserResponse.getUserUUID())
                            .responseUserEmail(inviteUserResponse.getEmail())
                            .responseUserName(inviteUserResponse.getName())
                            .responseUserNickName(inviteUserResponse.getNickname())
                            .requestUserId(materUser.getUuid())
                            .requestUserEmail(materUser.getEmail())
                            .requestUserName(materUser.getName())
                            .requestUserNickName(materUser.getNickname())
                            .workspaceId(workspace.getUuid())
                            .workspaceName(workspace.getName())
                            .role(userInfo.getRole())
                            .planRemote(userInfo.isPlanRemote())
                            .planMake(userInfo.isPlanMake())
                            .planView(userInfo.isPlanView())
                            .planRemoteType(licensePlanType)
                            .planMakeType(licensePlanType)
                            .planViewType(licensePlanType)
                            .invitedDate(LocalDateTime.now())
                            .updatedDate(null)
                            .expireTime(duration)
                            .build();
                    userInviteRepository.save(newUserInvite);
                    log.debug(
                            "[WORKSPACE INVITE USER] Workspace Invite Info Redis Set >> {}", newUserInvite.toString());
                }
                //메일은 이미 초대한 것 여부와 관계없이 발송한다.
                String rejectUrl = serverUrl + "/workspaces/" + workspaceId + "/invite/accept?userId="
                        + inviteUserResponse.getUserUUID() + "&accept=false&lang=" + locale.getLanguage();
                String acceptUrl = serverUrl + "/workspaces/" + workspaceId + "/invite/accept?userId="
                        + inviteUserResponse.getUserUUID() + "&accept=true&lang=" + locale.getLanguage();
                Context context = new Context();
                context.setVariable("workspaceMasterNickName", materUser.getNickname());
                context.setVariable("workspaceMasterEmail", materUser.getEmail());
                context.setVariable("workspaceName", workspace.getName());
                context.setVariable("workstationHomeUrl", redirectUrl);
                context.setVariable("rejectUrl", rejectUrl);
                context.setVariable("acceptUrl", acceptUrl);
                context.setVariable("responseUserName", inviteUserResponse.getName());
                context.setVariable("responseUserEmail", inviteUserResponse.getEmail());
                context.setVariable("responseUserNickName", inviteUserResponse.getNickname());
                context.setVariable("role", userInfo.getRole());
                context.setVariable("plan", generatePlanString(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView()));
                context.setVariable("supportUrl", supportUrl);
                String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE.getSubject(), null, locale);
                String template = messageSource.getMessage(Mail.WORKSPACE_INVITE.getTemplate(), null, locale);
                String html = springTemplateEngine.process(template, context);

                List<String> emailReceiverList = new ArrayList<>();
                emailReceiverList.add(inviteUserResponse.getEmail());

                sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);
            }
        }));

        return new ApiResponse<>(true);
    }*/
    public ApiResponse<Boolean> inviteWorkspace(
            String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale
    ) {/*
        // 워크스페이스 플랜 조회하여 최대 초대 가능 명 수를 초과했는지 체크
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(
                workspaceId).getData();
        if (workspaceLicensePlanInfoResponse == null || workspaceLicensePlanInfoResponse.getLicenseProductInfoList() == null
                || workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_WORKSPACE_LICENSE_PLAN);
        }
        int workspaceUserAmount = workspaceUserRepository.findByWorkspace_Uuid(workspaceId).size();
        if (workspaceLicensePlanInfoResponse.getMaxUserAmount()
                < workspaceUserAmount + workspaceInviteRequest.getUserInfoList().size()) {
            throw new WorkspaceException(ErrorCode.ERR_NOMORE_JOIN_WORKSPACE);
        }

        //초대받는 사람에게 할당할 라이선스가 있는 지 체크.(useful license check)
        int requestRemote = 0, requestMake = 0, requestView = 0;
        for (WorkspaceInviteRequest.UserInfo userInfo : workspaceInviteRequest.getUserInfoList()) {
            //초대받는 사람에게 부여되는 라이선스는 최소 1개 이상이도록 체크
            userLicenseValidCheck(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView());
            if (userInfo.isPlanRemote()) {
                requestRemote++;
            }
            if (userInfo.isPlanMake()) {
                requestMake++;
            }
            if (userInfo.isPlanView()) {
                requestView++;
            }
        }

        for (WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse licenseProductInfo : workspaceLicensePlanInfoResponse.getLicenseProductInfoList()) {
            if (licenseProductInfo.getProductName().equals(LicenseProduct.REMOTE.toString())) {
                log.info(
                        "[WORKSPACE INVITE USER] Workspace Useful License Check. product : [{}] unused License count : [{}], request License count : [{}], License status : [{}]",
                        LicenseProduct.REMOTE.toString(),
                        licenseProductInfo.getUnUseLicenseAmount(),
                        requestRemote,
                        licenseProductInfo.getProductStatus()
                );
                if (!licenseProductInfo.getProductStatus().equals(LicenseProductStatus.ACTIVE) || licenseProductInfo.getUnUseLicenseAmount() < requestRemote) {
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
            }
            if (licenseProductInfo.getProductName().equals(LicenseProduct.MAKE.toString())) {
                log.info(
                        "[WORKSPACE INVITE USER] Workspace Useful License Check. product : [{}] unused License count : [{}], request License count : [{}], License status : [{}]",
                        LicenseProduct.MAKE.toString(),
                        licenseProductInfo.getUnUseLicenseAmount(),
                        requestRemote,
                        licenseProductInfo.getProductStatus()
                );
                if (!licenseProductInfo.getProductStatus().equals(LicenseProductStatus.ACTIVE) || licenseProductInfo.getUnUseLicenseAmount() < requestMake) {
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
            }
            if (licenseProductInfo.getProductName().equals(LicenseProduct.VIEW.toString())) {
                log.info(
                        "[WORKSPACE INVITE USER] Workspace Useful License Check. product : [{}] unused License count : [{}], request License count : [{}], License status : [{}]",
                        LicenseProduct.MAKE.toString(),
                        licenseProductInfo.getUnUseLicenseAmount(),
                        requestRemote,
                        licenseProductInfo.getProductStatus()
                );
                if (!licenseProductInfo.getProductStatus().equals(LicenseProductStatus.ACTIVE) || licenseProductInfo.getUnUseLicenseAmount() < requestView) {
                    throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_USEFUL_WORKSPACE_LICENSE);
                }
            }
        }
        //라이선스 플랜 타입 구하기 -- basic, pro..(한 워크스페이스에서 다른 타입의 라이선스 플랜을 동시에 가지고 있을 수 없으므로, 아무 플랜이나 잡고 타입을 구함.)
        String licensePlanType = workspaceLicensePlanInfoResponse.getLicenseProductInfoList()
                .stream()
                .map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getLicenseType)
                .collect(Collectors.toList())
                .get(0);*/

        /**
         * 권한체크
         * 초대하는 사람 권한 - 마스터, 매니저만 가능
         * 초대받는 사람 권한 - 매니저, 멤버만 가능
         * 초대하는 사람이 매니저일때 - 멤버만 초대할 수 있음.
         */
        WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, workspaceInviteRequest.getUserId()).orElse(null);
        if (requestUserPermission == null || requestUserPermission.getWorkspaceRole().getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            log.debug("[WORKSPACE INVITE USER] Invite request user role >> [{}], response user role >> [{}]",
                    requestUserPermission.getWorkspaceRole().getRole(), userInfo.getRole());
            if (userInfo.getRole().equalsIgnoreCase("MASTER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            if (requestUserPermission.getWorkspaceRole().getRole().equals("MANAGER") && userInfo.getRole().equalsIgnoreCase("MANAGER")) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        });

        workspaceInviteRequest.getUserInfoList().forEach(userInfo -> {
            InviteUserInfoResponse inviteUserResponse = userRestService.getUserInfoByEmail(userInfo.getEmail()).getData();
            if (inviteUserResponse.isMemberUser() && workspaceUserRepository.findByUserIdAndWorkspace_Uuid(inviteUserResponse.getInviteUserDetailInfo().getUserUUID(), workspaceId).isPresent()) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ALREADY_EXIST);
            }

            boolean inviteSessionExist = false;
            String sessionCode = RandomStringTokenUtil.generate(UUIDType.INVITE_CODE, 20);
            for (UserInvite userInvite : userInviteRepository.findAll()) {
                if (userInvite != null && userInvite.getWorkspaceId().equals(workspaceId) && userInvite.getInvitedUserEmail().equals(userInfo.getEmail())) {
                    inviteSessionExist = true;
                    userInvite.setRole(userInfo.getRole());
                    userInvite.setPlanRemote(userInfo.isPlanRemote());
                    userInvite.setPlanMake(userInfo.isPlanMake());
                    userInvite.setPlanView(userInfo.isPlanView());
                    userInvite.setUpdatedDate(LocalDateTime.now());
                    userInvite.setExpireTime(Duration.ofDays(7).getSeconds());
                    userInviteRepository.save(userInvite);
                    sessionCode = userInvite.getSessionCode();
                    log.info("[WORKSPACE INVITE USER] Workspace Invite Info Redis Update >> {}", userInvite.toString());
                }
            }
            if (!inviteSessionExist) {
                UserInvite newUserInvite = UserInvite.builder()
                        .sessionCode(sessionCode)
                        .invitedUserEmail(userInfo.getEmail())
                        .invitedUserId(inviteUserResponse.getInviteUserDetailInfo().getUserUUID())
                        .requestUserId(workspaceInviteRequest.getUserId())
                        .workspaceId(workspaceId)
                        .role(userInfo.getRole())
                        .planRemote(userInfo.isPlanRemote())
                        .planMake(userInfo.isPlanMake())
                        .planView(userInfo.isPlanView())
         //               .planRemoteType(licensePlanType)
//                        .planMakeType(licensePlanType)
//                        .planViewType(licensePlanType)
                        .invitedDate(LocalDateTime.now())
                        .updatedDate(null)
                        .expireTime(Duration.ofDays(7).getSeconds())
                        .build();
                userInviteRepository.save(newUserInvite);
                log.info("[WORKSPACE INVITE USER] Workspace Invite Info Redis Set >> {}", newUserInvite.toString());
            }

            //메일 전송
            String rejectUrl = redirectProperty.getWorkspaceServer() + "/workspaces/invite/" + sessionCode + "/reject?lang=" + locale.getLanguage();
            String acceptUrl = redirectProperty.getWorkspaceServer() + "/workspaces/invite/" + sessionCode + "/accept?lang=" + locale.getLanguage();
            Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
            UserInfoRestResponse materUser = getUserInfo(workspace.getUserId());
            if (inviteUserResponse.isMemberUser()) {
                Context context = new Context();
                context.setVariable("rejectUrl", rejectUrl);
                context.setVariable("acceptUrl", acceptUrl);
                context.setVariable("workspaceMasterNickName", materUser.getNickname());
                context.setVariable("workspaceMasterEmail", materUser.getEmail());
                context.setVariable("workspaceName", workspace.getName());
                context.setVariable("responseUserName", inviteUserResponse.getInviteUserDetailInfo().getName());
                context.setVariable("responseUserEmail", inviteUserResponse.getInviteUserDetailInfo().getEmail());
                context.setVariable("responseUserNickName", inviteUserResponse.getInviteUserDetailInfo().getNickname());
                context.setVariable("role", userInfo.getRole());
                context.setVariable("plan", generatePlanString(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView()));
                context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
                context.setVariable("supportUrl", redirectProperty.getSupportWeb());
                String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE.getSubject(), null, locale);
                String template = messageSource.getMessage(Mail.WORKSPACE_INVITE.getTemplate(), null, locale);
                String html = springTemplateEngine.process(template, context);
                List<String> emailReceiverList = new ArrayList<>();
                emailReceiverList.add(userInfo.getEmail());
                sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);
            } else {
                Context context = new Context();
                context.setVariable("rejectUrl", rejectUrl);
                context.setVariable("acceptUrl", acceptUrl);
                context.setVariable("masterUserName", materUser.getName());
                context.setVariable("masterUserNickname", materUser.getNickname());
                context.setVariable("masterUserEmail", materUser.getEmail());
                context.setVariable("workspaceName", workspace.getName());
                context.setVariable("inviteUserEmail", userInfo.getEmail());
                context.setVariable("role", userInfo.getRole());
                context.setVariable("plan", generatePlanString(userInfo.isPlanRemote(), userInfo.isPlanMake(), userInfo.isPlanView()));
                context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
                context.setVariable("supportUrl", redirectProperty.getSupportWeb());
                String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE_NON_USER.getSubject(), null, locale);
                String template = messageSource.getMessage(Mail.WORKSPACE_INVITE_NON_USER.getTemplate(), null, locale);
                String html = springTemplateEngine.process(template, context);
                List<String> emailReceiverList = new ArrayList<>();
                emailReceiverList.add(userInfo.getEmail());
                sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);
            }
        });
        return new ApiResponse<>(true);
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

    /*public RedirectView inviteWorkspaceResult(String email, Boolean accept, String lang) {
        Locale locale = new Locale(lang, "");
        UserInvite userInvite = userInviteRepository.findById(email).orElse(null);
        if (userInvite == null) {
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectUrl + "/?message=workspace.invite.invalid");
            redirectView.setContentType("application/json");
            return redirectView;
        }

        if (!userInvite.isUser()) {
            //회원가입하고나서 다시 요청하도록한다.
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("https://192.168.6.3:8883/terms?invite=true&lang=" + lang);
            redirectView.setContentType("application/json");
            return redirectView;
        } else {
            InviteUserInfoResponse inviteUserInfo = userRestService.getUserInfoByEmail(userInvite.getInvitedUserEmail()).getData();
            userInvite.setInvitedUserEmail(email);
            userInvite.setUser(true);
            userInvite.setInvitedUserId(inviteUserInfo.getInviteUserDetailInfo().getUserUUID());
            userInviteRepository.save(userInvite);
        }

        if (accept) {
            return inviteWorkspaceAccept(userInvite, locale);
        } else {
            return inviteWorkspaceReject(userInvite, locale);
        }
    }*/

    private void historySaveHandler(String message, String userId, Workspace workspace) {
        History history = History.builder()
                .message(message)
                .userId(userId)
                .workspace(workspace)
                .build();
        historyRepository.save(history);
    }

    public RedirectView inviteWorkspaceAccept(String sessionCode, String lang) throws IOException {
        Locale locale = new Locale(lang, "");
        UserInvite userInvite = userInviteRepository.findById(sessionCode).orElse(null);
        if (userInvite == null) {
            log.info("[WORKSPACE INVITE ACCEPT] Workspace invite session Info Not found. session code >> [{}]", sessionCode);
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getWorkstationWeb() + "/?message=workspace.invite.invalid");
            redirectView.setContentType("application/json");
            return redirectView;
        }

        log.info("[WORKSPACE INVITE ACCEPT] Workspace invite session Info >> [{}]", userInvite.toString());
        InviteUserInfoResponse inviteUserResponse = userRestService.getUserInfoByEmail(userInvite.getInvitedUserEmail()).getData();
        if (inviteUserResponse != null && !inviteUserResponse.isMemberUser()) {
            log.info("[WORKSPACE INVITE ACCEPT] Invited User isMemberUser Info >> [{}]", inviteUserResponse.isMemberUser());
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getTermsWeb() + "?inviteSession=" + sessionCode + "&lang=" + lang +"&email="+userInvite.getInvitedUserEmail());
            redirectView.setContentType("application/json");
            return redirectView;

        }
        //비회원일경우 초대 session정보에 uuid가 안들어가므로 user서버에서 조회해서 가져온다.
        InviteUserDetailInfoResponse inviteUserDetailInfoResponse = inviteUserResponse.getInviteUserDetailInfo();
        userInvite.setInvitedUserEmail(inviteUserDetailInfoResponse.getEmail());
        userInvite.setInvitedUserId(inviteUserDetailInfoResponse.getUserUUID());
        userInviteRepository.save(userInvite);

        Workspace workspace = workspaceRepository.findByUuid(userInvite.getWorkspaceId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));

        //워크스페이스 최대 초대가능한 멤버수 9명 체크
        /*if (workspaceUserRepository.findByWorkspace_Uuid(workspace.getUuid()).size() > 8) {
            return worksapceOverJoinFailHandler(workspace, userInvite, locale);
        }
*/
        //라이선스 체크 - 라이선스 플랜 보유 체크, 멤버 제한 수 체크
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(workspace.getUuid()).getData();
        if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() == null || workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_WORKSPACE_LICENSE_PLAN);
        }

        //라이선스 최대 멤버 수 초과 메일전송
        int workspaceUserAmount = workspaceUserRepository.findByWorkspace_Uuid(workspace.getUuid()).size();
        if (workspaceLicensePlanInfoResponse.getMaxUserAmount() < workspaceUserAmount + 1) {
            log.error("[WORKSPACE INVITE ACCEPT] Over Max Workspace Member amount. max user Amount >> [{}], exist user amount >> [{}]",
                    workspaceLicensePlanInfoResponse.getMaxUserAmount(),
                    workspaceUserAmount + 1);
            worksapceOverMaxUserFailHandler(workspace, userInvite, locale);
        }

        //플랜 할당.
        boolean licenseGrantResult = true;
        List<String> successPlan = new ArrayList<>();
        List<String> failPlan = new ArrayList<>();

        List<LicenseProduct> licenseProductList = generatePlanList(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView());
        for (LicenseProduct licenseProduct : licenseProductList) {
            MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(workspace.getUuid(), inviteUserDetailInfoResponse.getUserUUID(), licenseProduct.toString()).getData();
            if (grantResult == null || !StringUtils.hasText(grantResult.getProductName())) {
                failPlan.add(licenseProduct.toString());
                licenseGrantResult = false;
            } else {
                successPlan.add(licenseProduct.toString());
            }
        }
        if (!licenseGrantResult) {
            workspaceOverPlanFailHandler(workspace, userInvite, successPlan, failPlan, locale);
            successPlan.forEach(s -> {
                Boolean revokeResult = licenseRestService.revokeWorkspaceLicenseToUser(workspace.getUuid(), userInvite.getInvitedUserId(), s).getData();
                log.info("[WORKSPACE INVITE ACCEPT] [{}] License Grant Fail. Revoke user License Result >> [{}]", s, revokeResult);
            });
        }
        //워크스페이스 소속 넣기 (workspace_user)
        WorkspaceUser workspaceUser = WorkspaceUser.builder().workspace(workspace).userId(userInvite.getInvitedUserId()).build();
        workspaceUserRepository.save(workspaceUser);

        //워크스페이스 권한 부여하기 (workspace_user_permission)
        WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(userInvite.getRole().toUpperCase()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceUser(workspaceUser)
                .workspaceRole(workspaceRole)
                .workspacePermission(workspacePermission)
                .build();
        workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

        //MAIL 발송
        UserInfoRestResponse inviteUserInfo = getUserInfo(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfo(workspace.getUserId());
        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("acceptUserNickName", inviteUserInfo.getNickname());
        context.setVariable("acceptUserEmail", userInvite.getInvitedUserEmail());
        context.setVariable("role", userInvite.getRole());
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("plan", generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()));
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE_ACCEPT.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_INVITE_ACCEPT.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);
        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());
        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfo(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }

        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        //redis 에서 삭제
        cacheManager.getCache("userWorkspaces").evict(userInvite.getInvitedUserEmail());
        userInviteRepository.delete(userInvite);

        //history 저장
        if (workspaceRole.getRole().equalsIgnoreCase("MANAGER")) {
            String message = messageSource.getMessage("WORKSPACE_INVITE_MANAGER", new String[]{inviteUserInfo.getNickname(), generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView())}, locale);
            historySaveHandler(message, userInvite.getInvitedUserId(), workspace);
        } else {
            String message = messageSource.getMessage("WORKSPACE_INVITE_MEMBER", new String[]{inviteUserInfo.getNickname(), generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView())}, locale);
            historySaveHandler(message, userInvite.getInvitedUserId(), workspace);
        }

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb());
        redirectView.setContentType("application/json");
        return redirectView;
    }

    public RedirectView worksapceOverJoinFailHandler(Workspace workspace, UserInvite userInvite, Locale locale) {
        UserInfoRestResponse inviteUserInfo = getUserInfo(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfo(workspace.getUserId());
        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("userNickName", inviteUserInfo.getNickname());
        context.setVariable("userEmail", inviteUserInfo.getEmail());
        context.setVariable("plan", generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()));
        context.setVariable("planRemoteType", userInvite.getPlanRemoteType());
        context.setVariable("planMakeType", userInvite.getPlanMakeType());
        context.setVariable("planViewType", userInvite.getPlanViewType());
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("workstationMembersUrl", redirectProperty.getMembersWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        String subject = messageSource.getMessage(Mail.WORKSPACE_OVER_JOIN_FAIL.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_OVER_JOIN_FAIL.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);

        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());
        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfo(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }

        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        userInviteRepository.delete(userInvite);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_JOIN_FAIL.getValue());
        redirectView.setContentType("application/json");
        return redirectView;
    }

    public RedirectView worksapceOverMaxUserFailHandler(Workspace workspace, UserInvite userInvite, Locale locale) {
        UserInfoRestResponse inviteUserInfo = getUserInfo(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfo(workspace.getUserId());

        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("userNickName", inviteUserInfo.getNickname());
        context.setVariable("userEmail", inviteUserInfo.getEmail());
        context.setVariable("plan", generatePlanString(userInvite.isPlanRemote(), userInvite.isPlanMake(), userInvite.isPlanView()));
        context.setVariable("planRemoteType", userInvite.getPlanRemoteType());
        context.setVariable("planMakeType", userInvite.getPlanMakeType());
        context.setVariable("planViewType", userInvite.getPlanViewType());
        context.setVariable("contactUrl", redirectProperty.getContactWeb());
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());
        String subject = messageSource.getMessage(Mail.WORKSPACE_OVER_MAX_USER_FAIL.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_OVER_MAX_USER_FAIL.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);
        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());
        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfo(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }
        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        userInviteRepository.delete(userInvite);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_MAX_USER_FAIL.getValue());
        redirectView.setContentType("application/json");
        return redirectView;
    }

    public RedirectView workspaceOverPlanFailHandler(Workspace workspace, UserInvite userInvite, List<String> successPlan, List<String> failPlan, Locale locale) {
        UserInfoRestResponse inviteUserInfo = getUserInfo(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfo(workspace.getUserId());

        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUserInfo.getNickname());
        context.setVariable("workspaceMasterEmail", masterUserInfo.getEmail());
        context.setVariable("userNickName", inviteUserInfo.getNickname());
        context.setVariable("userEmail", inviteUserInfo.getEmail());
        context.setVariable("successPlan", org.apache.commons.lang.StringUtils.join(successPlan, ","));
        context.setVariable("failPlan", org.apache.commons.lang.StringUtils.join(failPlan, ","));
        context.setVariable("planRemoteType", userInvite.getPlanRemoteType());
        context.setVariable("planMakeType", userInvite.getPlanMakeType());
        context.setVariable("planViewType", userInvite.getPlanViewType());
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("workstationMembersUrl", redirectProperty.getMembersWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        String subject = messageSource.getMessage(Mail.WORKSPACE_OVER_PLAN_FAIL.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_OVER_PLAN_FAIL.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);
        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());
        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfo(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }
        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        userInviteRepository.delete(userInvite);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb() + RedirectPath.WORKSPACE_OVER_PLAN_FAIL.getValue());
        redirectView.setContentType("application/json");
        return redirectView;
    }

    public RedirectView inviteWorkspaceReject(String sessionCode, String lang) {
        Locale locale = new Locale(lang, "");
        UserInvite userInvite = userInviteRepository.findById(sessionCode).orElse(null);
        if (userInvite == null) {
            log.info("[WORKSPACE INVITE REJECT] Workspace invite session Info Not found. session code >> [{}]", sessionCode);
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getWorkstationWeb());
            redirectView.setContentType("application/json");
            return redirectView;
        }
        log.info("[WORKSPACE INVITE REJECT] Workspace Invite Session Info >> [{}] ", userInvite);

        //비회원 거절은 메일 전송 안함.
        InviteUserInfoResponse inviteUserResponse = userRestService.getUserInfoByEmail(userInvite.getInvitedUserEmail()).getData();
        if (inviteUserResponse != null && !inviteUserResponse.isMemberUser()) {
            log.info("[WORKSPACE INVITE REJECT] Invited User isMemberUser Info >> [{}]", inviteUserResponse.isMemberUser());
            userInviteRepository.delete(userInvite);
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectProperty.getWorkstationWeb());
            redirectView.setContentType("application/json");
            return redirectView;
        }
        //비회원일경우 초대 session정보에 uuid가 안들어가므로 user서버에서 조회해서 가져온다.
        InviteUserDetailInfoResponse inviteUserDetailInfoResponse = inviteUserResponse.getInviteUserDetailInfo();
        userInvite.setInvitedUserEmail(inviteUserDetailInfoResponse.getEmail());
        userInvite.setInvitedUserId(inviteUserDetailInfoResponse.getUserUUID());
        userInviteRepository.save(userInvite);

        userInviteRepository.delete(userInvite);

        //MAIL 발송
        Workspace workspace = workspaceRepository.findByUuid(userInvite.getWorkspaceId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        UserInfoRestResponse inviterUserInfo = getUserInfo(userInvite.getInvitedUserId());
        UserInfoRestResponse masterUserInfo = getUserInfo(workspace.getUserId());
        Context context = new Context();
        context.setVariable("rejectUserNickname", inviterUserInfo.getNickname());
        context.setVariable("rejectUserEmail", userInvite.getInvitedUserEmail());
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("accountUrl", redirectProperty.getAccountWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE_REJECT.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_INVITE_REJECT.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);

        List<String> emailReceiverList = new ArrayList<>();
        emailReceiverList.add(masterUserInfo.getEmail());

        List<WorkspaceUserPermission> managerUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (managerUserPermissionList != null && !managerUserPermissionList.isEmpty()) {
            managerUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUserInfo = getUserInfo(workspaceUserPermission.getWorkspaceUser().getUserId());
                emailReceiverList.add(managerUserInfo.getEmail());
            });
        }
        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectProperty.getWorkstationWeb());
        redirectView.setContentType("application/json");
        return redirectView;
    }

    private List<LicenseProduct> generatePlanList(boolean remote, boolean make, boolean view) {
        List<LicenseProduct> productList = new ArrayList<>();
        if (remote) {
            productList.add(LicenseProduct.REMOTE);
        }
        if (make) {
            productList.add(LicenseProduct.MAKE);
        }
        if (view) {
            productList.add(LicenseProduct.VIEW);
        }
        return productList;
    }

    private String generatePlanString(boolean remote, boolean make, boolean view) {
        return generatePlanList(remote, make, view).stream().map(Enum::toString).collect(Collectors.joining(","));
    }

    /*
    public RedirectView inviteWorkspaceResult(String workspaceId, String userId, Boolean accept, String lang) {
        Locale locale = new Locale(lang, "");
        if (accept) {
            return inviteWorkspaceAccept(workspaceId, userId, locale);
        } else {
            return inviteWorkspaceReject(workspaceId, userId, locale);
        }
    }*/
/*
    public RedirectView inviteWorkspaceAccept(String workspaceId, String userId, Locale locale) {
        UserInvite userInvite = userInviteRepository.findById(userId + "-" + workspaceId)
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_NOT_FOUND_INVITE_WORKSPACE_INFO));
        Workspace workspace = workspaceRepository.findByUuid(workspaceId)
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));

        //메일 발송 수신자 : 마스터 유저, 매니저 유저
        List<String> emailReceiverList = new ArrayList<>();
        UserInfoRestResponse masterUser = userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
        emailReceiverList.add(masterUser.getEmail());

        List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        if (workspaceUserPermissionList != null) {
            workspaceUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUser = userRestService.getUserInfoByUserId(workspace.getUserId())
                        .getData();
                emailReceiverList.add(managerUser.getEmail());
            });
        }

        //이미 마스터, 매니저, 멤버로 소속되어 있는 워크스페이스 최대 개수 9개 체크 <-- 제한 수 없어짐
        *//*if (workspaceUserRepository.countWorkspaceUsersByUserId(userId) > 8) {
            Context context = new Context();
            context.setVariable("workspaceName", workspace.getName());
            context.setVariable("workspaceMasterNickName", masterUser.getNickname());
            context.setVariable("workspaceMasterEmail", masterUser.getEmail());
            context.setVariable("userNickName", userInvite.getResponseUserNickName());
            context.setVariable("userEmail", userInvite.getResponseUserEmail());
            context.setVariable("plan", generatePlanString(userInvite.getPlanRemote(), userInvite.getPlanMake(), userInvite.getPlanView()));
            context.setVariable("planRemoteType", userInvite.getPlanRemoteType());
            context.setVariable("planMakeType", userInvite.getPlanMakeType());
            context.setVariable("planViewType", userInvite.getPlanViewType());
            context.setVariable("workstationHomeUrl", redirectUrl);
            context.setVariable("workstationMembersUrl", redirectUrl + "/members");
            context.setVariable("supportUrl", supportUrl);

            String subject = messageSource.getMessage(Mail.WORKSPACE_OVER_JOIN_FAIL.getSubject(), null, locale);
            String template = messageSource.getMessage(Mail.WORKSPACE_OVER_JOIN_FAIL.getTemplate(), null, locale);
            String html = springTemplateEngine.process(template, context);

            sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

            userInviteRepository.deleteById(userId + "-" + workspaceId);

            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectUrl + RedirectPath.WORKSPACE_OVER_JOIN_FAIL.getValue());
            redirectView.setContentType("application/json");
            return redirectView;
        }*//*


        //라이선스 플랜 - 라이선스 플랜 보유 체크, 멤버 제한 수 체크
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(
                workspaceId).getData();
        if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() == null
                || workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_WORKSPACE_LICENSE_PLAN);
        }

        int workspaceUserAmount = workspaceUserRepository.findByWorkspace_Uuid(workspaceId).size();

        if (workspaceLicensePlanInfoResponse.getMaxUserAmount() < workspaceUserAmount + 1) {
            Context context = new Context();
            context.setVariable("workspaceName", workspace.getName());
            context.setVariable("workspaceMasterNickName", masterUser.getNickname());
            context.setVariable("workspaceMasterEmail", masterUser.getEmail());
            context.setVariable("userNickName", userInvite.getResponseUserNickName());
            context.setVariable("userEmail", userInvite.getResponseUserEmail());
            context.setVariable(
                    "plan",
                    generatePlanString(userInvite.getPlanRemote(), userInvite.getPlanMake(), userInvite.getPlanView())
            );
            context.setVariable("planRemoteType", userInvite.getPlanRemoteType());
            context.setVariable("planMakeType", userInvite.getPlanMakeType());
            context.setVariable("planViewType", userInvite.getPlanViewType());
            context.setVariable("contactUrl", contactUrl);
            context.setVariable("workstationHomeUrl", redirectUrl);
            context.setVariable("supportUrl", supportUrl);

            String subject = messageSource.getMessage(
                    Mail.WORKSPACE_OVER_MAX_USER_FAIL.getSubject(), null, locale);
            String template = messageSource.getMessage(
                    Mail.WORKSPACE_OVER_MAX_USER_FAIL.getTemplate(), null, locale);
            String html = springTemplateEngine.process(template, context);
            sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

            userInviteRepository.deleteById(userId + "-" + workspaceId);
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectUrl + RedirectPath.WORKSPACE_OVER_MAX_USER_FAIL.getValue());
            redirectView.setContentType("application/json");
            return redirectView;
        }
        //플랜 할당.
        boolean planRemoteGrantResult = true, planMakeGrantResult = true, planViewGrantResult = true;
        List<String> successPlan = new ArrayList<>();
        List<String> failPlan = new ArrayList<>();

        if (userInvite.getPlanRemote()) {
            MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(
                    workspaceId, userId, LicenseProduct.REMOTE.toString()).getData();
            if (!grantResult.getProductName().equals(LicenseProduct.REMOTE.toString())) {
                planRemoteGrantResult = false;
                failPlan.add("REMOTE");
            } else {
                successPlan.add("REMOTE");
            }
        }
        if (userInvite.getPlanMake()) {
            MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(
                    workspaceId, userId, LicenseProduct.MAKE.toString()).getData();
            if (!grantResult.getProductName().equals(LicenseProduct.MAKE.toString())) {
                planMakeGrantResult = false;
                failPlan.add("MAKE");
            } else {
                successPlan.add("MAKE");
            }
        }
        if (userInvite.getPlanView()) {
            MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(
                    workspaceId, userId, LicenseProduct.VIEW.toString()).getData();
            if (!grantResult.getProductName().equals(LicenseProduct.VIEW.toString())) {
                planViewGrantResult = false;
                failPlan.add("VIEW");
            } else {
                successPlan.add("VIEW");
            }
        }

        if (!planRemoteGrantResult || !planMakeGrantResult || !planViewGrantResult) {
            Context context = new Context();
            context.setVariable("workspaceName", workspace.getName());
            context.setVariable("workspaceMasterNickName", masterUser.getNickname());
            context.setVariable("workspaceMasterEmail", masterUser.getEmail());
            context.setVariable("userNickName", userInvite.getResponseUserNickName());
            context.setVariable("userEmail", userInvite.getResponseUserEmail());
            context.setVariable("successPlan", org.apache.commons.lang.StringUtils.join(successPlan, ","));
            context.setVariable("failPlan", org.apache.commons.lang.StringUtils.join(failPlan, ","));
            context.setVariable("planRemoteType", userInvite.getPlanRemoteType());
            context.setVariable("planMakeType", userInvite.getPlanMakeType());
            context.setVariable("planViewType", userInvite.getPlanViewType());
            context.setVariable("workstationHomeUrl", redirectUrl);
            context.setVariable("workstationMembersUrl", redirectUrl + "/members");
            context.setVariable("supportUrl", supportUrl);

            String subject = messageSource.getMessage(Mail.WORKSPACE_OVER_PLAN_FAIL.getSubject(), null, locale);
            String template = messageSource.getMessage(Mail.WORKSPACE_OVER_PLAN_FAIL.getTemplate(), null, locale);
            String html = springTemplateEngine.process(template, context);
            sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

            userInviteRepository.deleteById(userId + "-" + workspaceId);

            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(redirectUrl + RedirectPath.WORKSPACE_OVER_PLAN_FAIL.getValue());
            redirectView.setContentType("application/json");
            return redirectView;

        }
        //워크스페이스 소속 넣기 (workspace_user)
        WorkspaceUser workspaceUser = WorkspaceUser.builder().workspace(workspace).userId(userId).build();
        workspaceUserRepository.save(workspaceUser);

        //워크스페이스 권한 부여하기 (workspace_user_permission)
        WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(userInvite.getRole().toUpperCase()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
        WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceUser(workspaceUser)
                .workspaceRole(workspaceRole)
                .workspacePermission(workspacePermission)
                .build();

        workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

        //MAIL 발송
        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUser.getNickname());
        context.setVariable("workspaceMasterEmail", masterUser.getEmail());
        context.setVariable("acceptUserNickName", userInvite.getResponseUserNickName());
        context.setVariable("acceptUserEmail", userInvite.getResponseUserEmail());
        context.setVariable("role", userInvite.getRole());
        context.setVariable("workstationHomeUrl", redirectUrl);
        context.setVariable(
                "plan", generatePlanString(userInvite.getPlanRemote(), userInvite.getPlanMake(), userInvite.getPlanView()));
        context.setVariable("supportUrl", supportUrl);

        String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE_ACCEPT.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_INVITE_ACCEPT.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);
        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        //redis 에서 삭제
        userInviteRepository.deleteById(userId + "-" + workspaceId);

        //history 저장
        String message;
        if (workspaceRole.getRole().equalsIgnoreCase("MANAGER")) {
            message = messageSource.getMessage(
                    "WORKSPACE_INVITE_MANAGER", new String[]{userInvite.getResponseUserNickName(),
                            generatePlanString(userInvite.getPlanRemote(), userInvite.getPlanMake(), userInvite.getPlanView())},
                    locale
            );
        } else {
            message = messageSource.getMessage(
                    "WORKSPACE_INVITE_MEMBER", new String[]{userInvite.getResponseUserNickName(),
                            generatePlanString(userInvite.getPlanRemote(), userInvite.getPlanMake(), userInvite.getPlanView())},
                    locale
            );
        }
        History history = History.builder()
                .message(message)
                .userId(userInvite.getResponseUserId())
                .workspace(workspace)
                .build();
        historyRepository.save(history);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        redirectView.setContentType("application/json");
        return redirectView;

    }


    public RedirectView inviteWorkspaceReject(String workspaceId, String userId, Locale locale) {
        //REDIS 에서 초대정보 조회
        UserInvite userInvite = userInviteRepository.findById(userId + "-" + workspaceId)
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_NOT_FOUND_INVITE_WORKSPACE_INFO));
        Workspace workspace = workspaceRepository.findByUuid(workspaceId)
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));

        //워크스페이스 초대 거절 메일 수신자 : 마스터, 매니저
        List<String> emailReceiverList = new ArrayList<>();
        UserInfoRestResponse masterUser = userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
        emailReceiverList.add(masterUser.getEmail());

        List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(
                workspace, "MANAGER");
        if (workspaceUserPermissionList != null) {
            workspaceUserPermissionList.forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUser = userRestService.getUserInfoByUserId(workspace.getUserId())
                        .getData();
                emailReceiverList.add(managerUser.getEmail());
            });
        }

        //redis에서 삭제
        userInviteRepository.deleteById(userId + "-" + workspaceId);

        //MAIL 발송
        Context context = new Context();
        context.setVariable("rejectUserNickname", userInvite.getResponseUserNickName());
        context.setVariable("rejectUserEmail", userInvite.getResponseUserEmail());
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("accountUrl", accountUrl);
        context.setVariable("supportUrl", supportUrl);

        String subject = messageSource.getMessage(Mail.WORKSPACE_INVITE_REJECT.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_INVITE_REJECT.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);
        sendMailRequest(html, emailReceiverList, MailSender.MASTER.getValue(), subject);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        redirectView.setContentType("application/json");
        return redirectView;
    }*/

    /**
     * 권한 변경 기능
     * 권한 변경에는 워크스페이스 내의 유저 권한 변경, 플랜 변경이 있음.
     * 유저 권한 변경은 해당 워크스페이스의 '마스터'유저만 가능함.('매니저' to '멤버', '멤버' to '매니저')
     * 플랜 변경은 해당 워크스페이스 '마스터', '매니저'유저 가 가능함.
     * 이때 주의점은 어느 유저든지 간에 최소 1개 이상의 제품라이선스를 보유하고 있어야 함.
     *
     * @param workspaceId         - 권한 변경이 이루어지는 워크스페이스의 식별자
     * @param memberUpdateRequest - 권한 변경 요청 정보
     * @param locale              - 언어 정보
     * @return - 변경 성공 여부
     */
    @Transactional
    public ApiResponse<Boolean> reviseMemberInfo(
            String workspaceId, MemberUpdateRequest memberUpdateRequest, Locale locale
    ) {

        Workspace workspace = workspaceRepository.findByUuid(workspaceId)
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission userPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
                workspace, memberUpdateRequest.getUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

        WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(memberUpdateRequest.getRole()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
        UserInfoRestResponse masterUser = getUserInfo(workspace.getUserId());
        UserInfoRestResponse user = getUserInfo(memberUpdateRequest.getUserId());
        UserInfoRestResponse requestUser = getUserInfo(memberUpdateRequest.getRequestUserId());

        //권한 변경
        if (!userPermission.getWorkspaceRole().equals(workspaceRole)) {
            updateUserPermission(
                    workspace, memberUpdateRequest.getRequestUserId(), memberUpdateRequest.getUserId(), workspaceRole,
                    masterUser, user, locale
            );
        }

        //플랜 변경
        workspaceUserLicenseHandling(
                memberUpdateRequest.getUserId(), workspace, masterUser, user, requestUser,
                memberUpdateRequest.getLicenseRemote(), memberUpdateRequest.getLicenseMake(),
                memberUpdateRequest.getLicenseView(), locale
        );

        return new ApiResponse<>(true);
    }

    @Transactional
    public void workspaceUserLicenseHandling(
            String userId, Workspace workspace, UserInfoRestResponse masterUser, UserInfoRestResponse user,
            UserInfoRestResponse requestUser, Boolean remoteLicense, Boolean makeLicense, Boolean
                    viewLicense, Locale locale
    ) {
        //라이선스 할당 체크
        List<String> requestProductList = userLicenseValidCheck(remoteLicense, makeLicense, viewLicense);

        //라이선스 변경 권한 체크 - MASTER, MANAGER 에게만 있음.
        checkWorkspaceAndUserRole(workspace.getUuid(), requestUser.getUuid(), new String[]{"MASTER", "MANAGER"});

        //사용자의 예전 라이선스정보 가져오기
        MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(workspace.getUuid(), userId).getData();
        List<String> oldProductList = new ArrayList<>();
        if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList()
                .isEmpty()) {
            for (MyLicenseInfoResponse myLicenseInfoResponse : myLicenseInfoListResponse.getLicenseInfoList()) {
                oldProductList.add(myLicenseInfoResponse.getProductName());
            }
        }
        List<String> removedProductList = oldProductList.stream().filter(s -> !requestProductList.contains(s)).collect(Collectors.toList());
        List<String> addedProductList = requestProductList.stream().filter(s -> !oldProductList.contains(s)).collect(Collectors.toList());
        oldProductList.removeAll(removedProductList);
        List<String> updatedProductList = Stream.concat(oldProductList.stream(), addedProductList.stream()).distinct().collect(Collectors.toList());

        log.info("[REVISE MEMBER INFO] Revise License Info. removed License Product Info >> [{}], added License Product Info >> [{}], result License Product Info >> [{}].",
                org.apache.commons.lang.StringUtils.join(removedProductList, ","),
                org.apache.commons.lang.StringUtils.join(addedProductList, ","),
                org.apache.commons.lang.StringUtils.join(updatedProductList, ","));

        if (!removedProductList.isEmpty()) {
            removedProductList.forEach(productName -> {
                Boolean revokeResult = licenseRestService.revokeWorkspaceLicenseToUser(
                        workspace.getUuid(), userId, productName).getData();
                if (!revokeResult) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_REVOKE_FAIL);
                }
            });
            String message = messageSource.getMessage(
                    "WORKSPACE_REVOKE_LICENSE",
                    new String[]{requestUser.getNickname(), user.getNickname(), org.apache.commons.lang.StringUtils.join(removedProductList, ",")},
                    locale
            );
            saveHistotry(workspace, userId, message);
        }

        if (!addedProductList.isEmpty()) {
            addedProductList.forEach(productName -> {
                MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(
                        workspace.getUuid(), userId, productName).getData();
                if (!grantResult.getProductName().equals(productName)) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
            });
            String message = messageSource.getMessage(
                    "WORKSPACE_GRANT_LICENSE",
                    new String[]{requestUser.getNickname(), user.getNickname(), org.apache.commons.lang.StringUtils.join(addedProductList, ",")}, locale
            );
            saveHistotry(workspace, userId, message);
        }

        if (!addedProductList.isEmpty() || !removedProductList.isEmpty()) {
            //메일 발송
            Context context = new Context();
            context.setVariable("workspaceName", workspace.getName());
            context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
            context.setVariable("workspaceMasterNickName", masterUser.getNickname());
            context.setVariable("workspaceMasterEmail", masterUser.getEmail());
            context.setVariable("responseUserNickName", user.getNickname());
            context.setVariable("responseUserEmail", user.getEmail());
            context.setVariable("supportUrl", redirectProperty.getSupportWeb());
            context.setVariable("plan", org.apache.commons.lang.StringUtils.join(updatedProductList, ","));

            List<String> receiverEmailList = new ArrayList<>();
            receiverEmailList.add(user.getEmail());
            String subject = messageSource.getMessage(Mail.WORKSPACE_USER_PLAN_UPDATE.getSubject(), null, locale);
            String template = messageSource.getMessage(
                    Mail.WORKSPACE_USER_PLAN_UPDATE.getTemplate(), null, locale);
            String html = springTemplateEngine.process(template, context);
            sendMailRequest(html, receiverEmailList, MailSender.MASTER.getValue(), subject);
        }
    }

    @CacheEvict(value = "userWorkspaces", key = "#requestUserId")
    @Transactional
    public void updateUserPermission(
            Workspace workspace, String requestUserId, String responseUserId, WorkspaceRole workspaceRole,
            UserInfoRestResponse masterUser, UserInfoRestResponse user, Locale locale
    ) {
        //1. 요청자 권한 확인(마스터만 가능)
        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
                workspace, requestUserId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        String role = workspaceUserPermission.getWorkspaceRole().getRole();
        if (role == null || !role.equalsIgnoreCase("MASTER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //2. 대상자 권한 확인(매니저, 멤버 권한만 가능)
        WorkspaceUserPermission userPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
                workspace, responseUserId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        String userRole = userPermission.getWorkspaceRole().getRole();
        if (userRole == null || userRole.equalsIgnoreCase("MASTER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //3. 권한 변경
        userPermission.setWorkspaceRole(workspaceRole);
        workspaceUserPermissionRepository.save(userPermission);

        // 메일 발송
        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUser.getNickname());
        context.setVariable("workspaceMasterEmail", masterUser.getEmail());
        context.setVariable("responseUserNickName", user.getNickname());
        context.setVariable("responseUserEmail", user.getEmail());
        context.setVariable("role", workspaceRole.getRole());
        context.setVariable("workstationHomeUrl", redirectProperty.getWorkstationWeb());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        List<String> receiverEmailList = new ArrayList<>();
        receiverEmailList.add(user.getEmail());
        String subject = messageSource.getMessage(
                Mail.WORKSPACE_USER_PERMISSION_UPDATE.getSubject(), null, locale);
        String template = messageSource.getMessage(
                Mail.WORKSPACE_USER_PERMISSION_UPDATE.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);

        sendMailRequest(html, receiverEmailList, MailSender.MASTER.getValue(), subject);

        // 히스토리 적재
        String message;
        if (workspaceRole.getRole().equalsIgnoreCase("MANAGER")) {
            message = messageSource.getMessage(
                    "WORKSPACE_SET_MANAGER", new java.lang.String[]{masterUser.getNickname(), user.getNickname()}, locale);
        } else {
            message = messageSource.getMessage(
                    "WORKSPACE_SET_MEMBER", new java.lang.String[]{masterUser.getNickname(), user.getNickname()}, locale);
        }
        saveHistotry(workspace, responseUserId, message);

    }

    private void saveHistotry(Workspace workspace, String userId, String message) {
        History history = History.builder()
                .workspace(workspace)
                .userId(userId)
                .message(message)
                .build();
        historyRepository.save(history);
    }

    private List<String> userLicenseValidCheck(boolean planRemote, boolean planMake, boolean planView) {
        if (!planRemote && !planMake && !planView) {
            throw new WorkspaceException(ErrorCode.ERR_INCORRECT_USER_LICENSE_INFO);
        }
        List<String> licenseProductList = new ArrayList<>();
        if (planRemote) {
            licenseProductList.add(LicenseProduct.REMOTE.toString());
        }
        if (planMake) {
            licenseProductList.add(LicenseProduct.MAKE.toString());
        }
        if (planView) {
            licenseProductList.add(LicenseProduct.VIEW.toString());
        }
        return licenseProductList;
    }

    public List<WorkspaceNewMemberInfoDTO> getWorkspaceNewUserInfo(String workspaceId) {
        List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.findRecentWorkspaceUserList(4, workspaceId);
        List<WorkspaceNewMemberInfoDTO> workspaceNewMemberInfoList = new ArrayList<>();
        for (WorkspaceUserPermission workspaceUserPermission : workspaceUserPermissionList) {
            UserInfoRestResponse userInfoRestResponse = getUserInfo(workspaceUserPermission.getWorkspaceUser().getUserId());
            WorkspaceNewMemberInfoDTO newMemberInfo = modelMapper.map(userInfoRestResponse, WorkspaceNewMemberInfoDTO.class);
            newMemberInfo.setJoinDate(workspaceUserPermission.getWorkspaceUser().getCreatedDate());
            newMemberInfo.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            workspaceNewMemberInfoList.add(newMemberInfo);
        }
        return workspaceNewMemberInfoList;
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

        WorkspaceInfoDTO workspaceInfoDTO = modelMapper.map(workspace, WorkspaceInfoDTO.class);
        workspaceInfoDTO.setMasterUserId(workspace.getUserId());

        return workspaceInfoDTO;
    }

    public UserInfoDTO getMemberInfo(String workspaceId, String userId) {
        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, userId).orElse(null);
        if (workspaceUserPermission == null) {
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            return userInfoDTO;
        }
        UserInfoRestResponse userInfoRestResponse = getUserInfo(userId);
        UserInfoDTO userInfoDTO = modelMapper.map(userInfoRestResponse, UserInfoDTO.class);
        userInfoDTO.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
        userInfoDTO.setLicenseProducts(getUserLicenseProductList(workspaceId, userId));
        return userInfoDTO;
    }

    @CacheEvict(value = "userWorkspaces", key = "#memberKickOutRequest.kickedUserId")
    @Transactional
    public ApiResponse<Boolean> kickOutMember(
            String workspaceId, MemberKickOutRequest memberKickOutRequest, Locale locale
    ) {
        log.debug(
                "[WORKSPACE KICK OUT USER] Workspace >> {}, Kickout User >> {}, Request User >> {}", workspaceId,
                memberKickOutRequest.getKickedUserId(),
                memberKickOutRequest.getUserId()
        );
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));

        /**
         * 권한체크
         * 내보내는 사람 권한 - 마스터, 매니저만 가능
         * 내쫓기는 사람 권한 - 매니저, 멤버만 가능
         * 내보내는 사람이 매니저일때 - 멤버만 내보낼 수 있음
         */

        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
                workspace, memberKickOutRequest.getUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        WorkspaceUserPermission kickedUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
                workspace, memberKickOutRequest.getKickedUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        log.debug("[WORKSPACE KICK OUT USER] Request user Role >> [{}], Response user Role >> [{}]", workspaceUserPermission.getWorkspaceRole().getRole(),
                kickedUserPermission.getWorkspaceRole().getRole());
        //내보내는 자의 권한 확인(마스터, 매니저만 가능)
        if (workspaceUserPermission.getWorkspaceRole().getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        //내쫓기는 자의 권한 확인(매니저, 멤버만 가능)
        if (kickedUserPermission.getWorkspaceRole().getRole().equals("MASTER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //내보내는 사람이 매니저일때는 멤버만 가능. = 내쫓기는 사람이 매니저일때는 마스터만 가능 = 매니저는 매니저를 내보낼 수 없음.
        if (workspaceUserPermission.getWorkspaceRole().getRole().equals("MANAGER") && kickedUserPermission.getWorkspaceRole().getRole().equals("MANAGER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //라이선스 해제
        MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(
                workspaceId, memberKickOutRequest.getKickedUserId()).getData();
        if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList()
                .

                        isEmpty()) {
            myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                log.debug(
                        "[WORKSPACE KICK OUT USER] Workspace User License Revoke. License Product Name >> {}",
                        myLicenseInfoResponse.getProductName()
                );
                boolean revokeResult = licenseRestService.revokeWorkspaceLicenseToUser(
                        workspaceId, memberKickOutRequest.getKickedUserId(), myLicenseInfoResponse.getProductName())
                        .getData();

                if (!revokeResult) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_REVOKE_FAIL);
                }
            });
        }

        //workspace_user_permission 삭제(history 테이블 기록)
        workspaceUserPermissionRepository.delete(kickedUserPermission);
        log.debug("[WORKSPACE KICK OUT USER] Delete Workspace user permission info.");

        //workspace_user 삭제(history 테이블 기록)
        workspaceUserRepository.delete(kickedUserPermission.getWorkspaceUser());
        log.debug("[WORKSPACE KICK OUT USER] Delete Workspace user info.");

        //메일 발송
        UserInfoRestResponse masterUser = userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
        UserInfoRestResponse kickedUser = getUserInfo(memberKickOutRequest.getKickedUserId());
        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUser.getNickname());
        context.setVariable("workspaceMasterEmail", masterUser.getEmail());
        context.setVariable("supportUrl", redirectProperty.getSupportWeb());

        List<String> receiverEmailList = new ArrayList<>();
        receiverEmailList.add(kickedUser.getEmail());

        String subject = messageSource.getMessage(Mail.WORKSPACE_KICKOUT.getSubject(), null, locale);
        String template = messageSource.getMessage(Mail.WORKSPACE_KICKOUT.getTemplate(), null, locale);
        String html = springTemplateEngine.process(template, context);

        sendMailRequest(html, receiverEmailList, MailSender.MASTER.getValue(), subject);
        log.debug("[WORKSPACE KICK OUT USER] Send Workspace kick out mail.");

        //history 저장
        String message = messageSource.getMessage(
                "WORKSPACE_EXPELED", new String[]{masterUser.getNickname(), kickedUser.getNickname()}, locale);
        History history = History.builder()
                .message(message)
                .userId(kickedUser.getUuid())
                .workspace(workspace)
                .build();
        historyRepository.save(history);

        return new ApiResponse<>(true);
    }

    @CacheEvict(value = "userWorkspaces", key = "#userId")
    public ApiResponse<Boolean> exitWorkspace(String workspaceId, String userId, Locale locale) {
        Workspace workspace = workspaceRepository.findByUuid(workspaceId)
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        //마스터 유저는 워크스페이스 나가기를 할 수 없음.
        if (workspace.getUserId().equals(userId)) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, userId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

        //라이선스 해제
        MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(
                workspaceId, userId).getData();
        if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList()
                .isEmpty()) {
            myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                boolean revokeResult = licenseRestService.revokeWorkspaceLicenseToUser(
                        workspaceId, userId, myLicenseInfoResponse.getProductName()).getData();
                if (!revokeResult) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_REVOKE_FAIL);
                }
            });
        }

        workspaceUserPermissionRepository.delete(workspaceUserPermission);
        workspaceUserRepository.delete(workspaceUserPermission.getWorkspaceUser());

        //history 저장
        UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(userId).getData();
        String message = messageSource.getMessage(
                "WORKSPACE_LEAVE", new String[]{userInfoRestResponse.getNickname()}, locale);
        History history = History.builder()
                .message(message)
                .userId(userId)
                .workspace(workspace)
                .build();
        historyRepository.save(history);

        return new ApiResponse<>(true);
    }

    public ApiResponse<WorkspaceHistoryListResponse> getWorkspaceHistory(
            String workspaceId, String userId, Pageable pageable
    ) {

        Page<History> historyPage = historyRepository.findAllByUserIdAndWorkspace_Uuid(
                userId, workspaceId, pageable);
        List<WorkspaceHistoryListResponse.WorkspaceHistory> workspaceHistoryList = historyPage.stream().map(history -> modelMapper.map(history, WorkspaceHistoryListResponse.WorkspaceHistory.class)).collect(Collectors.toList());

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(historyPage.getTotalElements());
        pageMetadataResponse.setTotalPage(historyPage.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageable.getPageNumber());
        pageMetadataResponse.setCurrentSize(pageable.getPageSize());

        return new ApiResponse<>(new WorkspaceHistoryListResponse(workspaceHistoryList, pageMetadataResponse));
    }

    public MemberListResponse getSimpleWorkspaceUserList(String workspaceId) {
        List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findByWorkspace_Uuid(workspaceId);
        String[] workspaceUserIdList = workspaceUserList.stream()
                .map(WorkspaceUser::getUserId)
                .toArray(String[]::new);
        List<MemberInfoDTO> memberInfoDTOList = new ArrayList<>();

        UserInfoListRestResponse userInfoListRestResponse = userRestService.getUserInfoList(
                "", workspaceUserIdList).getData();
        userInfoListRestResponse.getUserInfoList().forEach(userInfoRestResponse -> {
            MemberInfoDTO memberInfoDTO = modelMapper.map(userInfoRestResponse, MemberInfoDTO.class);
            memberInfoDTOList.add(memberInfoDTO);
        });

        return new MemberListResponse(memberInfoDTOList, null);
    }

    /**
     * 워크스페이스 소속 멤버 플랜 리스트 조회
     *
     * @param workspaceId - 조회 대상 워크스페이스 식별자
     * @param pageable    -  페이징
     * @return - 멤버 플랜 리스트
     */
    public WorkspaceUserLicenseListResponse getLicenseWorkspaceUserList(
            String workspaceId, Pageable pageable
    ) {
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(
                workspaceId).getData();
        if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() == null
                || workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
            throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_WORKSPACE_LICENSE_PLAN);
        }

        List<WorkspaceUserLicenseInfoResponse> workspaceUserLicenseInfoList = new ArrayList<>();

        workspaceLicensePlanInfoResponse.getLicenseProductInfoList()
                .forEach(licenseProductInfoResponse -> {
                    licenseProductInfoResponse.getLicenseInfoList().stream()
                            .filter(licenseInfoResponse -> licenseInfoResponse.getStatus().equals(LicenseStatus.USE))
                            .forEach(licenseInfoResponse -> {
                                UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(licenseInfoResponse.getUserId()).getData();
                                WorkspaceUserLicenseInfoResponse workspaceUserLicenseInfo = modelMapper.map(userInfoRestResponse, WorkspaceUserLicenseInfoResponse.class);
                                workspaceUserLicenseInfo.setLicenseType(licenseProductInfoResponse.getLicenseType());
                                workspaceUserLicenseInfo.setProductName(licenseProductInfoResponse.getProductName());
                                workspaceUserLicenseInfoList.add(workspaceUserLicenseInfo);
                            });
                });
        if (workspaceUserLicenseInfoList.isEmpty()) {
            PageMetadataRestResponse pageMetadataRestResponse = new PageMetadataRestResponse();
            pageMetadataRestResponse.setCurrentPage(pageable.getPageNumber());
            pageMetadataRestResponse.setCurrentSize(pageable.getPageSize());
            pageMetadataRestResponse.setTotalElements(0);
            pageMetadataRestResponse.setTotalPage(0);
            return new WorkspaceUserLicenseListResponse(workspaceUserLicenseInfoList, new PageMetadataRestResponse());
        }
        List<WorkspaceUserLicenseInfoResponse> beforeWorkspaceUserLicenseList = new ArrayList<>();

        //sort
        String sortName = pageable.getSort().toString().split(":")[0].trim();//sort의 기준이 될 열
        String sortDirection = pageable.getSort().toString().split(":")[1].trim();//sort의 방향 : 내림차순 or 오름차순

        if (sortName.equalsIgnoreCase("plan") && sortDirection.equalsIgnoreCase("asc")) {
            beforeWorkspaceUserLicenseList = workspaceUserLicenseInfoList.stream()
                    .sorted(Comparator.comparing(
                            WorkspaceUserLicenseInfoResponse::getProductName,
                            Comparator.nullsFirst(Comparator.naturalOrder())
                    ))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("plan") && sortDirection.equalsIgnoreCase("desc")) {
            beforeWorkspaceUserLicenseList = workspaceUserLicenseInfoList.stream()
                    .sorted(Comparator.comparing(
                            WorkspaceUserLicenseInfoResponse::getProductName,
                            Comparator.nullsFirst(Comparator.reverseOrder())
                    ))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("nickName") && sortDirection.equalsIgnoreCase("asc")) {
            beforeWorkspaceUserLicenseList = workspaceUserLicenseInfoList.stream()
                    .sorted(Comparator.comparing(
                            WorkspaceUserLicenseInfoResponse::getNickName,
                            Comparator.nullsFirst(Comparator.naturalOrder())
                    ))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("nickName") && sortDirection.equalsIgnoreCase("desc")) {
            beforeWorkspaceUserLicenseList = workspaceUserLicenseInfoList.stream()
                    .sorted(Comparator.comparing(
                            WorkspaceUserLicenseInfoResponse::getNickName,
                            Comparator.nullsFirst(Comparator.reverseOrder())
                    ))
                    .collect(Collectors.toList());
        }
        //WorkspaceUserLicenseListResponse workspaceUserLicenseListResponse = paging(pageable.getPageNumber(), pageable.getPageSize(), beforeWorkspaceUserLicenseList);
        CustomPageHandler<WorkspaceUserLicenseInfoResponse> customPageHandler = new CustomPageHandler<>();
        CustomPageResponse customPageResponse = customPageHandler.paging(pageable.getPageNumber(), pageable.getPageSize(), beforeWorkspaceUserLicenseList);
        return new WorkspaceUserLicenseListResponse(customPageResponse.getAfterPagingList(), customPageResponse.getPageMetadataResponse());
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

    @Transactional
    public WorkspaceMemberInfoListResponse createWorkspaceMemberAccount(
            String workspaceId, MemberAccountCreateRequest memberAccountCreateRequest
    ) {
        //1. 요청한 사람의 권한 체크
        Workspace workspace = checkWorkspaceAndUserRole(
                workspaceId, memberAccountCreateRequest.getUserId(), new String[]{"MASTER", "MANAGER"});

        List<String> responseLicense = new ArrayList<>();
        List<MemberInfoDTO> memberInfoDTOList = new ArrayList<>();

        for (MemberAccountCreateInfo memberAccountCreateInfo : memberAccountCreateRequest.getMemberAccountCreateRequest()) {
            //1-1. 사용자에게 최소 1개 이상의 라이선스를 부여했는지 체크
            userLicenseValidCheck(memberAccountCreateInfo.isPlanRemote(), memberAccountCreateInfo.isPlanMake(),
                    memberAccountCreateInfo.isPlanView()
            );

            //2. user-server 멤버 정보 등록 api 요청
            RegisterMemberRequest registerMemberRequest = new RegisterMemberRequest();
            registerMemberRequest.setEmail(memberAccountCreateInfo.getId());
            registerMemberRequest.setPassword(memberAccountCreateInfo.getPassword());
            UserInfoRestResponse userInfoRestResponse = userRestService.registerMemberRequest(
                    registerMemberRequest,
                    serviceID
            )
                    .getData();

            if (userInfoRestResponse == null || !StringUtils.hasText(userInfoRestResponse.getUuid())) {
                log.error("[CREATE WORKSPACE MEMBER ACCOUNT] USER SERVER Member Register fail.");
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ACCOUNT_CREATE_FAIL);
            }
            log.info(
                    "[CREATE WORKSPACE MEMBER ACCOUNT] USER SERVER account register success. Create UUID : [{}], Create Date : [{}]",
                    userInfoRestResponse.getUuid(), userInfoRestResponse.getCreatedDate()
            );

            //3. license-server grant api 요청 -> 실패시 user-server 롤백 api 요청
            if (memberAccountCreateInfo.isPlanRemote()) {
                MyLicenseInfoResponse myLicenseInfoResponse = licenseRestService.grantWorkspaceLicenseToUser(
                        workspaceId, userInfoRestResponse.getUuid(), "REMOTE").getData();
                if (myLicenseInfoResponse == null || !StringUtils.hasText(myLicenseInfoResponse.getProductName())) {
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail. Request User UUID : [{}], Product License : [{}]",
                            userInfoRestResponse.getUuid(), "REMOTE"
                    );
                    UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(
                            userInfoRestResponse.getUuid(), serviceID).getData();
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail >>>> USER SERVER account delete process. Request User UUID : [{}], Delete Date : [{}]",
                            userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
                log.info(
                        "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant success. Request User UUID : [{}], Product License : [{}]",
                        userInfoRestResponse.getUuid(), myLicenseInfoResponse.getProductName()
                );
                responseLicense.add("REMOTE");
            }
            if (memberAccountCreateInfo.isPlanMake()) {
                MyLicenseInfoResponse myLicenseInfoResponse = licenseRestService.grantWorkspaceLicenseToUser(
                        workspaceId, userInfoRestResponse.getUuid(), "MAKE").getData();
                if (myLicenseInfoResponse == null || !StringUtils.hasText(myLicenseInfoResponse.getProductName())) {
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail. Request User UUID : [{}], Product License : [{}]",
                            userInfoRestResponse.getUuid(), "REMOTE"
                    );
                    UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(
                            userInfoRestResponse.getUuid(), serviceID).getData();
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail >>>> USER SERVER account delete process. Request User UUID : [{}], Delete Date : [{}]",
                            userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
                log.info(
                        "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant success. Request User UUID : [{}], Product License : [{}]",
                        userInfoRestResponse.getUuid(), myLicenseInfoResponse.getProductName()
                );
                responseLicense.add("MAKE");
            }
            if (memberAccountCreateInfo.isPlanView()) {
                MyLicenseInfoResponse myLicenseInfoResponse = licenseRestService.grantWorkspaceLicenseToUser(
                        workspaceId, userInfoRestResponse.getUuid(), "VIEW").getData();
                if (myLicenseInfoResponse == null || !StringUtils.hasText(myLicenseInfoResponse.getProductName())) {
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail. Request User UUID : [{}], Product License : [{}]",
                            userInfoRestResponse.getUuid(), "REMOTE"
                    );
                    UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(
                            userInfoRestResponse.getUuid(), serviceID).getData();
                    log.error(
                            "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant fail >>>> USER SERVER account delete process. Request User UUID : [{}], Delete Date : [{}]",
                            userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
                }
                log.info(
                        "[CREATE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license grant success. Request User UUID : [{}], Product License : [{}]",
                        userInfoRestResponse.getUuid(), myLicenseInfoResponse.getProductName()
                );
                responseLicense.add("VIEW");
            }

            //4. workspace 권한 및 소속 부여
            WorkspaceUser newWorkspaceUser = WorkspaceUser.builder()
                    .userId(userInfoRestResponse.getUuid())
                    .workspace(workspace)
                    .build();
            WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(memberAccountCreateInfo.getRole().toUpperCase()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
            WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_PERMISSION_NOT_FOUND));
            WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                    .workspaceUser(newWorkspaceUser)
                    .workspacePermission(workspacePermission)
                    .workspaceRole(workspaceRole)
                    .build();

            workspaceUserRepository.save(newWorkspaceUser);
            workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

            log.info(
                    "[CREATE WORKSPACE MEMBER ACCOUNT] Workspace add user success. Request User UUID : [{}], Role : [{}], JoinDate : [{}]",
                    userInfoRestResponse.getUuid(), workspaceRole.getRole(), newWorkspaceUser.getCreatedDate()
            );

            //5. response
            MemberInfoDTO memberInfoResponse = modelMapper.map(userInfoRestResponse, MemberInfoDTO.class);
            memberInfoResponse.setRole(newWorkspaceUserPermission.getWorkspaceRole().getRole());
            memberInfoResponse.setRoleId(newWorkspaceUserPermission.getWorkspaceRole().getId());
            memberInfoResponse.setJoinDate(newWorkspaceUser.getCreatedDate());
            memberInfoResponse.setLicenseProducts(responseLicense.toArray(new String[0]));
            memberInfoDTOList.add(memberInfoResponse);
        }

        return new WorkspaceMemberInfoListResponse(memberInfoDTOList);
    }

    @Transactional
    public boolean deleteWorkspaceMemberAccount(
            String workspaceId, MemberAccountDeleteRequest memberAccountDeleteRequest
    ) {
        //1. 요청한 사람의 권한 체크
        checkWorkspaceAndUserRole(workspaceId, memberAccountDeleteRequest.getUserId(), new String[]{"MASTER"});

        //1-1. user-server로 권한 체크
        UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(
                memberAccountDeleteRequest.getUserId()).getData();
        if (userInfoRestResponse == null || !StringUtils.hasText(userInfoRestResponse.getUuid())) {
            log.error(
                    "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER account not found. Request user UUID : [{}]",
                    memberAccountDeleteRequest.getUserId()
            );
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        UserInfoAccessCheckRequest userInfoAccessCheckRequest = new UserInfoAccessCheckRequest();
        userInfoAccessCheckRequest.setEmail(userInfoRestResponse.getEmail());
        userInfoAccessCheckRequest.setPassword(memberAccountDeleteRequest.getUserPassword());
        UserInfoAccessCheckResponse userInfoAccessCheckResponse = userRestService.userInfoAccessCheckRequest(
                memberAccountDeleteRequest.getUserId(), userInfoAccessCheckRequest).getData();
        if (userInfoAccessCheckResponse == null || !userInfoAccessCheckResponse.isAccessCheckResult()) {
            log.error(
                    "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER account invalid. Request user UUID : [{}]",
                    memberAccountDeleteRequest.getUserId()
            );
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //2. license-sever revoke api 요청
        MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(
                workspaceId, memberAccountDeleteRequest.getDeleteUserId()).getData();

        if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList()
                .isEmpty()) {
            myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                Boolean revokeResult = licenseRestService.revokeWorkspaceLicenseToUser(
                        workspaceId,
                        memberAccountDeleteRequest.getDeleteUserId(),
                        myLicenseInfoResponse.getProductName()
                ).getData();
                if (!revokeResult) {
                    log.error(
                            "[DELETE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license revoke fail. Request user UUID : [{}], Product License [{}]",
                            memberAccountDeleteRequest.getUserId(),
                            myLicenseInfoResponse.getProductName()
                    );
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_REVOKE_FAIL);
                }
                log.info(
                        "[DELETE WORKSPACE MEMBER ACCOUNT] LICENSE SERVER license revoke success. Request user UUID : [{}], Product License [{}]",
                        memberAccountDeleteRequest.getUserId(),
                        myLicenseInfoResponse.getProductName()
                );
            });
        }

        //3. user-server에 멤버 삭제 api 요청 -> 실패시 grant api 요청
        UserDeleteRestResponse userDeleteRestResponse = userRestService.userDeleteRequest(
                memberAccountDeleteRequest.getDeleteUserId(), serviceID).getData();
        if (userDeleteRestResponse == null || !StringUtils.hasText(userDeleteRestResponse.getUserUUID())) {
            log.error("[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER delete user fail.");
            if (myLicenseInfoListResponse.getLicenseInfoList() != null
                    && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
                myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                    MyLicenseInfoResponse grantResult = licenseRestService.grantWorkspaceLicenseToUser(
                            workspaceId, memberAccountDeleteRequest.getDeleteUserId(),
                            myLicenseInfoResponse.getProductName()
                    ).getData();
                    log.error(
                            "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER delete user fail. >>>> LICENSE SERVER license revoke process. Request user UUID : [{}], Product License [{}]",
                            memberAccountDeleteRequest.getDeleteUserId(), grantResult.getProductName()
                    );
                });
            }
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ACCOUNT_DELETE_FAIL);
        }
        log.info(
                "[DELETE WORKSPACE MEMBER ACCOUNT] USER SERVER delete user success. Request user UUID : [{}],Delete Date [{}]",
                userDeleteRestResponse.getUserUUID(), userDeleteRestResponse.getDeletedDate()
        );

        //4. workspace-sever 권한 및 소속 해제
        Optional<Workspace> workspace = workspaceRepository.findByUuid(workspaceId);
        WorkspaceUser workspaceUser = workspaceUserRepository.findByUserIdAndWorkspace(
                memberAccountDeleteRequest.getDeleteUserId(), workspace.get());
        workspaceUserPermissionRepository.deleteAllByWorkspaceUser(workspaceUser);
        workspaceUserRepository.deleteById(workspaceUser.getId());

        log.info(
                "[DELETE WORKSPACE MEMBER ACCOUNT] Workspace delete user success. Request User UUID : [{}], Delete User UUID : [{}], DeleteDate : [{}]",
                memberAccountDeleteRequest.getUserId(), memberAccountDeleteRequest.getDeleteUserId(), LocalDateTime.now()
        );
        return true;
    }

    private Workspace checkWorkspaceAndUserRole(String workspaceId, String userId, String[] role) {
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, userId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

        log.info(
                "[CHECK WORKSPACE USER ROLE] Acceptable User Workspace Role : {}, Present User Role : [{}]",
                Arrays.toString(role),
                workspaceUserPermission.getWorkspaceRole().getRole()
        );
        if (Arrays.stream(role).noneMatch(workspaceUserPermission.getWorkspaceRole().getRole()::equals)){
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        return workspace;
    }

    public WorkspaceFaviconUpdateResponse updateWorkspaceFavicon(
            String workspaceId, WorkspaceFaviconUpdateRequest workspaceFaviconUpdateRequest
    ) {
        //1. 권한 체크
        Workspace workspace = checkWorkspaceAndUserRole(
                workspaceId, workspaceFaviconUpdateRequest.getUserId(), new String[]{"MASTER"});
        List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();
        WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

        //2. 파비콘 확장자, 사이즈 체크
        if (workspaceFaviconUpdateRequest.getFavicon() == null) {
            String favicon = fileUploadService.getFileUrl("virnect-default-favicon.ico");
            workspaceSetting.setFavicon(favicon);
            workspaceSettingRepository.save(workspaceSetting);
            WorkspaceFaviconUpdateResponse workspaceFaviconUpdateResponse = new WorkspaceFaviconUpdateResponse();
            workspaceFaviconUpdateResponse.setResult(true);
            workspaceFaviconUpdateResponse.setFavicon(favicon);
            return workspaceFaviconUpdateResponse;
        }
        String allowExtension = "jpg,jpeg,ico,png";
        String extension = FilenameUtils.getExtension(workspaceFaviconUpdateRequest.getFavicon().getOriginalFilename());
        checkFileSize(workspaceFaviconUpdateRequest.getFavicon().getSize());
        checkFileExtension(extension, allowExtension);

        //3. 파비콘 업로드
        try {
            String favicon = fileUploadService.upload(workspaceFaviconUpdateRequest.getFavicon());
            workspaceSetting.setFavicon(favicon);
            workspaceSettingRepository.save(workspaceSetting);

            WorkspaceFaviconUpdateResponse workspaceFaviconUpdateResponse = new WorkspaceFaviconUpdateResponse();
            workspaceFaviconUpdateResponse.setResult(true);
            workspaceFaviconUpdateResponse.setFavicon(favicon);
            return workspaceFaviconUpdateResponse;
        } catch (IOException e) {
            log.error("[UPDATE WORKSPACE FAVICON] Favicon Image upload fail. Error message >> [{}]", e.getMessage());
            WorkspaceFaviconUpdateResponse workspaceFaviconUpdateResponse = new WorkspaceFaviconUpdateResponse();
            workspaceFaviconUpdateResponse.setResult(false);
            return workspaceFaviconUpdateResponse;
        }
    }

    private void checkFileSize(long requestSize) {
        if (requestSize < 0 || requestSize > (long) 3145728) {
            log.error(
                    "[UPLOAD FILE SIZE CHECK] Acceptable File size : [{}], Present File size : [{}] ",
                    3145728, requestSize
            );
            throw new WorkspaceException(ErrorCode.ERR_NOT_ALLOW_FILE_SIZE);
        }
    }

    private void checkFileExtension(String requestExtension, String allowExtension) {
        if (!StringUtils.hasText(requestExtension) || !allowExtension.contains(requestExtension.toLowerCase())) {
            log.error(
                    "[UPLOAD FILE EXTENSION CHECK] Acceptable File extension : [{}], Present File extension : [{}] ",
                    allowExtension, requestExtension
            );
            throw new WorkspaceException(ErrorCode.ERR_NOT_ALLOW_FILE_EXTENSION);
        }
    }

    public WorkspaceLogoUpdateResponse updateWorkspaceLogo(
            String workspaceId, WorkspaceLogoUpdateRequest workspaceLogoUpdateRequest
    ) {
        //1. 권한 체크
        Workspace workspace = checkWorkspaceAndUserRole(
                workspaceId, workspaceLogoUpdateRequest.getUserId(), new String[]{"MASTER"});
        List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();
        WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

        //2. 로고 확장자, 사이즈 체크
        String allowExtension = "jpg,jpeg,gif,png";

        //3. default logo 업로드
        if (workspaceLogoUpdateRequest.getDefaultLogo() != null) {
            String defaultExtension = FilenameUtils.getExtension(
                    workspaceLogoUpdateRequest.getDefaultLogo().getOriginalFilename());
            checkFileSize(workspaceLogoUpdateRequest.getDefaultLogo().getSize());
            checkFileExtension(defaultExtension, allowExtension);

            try {
                String logo = fileUploadService.upload(workspaceLogoUpdateRequest.getDefaultLogo());
                workspaceSetting.setDefaultLogo(logo);
            } catch (IOException e) {
                log.error("[UPDATE WORKSPACE LOGO] Logo Image upload fail. Error message >> [{}]", e.getMessage());
                WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = new WorkspaceLogoUpdateResponse();
                workspaceLogoUpdateResponse.setResult(false);
                return workspaceLogoUpdateResponse;
            }
        } else {
            String logoDefault = fileUploadService.getFileUrl("virnect-default-logo.png");
            workspaceSetting.setDefaultLogo(logoDefault);
            workspaceSettingRepository.save(workspaceSetting);

        }

        //4. grey logo 업로드
        if (workspaceLogoUpdateRequest.getGreyLogo() != null) {
            String greyExtension = FilenameUtils.getExtension(
                    workspaceLogoUpdateRequest.getGreyLogo().getOriginalFilename());
            checkFileSize(workspaceLogoUpdateRequest.getGreyLogo().getSize());
            checkFileExtension(greyExtension, allowExtension);

            try {
                String logo = fileUploadService.upload(workspaceLogoUpdateRequest.getGreyLogo());
                workspaceSetting.setGreyLogo(logo);
            } catch (IOException e) {
                log.error("[UPDATE WORKSPACE LOGO] Logo Image upload fail. Error message >> [{}]", e.getMessage());
                WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = new WorkspaceLogoUpdateResponse();
                workspaceLogoUpdateResponse.setResult(false);
                return workspaceLogoUpdateResponse;
            }
        }

        //4. white logo 업로드
        if (workspaceLogoUpdateRequest.getWhiteLogo() != null) {
            String whiteExtension = FilenameUtils.getExtension(workspaceLogoUpdateRequest.getWhiteLogo().getOriginalFilename());
            checkFileSize(workspaceLogoUpdateRequest.getWhiteLogo().getSize());
            checkFileExtension(whiteExtension, allowExtension);

            try {
                String logo = fileUploadService.upload(workspaceLogoUpdateRequest.getWhiteLogo());
                workspaceSetting.setWhiteLogo(logo);
            } catch (IOException e) {
                log.error("[UPDATE WORKSPACE LOGO] Logo Image upload fail. Error message >> [{}]", e.getMessage());
                WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = new WorkspaceLogoUpdateResponse();
                workspaceLogoUpdateResponse.setResult(false);
                return workspaceLogoUpdateResponse;
            }
        } else {
            String logoWhite = fileUploadService.getFileUrl("virnect-white-logo.png");
            workspaceSetting.setWhiteLogo(logoWhite);
        }

        workspaceSettingRepository.save(workspaceSetting);

        WorkspaceLogoUpdateResponse workspaceLogoUpdateResponse = new WorkspaceLogoUpdateResponse();
        workspaceLogoUpdateResponse.setResult(true);
        workspaceLogoUpdateResponse.setDefaultLogo(workspaceSetting.getDefaultLogo());
        workspaceLogoUpdateResponse.setGreyLogo(workspaceSetting.getGreyLogo());
        workspaceLogoUpdateResponse.setWhiteLogo(workspaceSetting.getWhiteLogo());
        return workspaceLogoUpdateResponse;
    }

    public WorkspaceTitleUpdateResponse updateWorkspaceTitle(
            String workspaceId, WorkspaceTitleUpdateRequest workspaceTitleUpdateRequest
    ) {
        //1. 권한 체크
        Workspace workspace = checkWorkspaceAndUserRole(
                workspaceId, workspaceTitleUpdateRequest.getUserId(), new String[]{"MASTER"});
        List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();
        WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

        //2. 고객사명 변경
        workspaceSetting.setTitle(workspaceTitleUpdateRequest.getTitle());
        workspaceSettingRepository.save(workspaceSetting);

        WorkspaceTitleUpdateResponse workspaceTitleUpdateResponse = new WorkspaceTitleUpdateResponse();
        workspaceTitleUpdateResponse.setResult(true);
        workspaceTitleUpdateResponse.setTitle(workspaceSetting.getTitle());
        return workspaceTitleUpdateResponse;
    }

    public WorkspaceCustomSettingResponse getWorkspaceCustomSetting() {
        List<WorkspaceSetting> workspaceSettingList = workspaceSettingRepository.findAll();

        WorkspaceSetting workspaceSetting = workspaceSettingList.stream().findFirst()
                .orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));

        WorkspaceCustomSettingResponse workspaceCustomSettingResponse = new WorkspaceCustomSettingResponse();
        workspaceCustomSettingResponse.setWorkspaceTitle(workspaceSetting.getTitle());
        workspaceCustomSettingResponse.setDefaultLogo(workspaceSetting.getDefaultLogo());
        workspaceCustomSettingResponse.setGreyLogo(workspaceSetting.getGreyLogo());
        workspaceCustomSettingResponse.setWhiteLogo(workspaceSetting.getWhiteLogo());
        workspaceCustomSettingResponse.setFavicon(workspaceSetting.getFavicon());

        return workspaceCustomSettingResponse;
    }

    /**
     * 워크스페이스 멤버 비밀번호 변경
     *
     * @param passwordChangeRequest - 비밀번호 변경 요청 정보
     * @param workspaceId           - 워크스페이스 식별자 정보
     * @return - 워크스페이스 멤버 비밀번호 변경 처리 결과
     */
    @Transactional
    public WorkspaceMemberPasswordChangeResponse memberPasswordChange(
            WorkspaceMemberPasswordChangeRequest passwordChangeRequest,
            String workspaceId
    ) {
        checkWorkspaceAndUserRole(
                workspaceId, passwordChangeRequest.getMasterUUID(), new String[]{"MASTER"});
        MemberUserPasswordChangeRequest changeRequest = new MemberUserPasswordChangeRequest(
                passwordChangeRequest.getMemberUUID(), passwordChangeRequest.getPassword()
        );

        ApiResponse<MemberUserPasswordChangeResponse> responseMessage = userRestService.memberUserPasswordChangeRequest(
                serviceID, changeRequest
        );

        if (responseMessage.getCode() != 200 || !responseMessage.getData().isChanged()) {
            log.error("[USER SERVER PASSWORD CHANGE REST RESULT] - [code: {}, data:{}, message: {}]",
                    responseMessage.getCode(), responseMessage.getData() == null ? "" : responseMessage.getData(),
                    responseMessage.getMessage()
            );
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_PASSWORD_CHANGE);
        }

        return new WorkspaceMemberPasswordChangeResponse(
                passwordChangeRequest.getMasterUUID(),
                responseMessage.getData().getUuid(),
                responseMessage.getData().getPasswordChangedDate()
        );

    }

    public WorkspaceInfoListResponse getAllWorkspaceUserList(Pageable pageable) {
        Page<Workspace> workspaceList = workspaceRepository.findAll(pageable);
        List<WorkspaceInfoListResponse.WorkspaceInfo> workspaceInfoList = workspaceList.stream().map(workspace -> {
            WorkspaceInfoListResponse.WorkspaceInfo workspaceInfo = modelMapper.map(workspace, WorkspaceInfoListResponse.WorkspaceInfo.class);
            List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.findByWorkspaceUser_Workspace(workspace);
            List<MemberInfoDTO> memberInfoList = workspaceUserPermissionList.stream().map(workspaceUserPermission -> {
                WorkspaceUser workspaceUser = workspaceUserPermission.getWorkspaceUser();
                UserInfoRestResponse userInfoRestResponse = getUserInfo(workspaceUser.getUserId());
                MemberInfoDTO memberInfoDTO = modelMapper.map(userInfoRestResponse, MemberInfoDTO.class);
                memberInfoDTO.setJoinDate(workspaceUser.getCreatedDate());
                memberInfoDTO.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
                memberInfoDTO.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
                memberInfoDTO.setLicenseProducts(getUserLicenseProductList(workspaceUser.getWorkspace().getUuid(), workspaceUser.getUserId()));
                return memberInfoDTO;
            }).collect(Collectors.toList());
            workspaceInfo.setMemberList(memberInfoList);
            return workspaceInfo;
        }).collect(Collectors.toList());
        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(workspaceList.getTotalElements());
        pageMetadataResponse.setTotalPage(workspaceList.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageable.getPageNumber() + 1);
        pageMetadataResponse.setCurrentSize(pageable.getPageSize());
        return new WorkspaceInfoListResponse(workspaceInfoList, pageMetadataResponse);
    }
}



package com.virnect.workspace.application;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.*;
import com.virnect.workspace.dao.redis.UserInviteRepository;
import com.virnect.workspace.domain.*;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.domain.rest.LicenseStatus;
import com.virnect.workspace.dto.request.MemberKickOutRequest;
import com.virnect.workspace.dto.request.MemberUpdateRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Project: PF-Workspace
 * DATE: 2021-02-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspacePermissionRepository workspacePermissionRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final UserRestService userRestService;
    private final ModelMapper modelMapper;
    private final MessageRestService messageRestService;
    private final UserInviteRepository userInviteRepository;
    private final SpringTemplateEngine springTemplateEngine;
    private final HistoryRepository historyRepository;
    private final MessageSource messageSource;
    private final LicenseRestService licenseRestService;
    private final RedirectProperty redirectProperty;
    private final CacheManager cacheManager;
    private static final String ALL_WORKSAPCE_ROLE = "MASTER|MANAGER|MEMBER";
    private static final String ALL_LICENSE_PRODUCT = "REMOTE|MAKE|VIEW";
    private final RedisTemplate redisTemplate;

    /**
     * 멤버 조회
     *
     * @param workspaceId - 조회 대상 워크스페이스 식별자
     * @param search      - 검색 필터링
     * @param filter      - 조건 필터링
     * @param pageRequest - 페이징 정보
     * @return - 워크스페이스 소속 멤버 목록
     */
    public ApiResponse<WorkspaceUserInfoListResponse> getMembers(
            String workspaceId, String search, String filter, com.virnect.workspace.global.common.PageRequest pageRequest
    ) {
        //workspace 서버에서 sort 처리할 수 있는 것들만 한다.
        Pageable newPageable = PageRequest.of(pageRequest.of().getPageNumber(), pageRequest.of().getPageSize());
        String sortName = pageRequest.getSortName();
        if (sortName.equals("workspaceRole") || sortName.equals(("workspaceUser.createdDate"))) {
            newPageable = pageRequest.of();
        }

        //search를 제일 먼저 한다.
        UserInfoListRestResponse userInfoListRestResponse = getSearchedUserInfoList(search, workspaceId);
        List<String> searchedUserIds = userInfoListRestResponse.getUserInfoList().stream().map(UserInfoRestResponse::getUuid).collect(Collectors.toList());

        //search만 한 결과.
        Page<WorkspaceUserPermission> workspaceUserPermissionPage;
        if (!searchedUserIds.isEmpty()) {
            workspaceUserPermissionPage = workspaceUserPermissionRepository.getContainedUserIdList(searchedUserIds, newPageable, workspaceId);
        } else {
            workspaceUserPermissionPage = workspaceUserPermissionRepository.getWorkspaceUserList(newPageable, workspaceId);
        }


        if (StringUtils.hasText(filter)) {
            String[] filters = StringUtils.split(filter.toUpperCase(), ",") == null ? new String[]{filter} : StringUtils.split(filter.toUpperCase(), ",");
            //권한으로 필터를 건 경우
            if (filters[0].matches(ALL_WORKSAPCE_ROLE)) {
                workspaceUserPermissionPage = workspaceUserPermissionRepository.getRoleFilteredUserList(workspaceUserPermissionPage.toList(), Arrays.asList(filters), newPageable, workspaceId);
                List<String> userIds = workspaceUserPermissionPage.stream().map(workspaceUserPermission -> workspaceUserPermission.getWorkspaceUser().getUserId()).collect(Collectors.toList());
                userInfoListRestResponse = userRestService.getUserInfoList(search, userIds).getData();//필터링 된 유저목록을 다시 검색한다.
            }

            //라이선스로 필터를 건 경우
            if (filters[0].matches(ALL_LICENSE_PRODUCT)) {
                List<String> userIdList = getLicenseFilterdUserList(searchedUserIds, filter, workspaceId);
                workspaceUserPermissionPage = workspaceUserPermissionRepository.getContainedUserIdList(userIdList, newPageable, workspaceId);
                List<String> userIds = workspaceUserPermissionPage.stream().map(workspaceUserPermission -> workspaceUserPermission.getWorkspaceUser().getUserId()).collect(Collectors.toList());
                userInfoListRestResponse = userRestService.getUserInfoList(search, userIds).getData();//필터링 된 유저목록을 다시 검색한다.
            }
        }

        //결과가 없는경우
        if (workspaceUserPermissionPage.isEmpty()) {
            PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
            pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
            pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());
            return new ApiResponse<>(new WorkspaceUserInfoListResponse(new ArrayList<>(), pageMetadataResponse));
        }

        List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList = new ArrayList<>();
        for (UserInfoRestResponse userInfoRestResponse : userInfoListRestResponse.getUserInfoList()) {
            for (WorkspaceUserPermission workspaceUserPermission : workspaceUserPermissionPage) {
                if (userInfoRestResponse.getUuid().equalsIgnoreCase(workspaceUserPermission.getWorkspaceUser().getUserId())) {
                    WorkspaceUserInfoResponse workspaceUserInfoResponse = modelMapper.map(userInfoRestResponse, WorkspaceUserInfoResponse.class);
                    workspaceUserInfoResponse.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
                    workspaceUserInfoResponse.setJoinDate(workspaceUserPermission.getWorkspaceUser().getCreatedDate());
                    workspaceUserInfoResponse.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
                    String[] userLicenseProducts = getUserLicenseProductList(workspaceId, workspaceUserPermission.getWorkspaceUser().getUserId());
                    workspaceUserInfoResponse.setLicenseProducts(userLicenseProducts);
                    workspaceUserInfoResponseList.add(workspaceUserInfoResponse);
                }
            }
        }

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(workspaceUserPermissionPage.getTotalElements());
        pageMetadataResponse.setTotalPage(workspaceUserPermissionPage.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
        pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());

        List<WorkspaceUserInfoResponse> resultMemberListResponse = getSortedMemberList(pageRequest, workspaceUserInfoResponseList);
        return new ApiResponse<>(new WorkspaceUserInfoListResponse(resultMemberListResponse, pageMetadataResponse));
    }

    private List<String> getLicenseFilterdUserList(List<String> searchedUserIds, String filter, String workspaceId) {
        List<String> userIdList = new ArrayList<>();
        if (!searchedUserIds.isEmpty()) {
            for (String searchedUserId : searchedUserIds) {
                MyLicenseInfoListResponse myLicenseInfoListResponse = licenseRestService.getMyLicenseInfoRequestHandler(workspaceId, searchedUserId).getData();
                if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
                    myLicenseInfoListResponse.getLicenseInfoList().forEach(myLicenseInfoResponse -> {
                        if (filter.toUpperCase().contains(myLicenseInfoResponse.getProductName())) {
                            userIdList.add(searchedUserId);
                        }
                    });
                }
            }
            return userIdList;
        }
        List<WorkspaceUser> workspaceUserList = workspaceUserRepository.findByWorkspace_Uuid(workspaceId);
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
        return userIdList;
    }

    private UserInfoListRestResponse getSearchedUserInfoList(String search, String workspaceId) {
        List<String> workspaceUserIdList = workspaceUserRepository.getWorkspaceUserIdList(workspaceId);
        return userRestService.getUserInfoList(search, workspaceUserIdList).getData();
    }
    public List<WorkspaceUserInfoResponse> getSortedMemberList(
            com.virnect.workspace.global.common.PageRequest
                    pageRequest, List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList
    ) {
        String sortName = pageRequest.of().getSort().toString().split(":")[0].trim();//sort의 기준이 될 열
        String sortDirection = pageRequest.of().getSort().toString().split(":")[1].trim();//sort의 방향 : 내림차순 or 오름차순
        if (sortName.equalsIgnoreCase("workspaceRole") && sortDirection.equalsIgnoreCase("asc")) {
            return workspaceUserInfoResponseList.stream()
                    .sorted(
                            Comparator.comparing(WorkspaceUserInfoResponse::getRoleId, Comparator.nullsFirst(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("workspaceRole") && sortDirection.equalsIgnoreCase("desc")) {
            return workspaceUserInfoResponseList.stream()
                    .sorted(
                            Comparator.comparing(WorkspaceUserInfoResponse::getRoleId, Comparator.nullsFirst(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("asc")) {
            return workspaceUserInfoResponseList.stream()
                    .sorted(Comparator.comparing(WorkspaceUserInfoResponse::getEmail, Comparator.nullsFirst(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("desc")) {
            return workspaceUserInfoResponseList.stream()
                    .sorted(Comparator.comparing(WorkspaceUserInfoResponse::getEmail, Comparator.nullsFirst(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("workspaceUser.createdDate") && sortDirection.equalsIgnoreCase("asc")) {
            return workspaceUserInfoResponseList.stream()
                    .sorted(
                            Comparator.comparing(WorkspaceUserInfoResponse::getJoinDate, Comparator.nullsFirst(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("workspaceUser.createdDate") && sortDirection.equalsIgnoreCase("desc")) {
            return workspaceUserInfoResponseList.stream()
                    .sorted(
                            Comparator.comparing(WorkspaceUserInfoResponse::getJoinDate, Comparator.nullsFirst(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("asc")) {
            List<WorkspaceUserInfoResponse> koList = workspaceUserInfoResponseList.stream().filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1).matches("[가-힣\\s]"))
                    .sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName)).collect(Collectors.toList());
            List<WorkspaceUserInfoResponse> enList = workspaceUserInfoResponseList.stream().filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1).matches("[a-zA-Z\\s]"))
                    .sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName)).collect(Collectors.toList());
            List<WorkspaceUserInfoResponse> etcList = workspaceUserInfoResponseList.stream().filter(memberInfoDTO -> !koList.contains(memberInfoDTO)).filter(memberInfoDTO -> !enList.contains(memberInfoDTO))
                    .sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName)).collect(Collectors.toList());
            List<WorkspaceUserInfoResponse> nullList = workspaceUserInfoResponseList.stream().filter(memberInfoDTO -> !StringUtils.hasText(memberInfoDTO.getNickName())).collect(Collectors.toList());
            enList.addAll(etcList);
            koList.addAll(enList);
            nullList.addAll(koList);
            return nullList;
        }
        if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("desc")) {
            List<WorkspaceUserInfoResponse> koList = workspaceUserInfoResponseList.stream().filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1).matches("[가-힣\\s]"))
                    .sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName).reversed()).collect(Collectors.toList());
            List<WorkspaceUserInfoResponse> enList = workspaceUserInfoResponseList.stream().filter(memberInfoDTO -> org.apache.commons.lang.StringUtils.left(memberInfoDTO.getNickName(), 1).matches("[a-zA-Z\\s]"))
                    .sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName).reversed()).collect(Collectors.toList());
            List<WorkspaceUserInfoResponse> etcList = workspaceUserInfoResponseList.stream().filter(memberInfoDTO -> !koList.contains(memberInfoDTO)).filter(memberInfoDTO -> !enList.contains(memberInfoDTO))
                    .sorted(Comparator.comparing(WorkspaceUserInfoResponse::getNickName).reversed()).collect(Collectors.toList());
            List<WorkspaceUserInfoResponse> nullList = workspaceUserInfoResponseList.stream().filter(memberInfoDTO -> !StringUtils.hasText(memberInfoDTO.getNickName())).collect(Collectors.toList());
            enList.addAll(etcList);
            koList.addAll(enList);
            nullList.addAll(koList);
            return nullList;
        } else {
            return workspaceUserInfoResponseList.stream()
                    .sorted(Comparator.comparing(
                            WorkspaceUserInfoResponse::getUpdatedDate,
                            Comparator.nullsFirst(Comparator.reverseOrder())
                    ))
                    .collect(Collectors.toList());
        }
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

    public List<WorkspaceNewMemberInfoResponse> getWorkspaceNewUserInfo(String workspaceId) {
        List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.findRecentWorkspaceUserList(4, workspaceId);

        return workspaceUserPermissionList.stream().map(workspaceUserPermission -> {
            WorkspaceNewMemberInfoResponse newMemberInfo = modelMapper.map(getUserInfo(workspaceUserPermission.getWorkspaceUser().getUserId()), WorkspaceNewMemberInfoResponse.class);
            newMemberInfo.setJoinDate(workspaceUserPermission.getWorkspaceUser().getCreatedDate());
            newMemberInfo.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            return newMemberInfo;
        }).collect(Collectors.toList());
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

    //@CacheEvict(value = "userWorkspaces", key = "#requestUserId")
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
        redisTemplate.keys("userWorkspaces::*").stream().forEach(object -> {
            String key = (String) object;
            if (key.startsWith("userWorkspaces::".concat(responseUserId))) {
                redisTemplate.delete(key);
            }
        });
    }

    private void saveHistotry(Workspace workspace, String userId, String message) {
        History history = History.builder()
                .workspace(workspace)
                .userId(userId)
                .message(message)
                .build();
        historyRepository.save(history);
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

    private Workspace checkWorkspaceAndUserRole(String workspaceId, String userId, String[] role) {
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, userId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

        log.info(
                "[CHECK WORKSPACE USER ROLE] Acceptable User Workspace Role : {}, Present User Role : [{}]",
                Arrays.toString(role),
                workspaceUserPermission.getWorkspaceRole().getRole()
        );
        if (Arrays.stream(role).noneMatch(workspaceUserPermission.getWorkspaceRole().getRole()::equals)) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }
        return workspace;
    }

    public WorkspaceUserInfoResponse getMemberInfo(String workspaceId, String userId) {
        Optional<WorkspaceUserPermission> workspaceUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, userId);
        if (workspaceUserPermission.isPresent()) {
            WorkspaceUserInfoResponse workspaceUserInfoResponse = modelMapper.map(getUserInfo(userId), WorkspaceUserInfoResponse.class);
            workspaceUserInfoResponse.setRole(workspaceUserPermission.get().getWorkspaceRole().getRole());
            workspaceUserInfoResponse.setLicenseProducts(getUserLicenseProductList(workspaceId, userId));
            return workspaceUserInfoResponse;
        } else {
            return new WorkspaceUserInfoResponse();
        }
    }

    //@CacheEvict(value = "userWorkspaces", key = "#memberKickOutRequest.kickedUserId")
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
        redisTemplate.keys("userWorkspaces::*").stream().forEach(object -> {
            String key = (String) object;
            if (key.startsWith("userWorkspaces::".concat(memberKickOutRequest.getKickedUserId()))) {
                redisTemplate.delete(key);
            }
        });
        return new ApiResponse<>(true);
    }

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
            redirectView.setUrl(redirectProperty.getTermsWeb() + "?inviteSession=" + sessionCode + "&lang=" + lang + "&email=" + userInvite.getInvitedUserEmail());
            redirectView.setContentType("application/json");
            return redirectView;

        }
        //비회원일경우 초대 session정보에 uuid가 안들어가므로 user서버에서 조회해서 가져온다.
        InviteUserDetailInfoResponse inviteUserDetailInfoResponse = inviteUserResponse.getInviteUserDetailInfo();
        userInvite.setInvitedUserEmail(inviteUserDetailInfoResponse.getEmail());
        userInvite.setInvitedUserId(inviteUserDetailInfoResponse.getUserUUID());
        userInviteRepository.save(userInvite);

        Cache cache = cacheManager.getCache("userWorkspaces");
        if (cache != null) {
            cache.evict(userInvite.getInvitedUserId());//레디스에 캐싱된 내 워크스페이스 목록 정보 삭제
        }

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

    private void historySaveHandler(String message, String userId, Workspace workspace) {
        History history = History.builder()
                .message(message)
                .userId(userId)
                .workspace(workspace)
                .build();
        historyRepository.save(history);
    }

    public RedirectView workspaceOverPlanFailHandler(Workspace workspace, UserInvite
            userInvite, List<String> successPlan, List<String> failPlan, Locale locale) {
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

    //@CacheEvict(value = "userWorkspaces", key = "#userId")
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

        redisTemplate.keys("userWorkspaces::*").stream().forEach(object -> {
            String key = (String) object;
            if (key.startsWith("userWorkspaces::".concat(userId))) {
                redisTemplate.delete(key);
            }
        });
        return new ApiResponse<>(true);
    }

    public WorkspaceUserInfoListResponse getSimpleWorkspaceUserList(String workspaceId) {
        UserInfoListRestResponse userInfoListRestResponse = getSearchedUserInfoList("", workspaceId);
        List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList = userInfoListRestResponse.getUserInfoList().stream().map(userInfoRestResponse -> {
            WorkspaceUserInfoResponse workspaceUserInfoResponse = modelMapper.map(userInfoRestResponse, WorkspaceUserInfoResponse.class);
            workspaceUserInfoResponse.setLicenseProducts(getUserLicenseProductList(workspaceId, userInfoRestResponse.getUuid()));
            return workspaceUserInfoResponse;
        }).collect(Collectors.toList());

        return new WorkspaceUserInfoListResponse(workspaceUserInfoResponseList, null);
    }

    /**
     * 워크스페이스 소속 멤버 플랜 리스트 조회
     *
     * @param workspaceId - 조회 대상 워크스페이스 식별자
     * @param pageable    -  페이징
     * @return - 멤버 플랜 리스트
     */
    public WorkspaceUserLicenseListResponse getLicenseWorkspaceUserList(
            String workspaceId, com.virnect.workspace.global.common.PageRequest pageRequest
    ) {
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = licenseRestService.getWorkspaceLicenses(workspaceId).getData();
        if (workspaceLicensePlanInfoResponse.getLicenseProductInfoList() == null || workspaceLicensePlanInfoResponse.getLicenseProductInfoList().isEmpty()) {
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
            pageMetadataRestResponse.setCurrentPage(pageRequest.of().getPageNumber());
            pageMetadataRestResponse.setCurrentSize(pageRequest.of().getPageSize());
            pageMetadataRestResponse.setTotalElements(0);
            pageMetadataRestResponse.setTotalPage(0);
            return new WorkspaceUserLicenseListResponse(workspaceUserLicenseInfoList, new PageMetadataRestResponse());
        }

        List<WorkspaceUserLicenseInfoResponse> sortedWorkspaceUserLicenseList
                = workspaceUserLicenseInfoList.stream()
                .sorted(Comparator.comparing(getWorkspaceUserLicenseInfoResponseSortKey(pageRequest.getSortName()), getSortDirection(pageRequest.getSortDirection())))
                .collect(Collectors.toList());

        CustomPageResponse<WorkspaceUserLicenseInfoResponse> customPageResponse
                = new CustomPageHandler<WorkspaceUserLicenseInfoResponse>().paging(pageRequest.of().getPageNumber(), pageRequest.of().getPageSize(), sortedWorkspaceUserLicenseList);
        return new WorkspaceUserLicenseListResponse(customPageResponse.getAfterPagingList(), customPageResponse.getPageMetadataResponse());
    }

    private Function<WorkspaceUserLicenseInfoResponse, String> getWorkspaceUserLicenseInfoResponseSortKey(String sortName) {
        if (sortName.equalsIgnoreCase("plan")) {
            return WorkspaceUserLicenseInfoResponse::getProductName;
        }
        if (sortName.equalsIgnoreCase("nickName")) {
            return WorkspaceUserLicenseInfoResponse::getNickName;
        }
        return WorkspaceUserLicenseInfoResponse::getProductName;
    }

    private Comparator<String> getSortDirection(String sortDirection) {
        if (sortDirection.equalsIgnoreCase("asc")) {
            return Comparator.nullsFirst(Comparator.naturalOrder());
        }
        if (sortDirection.equalsIgnoreCase("desc")) {
            return Comparator.nullsFirst(Comparator.reverseOrder());
        }
        return Comparator.nullsFirst(Comparator.naturalOrder());
    }

}

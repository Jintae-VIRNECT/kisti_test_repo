package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.message.MessageRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.workspace.WorkspaceRepository;
import com.virnect.workspace.dao.workspace.WorkspaceRoleRepository;
import com.virnect.workspace.dao.workspace.WorkspaceUserPermissionRepository;
import com.virnect.workspace.dao.workspace.WorkspaceUserRepository;
import com.virnect.workspace.domain.rest.LicenseStatus;
import com.virnect.workspace.domain.workspace.Workspace;
import com.virnect.workspace.domain.workspace.WorkspaceRole;
import com.virnect.workspace.domain.workspace.WorkspaceUserPermission;
import com.virnect.workspace.dto.onpremise.MemberAccountCreateRequest;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.event.cache.UserWorkspacesDeleteEvent;
import com.virnect.workspace.event.history.HistoryAddEvent;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.CustomPageHandler;
import com.virnect.workspace.global.common.CustomPageResponse;
import com.virnect.workspace.global.common.RedirectProperty;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.constant.LicenseProduct;
import com.virnect.workspace.global.constant.Mail;
import com.virnect.workspace.global.constant.MailSender;
import com.virnect.workspace.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public abstract class WorkspaceUserService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final UserRestService userRestService;
    private final MessageRestService messageRestService;
    private final SpringTemplateEngine springTemplateEngine;
    private final MessageSource messageSource;
    private final LicenseRestService licenseRestService;
    private final RedirectProperty redirectProperty;
    private static final String ALL_LICENSE_PRODUCT = ".*(?i)REMOTE.*|.*(?i)MAKE.*|.*(?i)VIEW.*";
    private final RestMapStruct restMapStruct;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 멤버 조회
     *
     * @param workspaceId - 조회 대상 워크스페이스 식별자
     * @param search      - 검색 필터링
     * @param filter      - 조건 필터링
     * @param pageRequest - 페이징 정보
     * @return - 워크스페이스 소속 멤버 목록
     */

    public WorkspaceUserInfoListResponse getMembers(
            String workspaceId, String search, String filter, com.virnect.workspace.global.common.PageRequest pageRequest
    ) {
        //1. 정렬 체크 : workspace 서버에서 정렬처리를 할 수 있는 항목인지 체크한다.
        Pageable newPageable = PageRequest.of(pageRequest.of().getPageNumber(), pageRequest.of().getPageSize());
        //정렬요청이 없는 경우에는 worksapceUser.updateDate,desc을 기본값으로 정렬한다.
        if (pageRequest.getSortName().equals("updatedDate")) {
            pageRequest.setSort("workspaceUser.updatedDate,desc");
            newPageable = pageRequest.of();
        }
        //워크스페이스에서 정렬이 가능한 경우 -> 권한 정렬, 참여 일자 정렬
        if (pageRequest.getSortName().equalsIgnoreCase("role") || pageRequest.getSortName().equalsIgnoreCase(("joinDate"))) {
            newPageable = pageRequest.of();
        }

        //2. search 필터링 : user서버에서 search 필터링된 유저 목록을 불러온다.
        List<String> workspaceUserIdList = workspaceUserRepository.getWorkspaceUserIdList(workspaceId);
        UserInfoListRestResponse userInfoListRestResponse = getUserInfoList(search, workspaceUserIdList);
        List<String> searchedUserIds = userInfoListRestResponse.getUserInfoList().stream().map(UserInfoRestResponse::getUuid).collect(Collectors.toList());

        //3. 라이선스 필터링 : filter값이 라이선스값인 경우, search된 유저 목록중에서 filter 라이선스 정보만 가지고 오고, filter값이 라이선스가 아니라면 search된 유저 목록의 모든 라이선스 정보를 가지고 온다.
        Map<String, List<MyLicenseInfoResponse>> licenseProducts = new HashMap<>();
        searchedUserIds.forEach(userId -> {
            MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(workspaceId, userId);
            if (myLicenseInfoListResponse != null && myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
                if (StringUtils.hasText(filter) && filter.matches(ALL_LICENSE_PRODUCT)) {
                    if (myLicenseInfoListResponse.getLicenseInfoList().stream().anyMatch(myLicenseInfoResponse -> filter.toUpperCase().contains(myLicenseInfoResponse.getProductName().toUpperCase()))) {
                        licenseProducts.put(userId, myLicenseInfoListResponse.getLicenseInfoList());
                    }
                } else {
                    licenseProducts.put(userId, myLicenseInfoListResponse.getLicenseInfoList());
                }
            }
        });
        if (StringUtils.hasText(filter) && filter.matches(ALL_LICENSE_PRODUCT)) {
            searchedUserIds.forEach(userId -> {
                MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(workspaceId, userId);
                if (myLicenseInfoListResponse != null && myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty() &&
                        myLicenseInfoListResponse.getLicenseInfoList().stream().anyMatch(myLicenseInfoResponse -> filter.toUpperCase().contains(myLicenseInfoResponse.getProductName().toUpperCase()))) {
                    licenseProducts.put(userId, myLicenseInfoListResponse.getLicenseInfoList());
                }
            });
            searchedUserIds = new ArrayList<>(licenseProducts.keySet());
        } else {
            searchedUserIds.forEach(userId -> {
                if (getMyLicenseInfoRequestHandler(workspaceId, userId) != null)
                    licenseProducts.put(userId, getMyLicenseInfoRequestHandler(workspaceId, userId).getLicenseInfoList());
            });
        }

        //4. 권한 필터링, 정렬 : filter값이 권한값인 경우, search된 유저 목록 중에서 권한에 일치한 유저만 검색한다.
        Page<WorkspaceUserPermission> workspaceUserPermissionPage = workspaceUserPermissionRepository.getWorkspaceUserPermissionByInUserListAndEqRole(searchedUserIds, filter, newPageable, workspaceId);

        //5. 결과가 없는 경우에는 빠른 리턴
        if (workspaceUserPermissionPage.isEmpty()) {
            PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
            pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
            pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());
            return new WorkspaceUserInfoListResponse(new ArrayList<>(), pageMetadataResponse);
        }

        //6. 응답 : user 서버, license 서버에서 유저 정보 긁어온다.
        List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList = new ArrayList<>();
        for (
                WorkspaceUserPermission workspaceUserPermission : workspaceUserPermissionPage) {
            UserInfoRestResponse userInfoResponse = userInfoListRestResponse.getUserInfoList().stream()
                    .filter(userInfoRestResponse -> userInfoRestResponse.getUuid().equals(workspaceUserPermission.getWorkspaceUser().getUserId())).findFirst().orElse(new UserInfoRestResponse());
            WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(userInfoResponse);
            workspaceUserInfoResponse.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            workspaceUserInfoResponse.setJoinDate(workspaceUserPermission.getWorkspaceUser().getCreatedDate());
            workspaceUserInfoResponse.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
            String[] userLicenseProducts = licenseProducts.isEmpty() ? new String[0] : licenseProducts.get(workspaceUserPermission.getWorkspaceUser().getUserId()).stream().map(MyLicenseInfoResponse::getProductName).toArray(String[]::new);
            workspaceUserInfoResponse.setLicenseProducts(userLicenseProducts);
            workspaceUserInfoResponseList.add(workspaceUserInfoResponse);
        }

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(workspaceUserPermissionPage.getTotalElements());
        pageMetadataResponse.setTotalPage(workspaceUserPermissionPage.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
        pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());

        List<WorkspaceUserInfoResponse> resultMemberListResponse = workspaceUserInfoResponseList;
        if (pageRequest.getSortName().equalsIgnoreCase("email") || pageRequest.getSortName().equalsIgnoreCase("nickname")) {
            resultMemberListResponse = getSortedMemberList(pageRequest, workspaceUserInfoResponseList);
        }
        return new WorkspaceUserInfoListResponse(resultMemberListResponse, pageMetadataResponse);
    }

    /**
     * user 서버에서 워크스페이스 유저 상세 정보 리스트를 조회함.
     *
     * @param search              - 유저 조회 시 검색어(닉네임, 이메일에 해당하는 값) 빈값인경우, 일반 워크스페이스 유저 목록을 가져옴.
     * @param workspaceUserIdList - 조회 대상 워크스페이스 유저 식별자 리스트
     * @return - 워크스페이스 유저 상세 정보 리스트
     */
    private UserInfoListRestResponse getUserInfoList(String search, List<String> workspaceUserIdList) {
        return userRestService.getUserInfoList(search, workspaceUserIdList).getData();
    }

    private MyLicenseInfoListResponse getMyLicenseInfoRequestHandler(String workspaceId, String userId) {
        ApiResponse<MyLicenseInfoListResponse> apiResponse = licenseRestService.getMyLicenseInfoRequestHandler(workspaceId, userId);
        if (apiResponse.getCode() != 200) {
            log.error("[GET MY LICENSE INFO BY WORKSPACE UUID & USER UUID] response message : {}", apiResponse.getMessage());
            return null;
        }
        return apiResponse.getData();
    }

    public List<WorkspaceUserInfoResponse> getSortedMemberList(
            com.virnect.workspace.global.common.PageRequest
                    pageRequest, List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList
    ) {
        String sortName = pageRequest.getSortName();
        String sortDirection = pageRequest.getSortDirection();
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
        MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(workspaceId, userId);

        if (myLicenseInfoListResponse.getLicenseInfoList() != null && !myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
            return myLicenseInfoListResponse.getLicenseInfoList()
                    .stream()
                    .map(MyLicenseInfoResponse::getProductName)
                    .toArray(String[]::new);
        }
        return new String[0];
    }

    public List<WorkspaceNewMemberInfoResponse> getWorkspaceNewUserInfo(String workspaceId) {
        List<WorkspaceUserPermission> workspaceUserPermissionList = workspaceUserPermissionRepository.findRecentWorkspaceUserList(4, workspaceId);

        return workspaceUserPermissionList.stream().map(workspaceUserPermission -> {
            WorkspaceNewMemberInfoResponse newMemberInfo = restMapStruct.userInfoRestResponseToWorkspaceNewMemberInfoResponse(getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId()));
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
    private UserInfoRestResponse getUserInfoByUserId(String userId) {
        //todo : logging
        return userRestService.getUserInfoByUserId(userId).getData();
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
        UserInfoRestResponse masterUser = getUserInfoByUserId(workspace.getUserId());
        UserInfoRestResponse user = getUserInfoByUserId(memberUpdateRequest.getUserId());
        UserInfoRestResponse requestUser = getUserInfoByUserId(memberUpdateRequest.getRequestUserId());

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
                    "WORKSPACE_SET_MANAGER", new String[]{masterUser.getNickname(), user.getNickname()}, locale);
        } else {
            message = messageSource.getMessage(
                    "WORKSPACE_SET_MEMBER", new String[]{masterUser.getNickname(), user.getNickname()}, locale);
        }
        applicationEventPublisher.publishEvent(new HistoryAddEvent(message, requestUserId, workspace));
        applicationEventPublisher.publishEvent(new UserWorkspacesDeleteEvent(responseUserId));
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
            applicationEventPublisher.publishEvent(new HistoryAddEvent(message, userId, workspace));
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
            applicationEventPublisher.publishEvent(new HistoryAddEvent(message, userId, workspace));
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
            WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(getUserInfoByUserId(userId));
            workspaceUserInfoResponse.setRole(workspaceUserPermission.get().getWorkspaceRole().getRole());
            workspaceUserInfoResponse.setLicenseProducts(getUserLicenseProductList(workspaceId, userId));
            return workspaceUserInfoResponse;
        } else {
            return new WorkspaceUserInfoResponse();
        }
    }

    public WorkspaceUserInfoListResponse getMemberInfoList(String workspaceId, String[] userIds) {
        List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList = new ArrayList<>();
        for (String userId : userIds) {
            Optional<WorkspaceUserPermission> workspaceUserPermission = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, userId);
            if (workspaceUserPermission.isPresent()) {
                WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(getUserInfoByUserId(userId));
                workspaceUserInfoResponse.setRole(workspaceUserPermission.get().getWorkspaceRole().getRole());
                workspaceUserInfoResponse.setLicenseProducts(getUserLicenseProductList(workspaceId, userId));
                workspaceUserInfoResponseList.add(workspaceUserInfoResponse);
            }
        }
        return new WorkspaceUserInfoListResponse(workspaceUserInfoResponseList, null);
    }

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
        UserInfoRestResponse kickedUser = getUserInfoByUserId(memberKickOutRequest.getKickedUserId());
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
        applicationEventPublisher.publishEvent(new HistoryAddEvent(message, kickedUser.getUuid(), workspace));

        applicationEventPublisher.publishEvent(new UserWorkspacesDeleteEvent(memberKickOutRequest.getKickedUserId()));
        return new ApiResponse<>(true);
    }

    @Profile("!onpremise")
    public abstract ApiResponse<Boolean> inviteWorkspace(String workspaceId, WorkspaceInviteRequest workspaceInviteRequest, Locale locale);

    @Profile("!onpremise")
    public abstract RedirectView inviteWorkspaceAccept(String sessionCode, String lang) throws IOException;

    @Profile("!onpremise")
    public abstract RedirectView inviteWorkspaceReject(String sessionCode, String lang);


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
        applicationEventPublisher.publishEvent(new HistoryAddEvent(message, userId, workspace));
        applicationEventPublisher.publishEvent(new UserWorkspacesDeleteEvent(userId));//캐싱 삭제
        return new ApiResponse<>(true);
    }

    public WorkspaceUserInfoListResponse getSimpleWorkspaceUserList(String workspaceId) {
        List<String> workspaceUserIdList = workspaceUserRepository.getWorkspaceUserIdList(workspaceId);
        UserInfoListRestResponse userInfoListRestResponse = getUserInfoList("", workspaceUserIdList);
        List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList = userInfoListRestResponse.getUserInfoList().stream().map(userInfoRestResponse -> {
            WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(userInfoRestResponse);
            WorkspaceRole role = workspaceUserPermissionRepository.findWorkspaceUser(workspaceId, userInfoRestResponse.getUuid()).get().getWorkspaceRole();
            workspaceUserInfoResponse.setRole(role.getRole());
            workspaceUserInfoResponse.setRoleId(role.getId());
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
                                WorkspaceUserLicenseInfoResponse workspaceUserLicenseInfo = restMapStruct.userInfoRestResponseToWorkspaceUserLicenseInfoResponse(userInfoRestResponse);
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

    @Profile("onpremise")
    @Transactional
    public abstract WorkspaceMemberInfoListResponse createWorkspaceMemberAccount(String workspaceId, MemberAccountCreateRequest memberAccountCreateRequest);

    @Profile("onpremise")
    @Transactional
    public abstract boolean deleteWorkspaceMemberAccount(String workspaceId, MemberAccountDeleteRequest memberAccountDeleteRequest);

    @Profile("onpremise")
    @Transactional
    public abstract WorkspaceMemberPasswordChangeResponse memberPasswordChange(WorkspaceMemberPasswordChangeRequest passwordChangeRequest, String workspaceId);
}

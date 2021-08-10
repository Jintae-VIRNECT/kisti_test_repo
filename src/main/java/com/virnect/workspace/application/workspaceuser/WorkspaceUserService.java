package com.virnect.workspace.application.workspaceuser;

import com.virnect.workspace.application.license.LicenseRestService;
import com.virnect.workspace.application.user.UserRestService;
import com.virnect.workspace.dao.setting.WorkspaceCustomSettingRepository;
import com.virnect.workspace.dao.workspace.*;
import com.virnect.workspace.domain.rest.LicenseStatus;
import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.SettingValue;
import com.virnect.workspace.domain.setting.WorkspaceCustomSetting;
import com.virnect.workspace.domain.workspace.*;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.event.cache.UserWorkspacesDeleteEvent;
import com.virnect.workspace.event.history.HistoryAddEvent;
import com.virnect.workspace.event.mail.MailContextHandler;
import com.virnect.workspace.event.mail.MailSendEvent;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.common.CustomPageHandler;
import com.virnect.workspace.global.common.CustomPageResponse;
import com.virnect.workspace.global.common.mapper.rest.RestMapStruct;
import com.virnect.workspace.global.constant.Mail;
import com.virnect.workspace.global.constant.Permission;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
public abstract class WorkspaceUserService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final UserRestService userRestService;
    private final MessageSource messageSource;
    private final LicenseRestService licenseRestService;
    private final RestMapStruct restMapStruct;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final WorkspaceCustomSettingRepository workspaceCustomSettingRepository;
    private final MailContextHandler mailContextHandler;
    private final WorkspacePermissionRepository workspacePermissionRepository;

    private static final String ANY_LICENSE_PRODUCT = ".*(?i)REMOTE.*|.*(?i)MAKE.*|.*(?i)VIEW.*";
    private static final int MAX_WORKSPACE_USER_AMOUNT = 50;//워크스페이스 최대 멤버 수(마스터 본인 포함)
    private static final String ANY_WORKSPACE_ROLE = ".*(?i)MASTER.*|.*(?i)MANAGER.*|.*(?i)MEMBER.*|.*(?i)SEAT.*";

    /**
     * 멤버 조회
     *
     * @param workspaceId - 조회 대상 워크스페이스 식별자
     * @param search      - 검색 필터링
     * @param filter      - 조건 필터링
     * @param roleFilter
     * @param pageRequest - 페이징 정보
     * @return - 워크스페이스 소속 멤버 목록
     */

    public WorkspaceUserInfoListResponse getMembers(
            String workspaceId, String search, String filter, List<Role> roleFilter, String userTypeFilter, String planFilter, com.virnect.workspace.global.common.PageRequest pageRequest
    ) {
        //1. 정렬 검증으로 Pageable 재정의
        Pageable newPageable = getPageable(pageRequest);

        //2. search 필터링
        List<String> resultUserIdList = workspaceUserRepository.getWorkspaceUserIdList(workspaceId);//워크스페이스 소속 전체 유저
        if (StringUtils.hasText(search)) {
            UserInfoListRestResponse userInfoListRestResponse = getUserInfoList(search, resultUserIdList);
            resultUserIdList = userInfoListRestResponse.getUserInfoList().stream().map(UserInfoRestResponse::getUuid).collect(Collectors.toList());
        }

        //3. 필터링
        if (StringUtils.hasText(filter)) {
            //3-1. 라이선스 플랜으로 필터링
            if (filter.matches(ANY_LICENSE_PRODUCT) && !resultUserIdList.isEmpty()) {
                resultUserIdList = filterUserIdListByPlan(workspaceId, resultUserIdList, filter);
            }
            //3-2. 워크스페이스 역할로 필터링
            else if (filter.matches(ANY_WORKSPACE_ROLE) && !resultUserIdList.isEmpty()) {
                String[] filters = filter.toUpperCase().split(",").length == 0 ? new String[]{filter.toUpperCase()} : filter.toUpperCase().split(",");
                List<Role> roleList = new ArrayList<>();
                Arrays.stream(Role.values()).forEach(role -> {
                    for (String StringRole : filters) {
                        if (role.name().equals(StringRole)) {
                            roleList.add(role);
                        }
                    }
                });
                resultUserIdList = workspaceUserPermissionRepository.getUserIdsByInUserListAndEqRole(resultUserIdList, roleList, workspaceId);
            }
        }
        //3-3. 워크스페이스 역할로 필터링
        if (!CollectionUtils.isEmpty(roleFilter)) {
            resultUserIdList = workspaceUserPermissionRepository.getUserIdsByInUserListAndEqRole(resultUserIdList, roleFilter, workspaceId);
        }

        //3-4. 유저 타입으로 필터링
        if (StringUtils.hasText(userTypeFilter)) {

        }

        //3-5. 라이선스 플랜으로 필터링
        if (StringUtils.hasText(planFilter)) {
            resultUserIdList = filterUserIdListByPlan(workspaceId, resultUserIdList, planFilter);
        }

        //4. 결과가 없는 경우에는 빠른 리턴
        if (resultUserIdList.isEmpty()) {
            PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
            pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
            pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());
            return new WorkspaceUserInfoListResponse(new ArrayList<>(), pageMetadataResponse);
        }

        //5. 최종적으로 필터링된 유저들을 페이징한다.
        Page<WorkspaceUserPermission> workspaceUserPermissionPage = workspaceUserPermissionRepository.getWorkspaceUserListByInUserList(resultUserIdList, newPageable, workspaceId);

        //6. 응답
        List<WorkspaceUserInfoResponse> workspaceUserInfoResponseList = new ArrayList<>();
        for (WorkspaceUserPermission workspaceUserPermission : workspaceUserPermissionPage) {
            UserInfoRestResponse userInfoResponse = getUserInfoByUserId(workspaceUserPermission.getWorkspaceUser().getUserId());
            if (!StringUtils.hasText(userInfoResponse.getUuid())) {
                continue;
            }
            WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(userInfoResponse);
            workspaceUserInfoResponse.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            workspaceUserInfoResponse.setJoinDate(workspaceUserPermission.getWorkspaceUser().getCreatedDate());
            workspaceUserInfoResponse.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
            MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(workspaceId, workspaceUserPermission.getWorkspaceUser().getUserId());
            String[] userLicenseProducts = myLicenseInfoListResponse.getLicenseInfoList().isEmpty() ? new String[0]
                    : myLicenseInfoListResponse.getLicenseInfoList().stream().map(MyLicenseInfoResponse::getProductName).toArray(String[]::new);
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


    private List<String> filterUserIdListByPlan(String workspaceId, List<String> userIdList, String planFilter) {
        List<String> filterdUserIdList = new ArrayList<>();
        for (String userId : userIdList) {
            MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(workspaceId, userId);
            List<MyLicenseInfoResponse> myLicenseInfoList = myLicenseInfoListResponse.getLicenseInfoList();
            if (!CollectionUtils.isEmpty(myLicenseInfoList)) {
                if (myLicenseInfoListResponse.getLicenseInfoList().stream().anyMatch(myLicenseInfoResponse -> planFilter.toUpperCase().contains(myLicenseInfoResponse.getProductName().toUpperCase()))) {
                    filterdUserIdList.add(userId);
                }
            }
        }
        return filterdUserIdList;
    }

    private Pageable getPageable(com.virnect.workspace.global.common.PageRequest pageRequest) {
        //정렬을 빼고 Pageable 객체를 만든다.
        Pageable pageable = PageRequest.of(pageRequest.of().getPageNumber(), pageRequest.of().getPageSize());
        //정렬요청이 없는 경우에는 worksapceUser.updateDate,desc을 기본값으로 정렬하는 것으로 Pageable 객체를 수정한다.
        if (pageRequest.getSortName().equals("updatedDate")) {
            pageRequest.setSort("workspaceUser.updatedDate,desc");
            pageable = pageRequest.of();
        }
        //워크스페이스에서 정렬이 가능한 경우에는 Pageable 객체를 수정한다.
        if (pageRequest.getSortName().equalsIgnoreCase("role") || pageRequest.getSortName().equalsIgnoreCase(("joinDate"))) {
            pageable = pageRequest.of();
        }
        return pageable;
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
            log.error("[GET MY LICENSE INFO BY WORKSPACE UUID & USER UUID] request workspaceId : {}, request userId : {}, response code : {}", workspaceId, userId, apiResponse.getCode());
            MyLicenseInfoListResponse myLicenseInfoListResponse = new MyLicenseInfoListResponse();
            myLicenseInfoListResponse.setLicenseInfoList(new ArrayList<>());
            return myLicenseInfoListResponse;
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

        if (!CollectionUtils.isEmpty(myLicenseInfoListResponse.getLicenseInfoList())) {
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
    public UserInfoRestResponse getUserInfoByUserId(String userId) {
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
        //1-1. 최소 하나 이상의 제품 라이선스를 부여했는지 체크
        if (!memberUpdateRequest.isEssentialLicenseToUser()) {
            throw new WorkspaceException(ErrorCode.ERR_INCORRECT_USER_LICENSE_INFO);
        }
        //1-2. 요청 워크스페이스 조회
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        //1-3. 요청 권한 조회
        WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(memberUpdateRequest.getRole()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));

        //1-4. 요청 유저 권한 조회
        WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberUpdateRequest.getRequestUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        WorkspaceUserPermission updateUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberUpdateRequest.getUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));

        UserInfoRestResponse masterUser = getUserInfoByUserId(workspace.getUserId());
        UserInfoRestResponse updateUser = getUserInfoByUserId(memberUpdateRequest.getUserId());
        UserInfoRestResponse requestUser = getUserInfoByUserId(memberUpdateRequest.getRequestUserId());

        //2. 사용자 권한 변경
        if (!updateUserPermission.getWorkspaceRole().getRole().equals(memberUpdateRequest.getRole())) {
            log.info("[REVISE MEMBER INFO] Revise User Role Info. Current User Role >> [{}], Updated User Role >> [{}].", updateUserPermission.getWorkspaceRole().getRole(), memberUpdateRequest.getRole());
            //2-1. 요청 유저 권한 체크
            checkUserRoleUpdatePermission(requestUserPermission, updateUserPermission, workspaceId);
            //2-2. 권한 정보 변경
            updateUserPermission.setWorkspaceRole(workspaceRole);
            workspaceUserPermissionRepository.save(updateUserPermission);

            //2-3. 변경 성공 메일 발송
            Context context = mailContextHandler.getWorkspaceUserPermissionUpdateContext(workspace.getName(), masterUser, updateUser, workspaceRole.getRole().toString());
            List<String> receiver = new ArrayList<>();
            receiver.add(updateUser.getEmail());
            applicationEventPublisher.publishEvent(new MailSendEvent(context, Mail.WORKSPACE_USER_PERMISSION_UPDATE, locale, receiver));

            //2-4. 변경 성공 히스토리 저장
            String message;
            if (updateUserPermission.getWorkspaceRole().getRole() == Role.MANAGER) {
                message = messageSource.getMessage("WORKSPACE_SET_MANAGER", new String[]{masterUser.getNickname(), updateUser.getNickname()}, locale);
            } else {
                message = messageSource.getMessage("WORKSPACE_SET_MEMBER", new String[]{masterUser.getNickname(), updateUser.getNickname()}, locale);
            }
            applicationEventPublisher.publishEvent(new HistoryAddEvent(message, memberUpdateRequest.getRequestUserId(), workspace));

            //2-5. 권한이 변경된 사용자 캐싱 데이터 삭제
            //applicationEventPublisher.publishEvent(new UserWorkspacesDeleteEvent(memberUpdateRequest.getUserId()));
        }

        //3. 사용자 제품 라이선스 유형 변경
        MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(workspaceId, memberUpdateRequest.getUserId());
        List<String> currentProductList = myLicenseInfoListResponse.getLicenseInfoList().stream().map(MyLicenseInfoResponse::getProductName).collect(Collectors.toList());
        List<String> removedProductList = getRemovedProductList(memberUpdateRequest.getLicenseRemote(), memberUpdateRequest.getLicenseMake(), memberUpdateRequest.getLicenseView(), currentProductList);
        List<String> addedProductList = getAddedProductList(memberUpdateRequest.getLicenseRemote(), memberUpdateRequest.getLicenseMake(), memberUpdateRequest.getLicenseView(), currentProductList);
        if (!removedProductList.isEmpty() || !addedProductList.isEmpty()) {
            log.info("[REVISE MEMBER INFO] Revise License Info. Current License Product Info >> [{}], Removed License Product Info >> [{}], Added License Product Info >> [{}].",
                    org.apache.commons.lang.StringUtils.join(currentProductList, ","),
                    org.apache.commons.lang.StringUtils.join(removedProductList, ","),
                    org.apache.commons.lang.StringUtils.join(addedProductList, ","));

            //3-1. 요청 유저 권한 체크
            checkUserLicenseUpdatePermission(requestUserPermission, updateUserPermission, workspaceId);

            //3-2. 제품 라이선스 해제 요청 및 히스토리 저장
            if (!removedProductList.isEmpty()) {
                removedProductList.forEach(productName -> revokeWorkspaceLicenseToUser(workspace.getUuid(), updateUser.getUuid(), productName));
                String message = messageSource.getMessage("WORKSPACE_REVOKE_LICENSE", new String[]{requestUser.getNickname(), updateUser.getNickname(), org.apache.commons.lang.StringUtils.join(removedProductList, ",")}, locale);
                applicationEventPublisher.publishEvent(new HistoryAddEvent(message, updateUser.getUuid(), workspace));
            }

            //3-3. 제품 라이선스 부여 요청 및 히스토리 저장
            if (!addedProductList.isEmpty()) {
                addedProductList.forEach(productName -> grantWorkspaceLicenseToUser(workspace.getUuid(), updateUser.getUuid(), productName));
                String message = messageSource.getMessage("WORKSPACE_GRANT_LICENSE", new String[]{requestUser.getNickname(), updateUser.getNickname(), org.apache.commons.lang.StringUtils.join(addedProductList, ",")}, locale);
                applicationEventPublisher.publishEvent(new HistoryAddEvent(message, updateUser.getUuid(), workspace));
            }

            //3-4. 라이선스 변경 성공 메일 전송
            Context context = mailContextHandler.getWorkspaceUserPlanUpdateContext(workspace.getName(), masterUser, updateUser, currentProductList);
            List<String> receiver = new ArrayList<>();
            receiver.add(updateUser.getEmail());
            applicationEventPublisher.publishEvent(new MailSendEvent(context, Mail.WORKSPACE_USER_PLAN_UPDATE, locale, receiver));
        }
        return new ApiResponse<>(true);
    }

    private List<String> getAddedProductList(boolean requestRemote, boolean requestMake, boolean requestView, List<
            String> userHaveProductList) {
        List<String> addedProductList = new ArrayList<>();
        if (requestRemote && !userHaveProductList.contains("REMOTE")) {
            addedProductList.add("REMOTE");
        }
        if (requestMake && !userHaveProductList.contains("MAKE")) {
            addedProductList.add("MAKE");
        }
        if (requestView && !userHaveProductList.contains("VIEW")) {
            addedProductList.add("VIEW");
        }

        return addedProductList;
    }

    private List<String> getRemovedProductList(boolean requestRemote, boolean requestMake,
                                               boolean requestView, List<String> userHaveProductList) {
        List<String> removedProductList = new ArrayList<>();
        if (!requestRemote && userHaveProductList.contains("REMOTE")) {
            removedProductList.add("REMOTE");
        }
        if (!requestMake && userHaveProductList.contains("MAKE")) {
            removedProductList.add("MAKE");
        }
        if (!requestView && userHaveProductList.contains("VIEW")) {
            removedProductList.add("VIEW");
        }
        return removedProductList;
    }

    /**
     * 워크스페이스 유저 라이선스 변경 요청 유효성 검증
     *
     * @param requestUserPermission - 변경 요청 유저 역할
     * @param updateUserPermission  - 변경 대상 유저 역할
     * @param workspaceId           - 워크스페이스 식별자
     */
    private void checkUserLicenseUpdatePermission(WorkspaceUserPermission
                                                          requestUserPermission, WorkspaceUserPermission updateUserPermission, String workspaceId) {
        /**
         * 요청 : 마스터, 대상 : 마스터 (o)
         * 요청 : 마스터, 대상 : 매니저 (o)
         * 요청 : 마스터, 대상 : 멤버 (o)
         * 요청 : 매니저, 대상 : 마스터 (x)
         * 요청 : 매니저, 대상 : 매니저 (o)
         * 요청 : 매니저, 대상 : 멤버 (o)
         * 요청 : 멤버, 대상 : 마스터 (x)
         * 요청 : 멤버, 대상 : 매니저 (x)
         * 요청 : 멤버, 대상 : 멤버 (o)
         *
         * DEFAULT : 요청 유저는 마스터 또는 매니저만 허용한다.
         */
        //본인 라이선스 변경은 요청 유효성을 검증하지 않는다.
        Role requestUserRole = requestUserPermission.getWorkspaceRole().getRole();
        if (requestUserPermission.getWorkspaceUser().getUserId().equals(updateUserPermission.getWorkspaceUser().getUserId())) {
            return;
        }

        //상위 유저에 대해서는 플랜을 변경할 수 없음.
        if (requestUserPermission.getId() > updateUserPermission.getId()) {
            if (updateUserPermission.getWorkspaceRole().getRole() == Role.MASTER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_INFO_UPDATE_MASTER_PLAN);
            }
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //워크스페이스 설정 정보에 따라 권한 체크가 달라짐.
        Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.USER_PLAN_MANAGEMENT_ROLE_SETTING);
        if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
            //요청 유저 권한 체크 -> 마스터 또는 매니저가 아니면 던짐
            if (requestUserRole != Role.MASTER && requestUserRole != Role.MANAGER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        }
        if (workspaceCustomSettingOptional.isPresent()) {
            log.info("[REVISE MEMBER INFO] workspace custom setting value : [{}]", workspaceCustomSettingOptional.get().getValue());
            //요청 유저 권한 체크 -> 마스터가 아니면 던짐
            if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER && requestUserRole != Role.MASTER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            //요청 유저 권한 체크 -> 마스터 또는 매니저 또는 멤버가 아니면 던짐
            if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
                if (requestUserRole != Role.MASTER && requestUserRole != Role.MANAGER && requestUserRole != Role.MEMBER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
        }
    }

    /**
     * 워크스페이스 유저 역할 변경 유효성 검증
     *
     * @param requestUserPermission - 역할 변경을 시도하는 유저의 역할
     * @param updateUserPermission  - 역할 변경 대상 유저의 현재 역할
     * @param workspaceId           - 해당 워크스페이스 식별자
     */
    private void checkUserRoleUpdatePermission(WorkspaceUserPermission
                                                       requestUserPermission, WorkspaceUserPermission updateUserPermission, String workspaceId) {
        /**
         * 요청 : 마스터, 대상 : 마스터 (x)
         * 요청 : 마스터, 대상 : 매니저 (o)
         * 요청 : 마스터, 대상 : 멤버 (o)
         * 요청 : 매니저, 대상 : 마스터 (x)
         * 요청 : 매니저, 대상 : 매니저 (x)
         * 요청 : 매니저, 대상 : 멤버 (o)
         * 요청 : 멤버, 대상 : 마스터 (x)
         * 요청 : 멤버, 대상 : 매니저 (x)
         * 요청 : 멤버, 대상 : 멤버 (x)
         *
         * DEFAULT : 요청 유저는 마스터만 허용한다.
         */

        //상위 유저, 동급 유저에 대해서는 역할을 변경할 수 없음.
        if (requestUserPermission.getId() >= updateUserPermission.getId()) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //워크스페이스 설정 정보에 따라 권한 체크가 달라짐.
        Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.USER_ROLE_MANAGEMENT_ROLE_SETTING);
        Role requestUserRole = requestUserPermission.getWorkspaceRole().getRole();
        if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER) {
            //요청 유저 권한 체크 -> 마스터가 아니면 던짐
            if (requestUserRole != Role.MASTER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        }
        if (workspaceCustomSettingOptional.isPresent()) {
            log.info("[REVISE MEMBER INFO] workspace custom setting value : [{}]", workspaceCustomSettingOptional.get().getValue());
            //요청 유저 권한 체크 -> 마스터 또는 매니저가 아니면 던짐
            if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
                if (requestUserRole != Role.MASTER && requestUserRole != Role.MANAGER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
            //요청 유저 권한 체크 -> 마스터 또는 매니저 또는 멤버가 아니면 던짐
            if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
                if (requestUserRole != Role.MASTER && requestUserRole != Role.MANAGER && requestUserRole != Role.MEMBER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
        }
    }


    private void grantWorkspaceLicenseToUser(String workspaceId, String userId, String productName) {
        ApiResponse<MyLicenseInfoResponse> myLicenseInfoResponseApiResponse = licenseRestService.grantWorkspaceLicenseToUser(workspaceId, userId, productName);
        if (myLicenseInfoResponseApiResponse.getCode() != 200 || !myLicenseInfoResponseApiResponse.getData().getProductName().equals(productName)) {
            log.error("[GRANT LICENSE TO USER] request workspaceId : {}, request userId : {}, request productName : {}, response code : {}", workspaceId, userId, productName, myLicenseInfoResponseApiResponse.getCode());
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_GRANT_FAIL);
        }
    }

    private void revokeWorkspaceLicenseToUser(String workspaceId, String userId, String productName) {
        ApiResponse<Boolean> revokeWorkspaceLicenseApiResponse = licenseRestService.revokeWorkspaceLicenseToUser(workspaceId, userId, productName);
        if (revokeWorkspaceLicenseApiResponse.getCode() != 200 || !revokeWorkspaceLicenseApiResponse.getData()) {
            log.error("[REVOKE LICENSE TO USER] request workspaceId : {}, request userId : {}, request productName : {}, response code : {}", workspaceId, userId, productName, revokeWorkspaceLicenseApiResponse.getCode());
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_LICENSE_REVOKE_FAIL);
        }
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


        WorkspaceUserPermission workspaceUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
                workspace, memberKickOutRequest.getUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        WorkspaceUserPermission kickedUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(
                workspace, memberKickOutRequest.getKickedUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        Role requestUserRole = workspaceUserPermission.getWorkspaceRole().getRole();
        log.debug("[WORKSPACE KICK OUT USER] Request user Role >> [{}], Response user Role >> [{}]", requestUserRole, kickedUserPermission.getWorkspaceRole().getRole());
        //마스터는 내보내기 할 수 없음
        if (kickedUserPermission.getWorkspaceRole().getRole() == Role.MASTER) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //상위 유저, 동급유저에 대해서는 역할을 변경할 수 없음.
        if (workspaceUserPermission.getId() >= kickedUserPermission.getId()) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        /**
         * 권한체크
         * setting이 있는 경우
         *  매니저인 경우 -> 요청자는 마스터 or 매니저, 대상자는 매니저 or 멤버  (default)
         *                  단 요청자가 매니저일땐 대상자는 매니저 일 수 x
         *  마스터인 경우 -> 요청자는 마스터, 대상자는 매니저 or 멤버
         *  멤버인 경우 -> 요청자는 마스터 or 매니저 or 멤버, 대상자는 멤버
         */
        Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspaceId, SettingName.PUBLIC_USER_MANAGEMENT_ROLE_SETTING);
        if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
            if (requestUserRole != Role.MASTER && requestUserRole != Role.MANAGER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        }
        if (workspaceCustomSettingOptional.isPresent()) {
            log.info("[WORKSPACE KICK OUT USER] workspace custom setting value : [{}]", workspaceCustomSettingOptional.get().getValue());
            if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER && requestUserRole != Role.MASTER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
            //TODO: workpsace setting이 들어갈때 멤버<-> 멤버 사이에 내보내기 기능 활성황 여부에 따라 달라질 수 있음.
            if (workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
                if (requestUserRole != Role.MASTER && requestUserRole != Role.MANAGER && requestUserRole != Role.MEMBER) {
                    throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
                }
            }
        }

        //라이선스 해제
        MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(workspaceId, memberKickOutRequest.getKickedUserId());
        if (!CollectionUtils.isEmpty(myLicenseInfoListResponse.getLicenseInfoList())) {
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
        Context context = mailContextHandler.getWorkspaceKickoutContext(workspace.getName(), masterUser);
        List<String> receiverEmailList = new ArrayList<>();
        receiverEmailList.add(kickedUser.getEmail());
        applicationEventPublisher.publishEvent(new MailSendEvent(context, Mail.WORKSPACE_KICKOUT, locale, receiverEmailList));

        //history 저장
        String message = messageSource.getMessage(
                "WORKSPACE_EXPELED", new String[]{masterUser.getNickname(), kickedUser.getNickname()}, locale);
        applicationEventPublisher.publishEvent(new HistoryAddEvent(message, kickedUser.getUuid(), workspace));

        applicationEventPublisher.publishEvent(new UserWorkspacesDeleteEvent(memberKickOutRequest.getKickedUserId()));
        return new ApiResponse<>(true);
    }

    /*
하위유저는 상위유저 또는 동급유저에 대한 권한이 없으므로 이에 대해 체크한다.
단 멤버유저의 경우 동급유저(멤버)에 대한 권한을 허용한다.
 *//*
    private boolean checkWorkspaceRole(SettingValue settingValue, String requestUserRole, String responseUserRole) {
        log.info("[WORKSPACE ROLE CHECK] setting value : [{}], request user role : [{}] , response user role : [{}]", settingValue, requestUserRole, responseUserRole);
        //요청자가 마스터 -> 대상자는 매니저, 멤버
        if (settingValue == SettingValue.MASTER) {
            if (!requestUserRole.equals("MASTER") || !responseUserRole.matches("MANAGER|MEMBER")) {
                return false;
            }

        }
        //요청자가 마스터 -> 대상자는 매니저, 멤버
        //요청자가 매니저 -> 대상자는 멤버
        if (settingValue == SettingValue.MASTER_OR_MANAGER) {
            if (requestUserRole.equals("MASTER") && !responseUserRole.matches("MANAGER|MEMBER")) {
                return false;
            }
            if (requestUserRole.equals("MANAGER") && !responseUserRole.equals("MEMBER")) {
                return false;
            }
            if (requestUserRole.equals("MEMBER")) {
                return false;
            }
        }
        //요청자가 마스터  -> 대상자는 매니저, 멤버
        //요청자가 매니저 -> 대상자는 멤버
        //요청자가 멤버 -> 대상자는 멤버
        if (settingValue == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
            if (requestUserRole.equals("MASTER") && !responseUserRole.matches("MANAGER|MEMBER")) {
                return false;
            }
            if (requestUserRole.equals("MANAGER") && !responseUserRole.equals("MEMBER")) {
                return false;
            }
            if (requestUserRole.equals("MEMBER") && !responseUserRole.equals("MEMBER")) {
                return false;
            }
        }
        return true;
    }*/

    @Profile("!onpremise")
    public abstract ApiResponse<Boolean> inviteWorkspace(String workspaceId, WorkspaceInviteRequest
            workspaceInviteRequest, Locale locale);

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
        MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(workspaceId, userId);
        if (!CollectionUtils.isEmpty(myLicenseInfoListResponse.getLicenseInfoList())) {
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

    private Function<WorkspaceUserLicenseInfoResponse, String> getWorkspaceUserLicenseInfoResponseSortKey
            (String
                     sortName) {
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
    public abstract WorkspaceMemberInfoListResponse createWorkspaceMemberAccount(String
                                                                                         workspaceId, MemberAccountCreateRequest memberAccountCreateRequest);

    @Profile("onpremise")
    @Transactional
    public abstract boolean deleteWorkspaceMemberAccount(String workspaceId, MemberAccountDeleteRequest
            memberAccountDeleteRequest);

    @Profile("onpremise")
    @Transactional
    public abstract WorkspaceMemberPasswordChangeResponse memberPasswordChange
            (WorkspaceMemberPasswordChangeRequest
                     passwordChangeRequest, String workspaceId);

    /**
     * 워크스페이스 시트 계정 생성
     *
     * @param workspaceId             - 요청 워크스페이스 식별자
     * @param memberSeatCreateRequest - 시트 계정 생성 요청 정보
     * @return - 생성된 시트 계정 목록
     */
    @Transactional
    public WorkspaceMemberInfoListResponse createWorkspaceMemberSeat(String
                                                                             workspaceId, MemberSeatCreateRequest
                                                                             memberSeatCreateRequest) {
        //1-1. 요청한 사람의 권한 체크
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        checkSeatMemberManagementPermission(workspace, memberSeatCreateRequest.getUserId());

        //1-2. 라이선스 갯수 유효성 체크
        WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse = getWorkspaceLicensesByWorkspaceId(workspaceId);
        checkSeatMemberCreateLicense(memberSeatCreateRequest, workspaceLicensePlanInfoResponse);

        //1-3. 워크스페이스 멤버 제한 수 체크
        int requestSeatUserAmount = memberSeatCreateRequest.getPlanRemoteAndView() + memberSeatCreateRequest.getPlanRemote() + memberSeatCreateRequest.getPlanView();
        checkWorkspaceMaxUserAmount(workspaceId, requestSeatUserAmount, workspaceLicensePlanInfoResponse);

        //3. 시트 계정 생성
        List<WorkspaceUserInfoResponse> workspaceMemberInfoList = new ArrayList<>();
        for (int i = 0; i < memberSeatCreateRequest.getPlanRemoteAndView(); i++) {
            //게정 생성
            UserInfoRestResponse userInfoRestResponse = userRestService.registerMemberRequest(new RegisterMemberRequest(), "workspace-server").getData();//TODO API 생기면 교체
            //라이선스 할당
            grantWorkspaceLicenseToUser(workspaceId, userInfoRestResponse.getUuid(), "REMOTE");
            grantWorkspaceLicenseToUser(workspaceId, userInfoRestResponse.getUuid(), "VIEW");
            //시트 멤버 저장
            setWorkspaceMember(userInfoRestResponse.getUuid(), workspace, Role.SEAT);
            //응답
            WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(userInfoRestResponse);
            workspaceMemberInfoList.add(workspaceUserInfoResponse);
        }
        for (int i = 0; i < memberSeatCreateRequest.getPlanRemote(); i++) {
            //게정 생성
            UserInfoRestResponse userInfoRestResponse = userRestService.registerMemberRequest(new RegisterMemberRequest(), "workspace-server").getData();//TODO API 생기면 교체
            //라이선스 할당
            grantWorkspaceLicenseToUser(workspaceId, userInfoRestResponse.getUuid(), "REMOTE");
            //시트 멤버 저장
            setWorkspaceMember(userInfoRestResponse.getUuid(), workspace, Role.SEAT);
            //응답
            WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(userInfoRestResponse);
            workspaceMemberInfoList.add(workspaceUserInfoResponse);
        }
        for (int i = 0; i < memberSeatCreateRequest.getPlanView(); i++) {
            //게정 생성
            UserInfoRestResponse userInfoRestResponse = userRestService.registerMemberRequest(new RegisterMemberRequest(), "workspace-server").getData();//TODO API 생기면 교체
            //라이선스 할당
            grantWorkspaceLicenseToUser(workspaceId, userInfoRestResponse.getUuid(), "VIEW");
            //시트 멤버 저장
            setWorkspaceMember(userInfoRestResponse.getUuid(), workspace, Role.SEAT);
            //응답
            WorkspaceUserInfoResponse workspaceUserInfoResponse = restMapStruct.userInfoRestResponseToWorkspaceUserInfoResponse(userInfoRestResponse);
            workspaceMemberInfoList.add(workspaceUserInfoResponse);
        }
        return new WorkspaceMemberInfoListResponse(workspaceMemberInfoList);
    }

    /**
     * 유저에게 워크스페이스 할당
     *
     * @param userId    - 요청 유저 식별자
     * @param workspace - 할당할 워크스페이스 정보
     * @param roleName  - 할당할 역할 이름
     */
    @Transactional
    public void setWorkspaceMember(String userId, Workspace workspace, Role roleName) {
        WorkspaceUser workspaceUser = WorkspaceUser.builder().userId(userId).workspace(workspace).build();
        WorkspaceRole workspaceRole = workspaceRoleRepository.findByRole(roleName).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_ROLE_NOT_FOUND));
        WorkspacePermission workspacePermission = workspacePermissionRepository.findById(Permission.ALL.getValue()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_PERMISSION_NOT_FOUND));
        WorkspaceUserPermission workspaceUserPermission = WorkspaceUserPermission.builder().workspaceUser(workspaceUser).workspaceRole(workspaceRole).workspacePermission(workspacePermission).build();
        workspaceUserRepository.save(workspaceUser);
        workspaceUserPermissionRepository.save(workspaceUserPermission);
    }

    /**
     * 시트 계정 생성 시 라이선스 갯수 검증
     *
     * @param memberSeatCreateRequest          - 시트 계정 생성 요청 정보
     * @param workspaceLicensePlanInfoResponse - 워크스페이스 라이선스 정보
     */
    private void checkSeatMemberCreateLicense(MemberSeatCreateRequest
                                                      memberSeatCreateRequest, WorkspaceLicensePlanInfoResponse workspaceLicensePlanInfoResponse) {
        Optional<Integer> optionalRemote = workspaceLicensePlanInfoResponse.getLicenseProductInfoList().stream().filter(licenseProductInfoResponse -> licenseProductInfoResponse.getProductName().equals("REMOTE")).map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getUnUseLicenseAmount).findFirst();
        Optional<Integer> optionalView = workspaceLicensePlanInfoResponse.getLicenseProductInfoList().stream().filter(licenseProductInfoResponse -> licenseProductInfoResponse.getProductName().equals("VIEW")).map(WorkspaceLicensePlanInfoResponse.LicenseProductInfoResponse::getUnUseLicenseAmount).findFirst();
        if (memberSeatCreateRequest.getPlanRemoteAndView() > 0) {
            if (!optionalRemote.isPresent() || !optionalView.isPresent() || memberSeatCreateRequest.getPlanRemoteAndView() > optionalRemote.get() || memberSeatCreateRequest.getPlanRemoteAndView() > optionalView.get()) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_SEAT_USER_CREATE_LACK_LICENSE);
            }
        }
        if (memberSeatCreateRequest.getPlanRemote() > 0) {
            if (!optionalRemote.isPresent() || memberSeatCreateRequest.getPlanRemote() > optionalRemote.get()) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_SEAT_USER_CREATE_LACK_LICENSE);
            }
        }
        if (memberSeatCreateRequest.getPlanView() > 0) {
            if (!optionalView.isPresent() || memberSeatCreateRequest.getPlanView() > optionalView.get()) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_SEAT_USER_CREATE_LACK_LICENSE);
            }
        }
    }

    /**
     * 시트 계정 생성, 삭제, 정보편집에 대한 권한 검증
     *
     * @param workspace     - 검증 대상 워크스페이스 정보
     * @param requestUserId - 계정 생성 요청 유저 식별자
     */
    private void checkSeatMemberManagementPermission(Workspace workspace, String requestUserId) {
        WorkspaceUserPermission requestUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, requestUserId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        Optional<WorkspaceCustomSetting> workspaceCustomSettingOptional = workspaceCustomSettingRepository.findByWorkspace_UuidAndSetting_Name(workspace.getUuid(), SettingName.SEAT_MANAGEMENT_ROLE_SETTING);
        Role requestUserRole = requestUserPermission.getWorkspaceRole().getRole();
        if (!workspaceCustomSettingOptional.isPresent() || workspaceCustomSettingOptional.get().getValue() == SettingValue.UNUSED || workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER) {
            if (requestUserRole != Role.MASTER && requestUserRole != Role.MANAGER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        }
        if (workspaceCustomSettingOptional.isPresent() && workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER) {
            if (requestUserPermission.getWorkspaceRole().getRole() == Role.MASTER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        }
        if (workspaceCustomSettingOptional.isPresent() && workspaceCustomSettingOptional.get().getValue() == SettingValue.MASTER_OR_MANAGER_OR_MEMBER) {
            if (requestUserRole != Role.MASTER && requestUserRole != Role.MANAGER && requestUserRole != Role.MEMBER) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
            }
        }
    }

    /**
     * 라이선스서버 - 워크스페이스 서버 라이선스 조회
     *
     * @param workspaceId - 요청 워크스페이스 식별자
     * @return - 워크스페이스 라이선스 정보
     */
    WorkspaceLicensePlanInfoResponse getWorkspaceLicensesByWorkspaceId(String workspaceId) {
        ApiResponse<WorkspaceLicensePlanInfoResponse> apiResponse = licenseRestService.getWorkspaceLicenses(workspaceId);
        if (apiResponse.getCode() != 200) {
            log.error("[GET WORKSPACE LICENSE PLAN INFO BY WORKSPACE UUID] response message : {}", apiResponse.getMessage());
            return new WorkspaceLicensePlanInfoResponse();
        }
        return apiResponse.getData();
    }

    /**
     * 워크스페이스 최대 멤버 제한 수 검증
     *
     * @param workspaceId                      - 검증 대상 워크스페이스 식별자
     * @param requestUserAmount                - 참여 요청 유저 수
     * @param workspaceLicensePlanInfoResponse - 워크스페이스 라이선스 정보
     */
    void checkWorkspaceMaxUserAmount(String workspaceId, int requestUserAmount, WorkspaceLicensePlanInfoResponse
            workspaceLicensePlanInfoResponse) {
        //마스터포함 워크스페이스 전체 유저 수 조회
        long workspaceUserAmount = workspaceUserRepository.countByWorkspace_Uuid(workspaceId);

        if (workspaceLicensePlanInfoResponse.getMaxUserAmount() > 0) {
            // 라이선스를 구매한 워크스페이스는 라이선스에 종속된 값으로 체크
            if (requestUserAmount + workspaceUserAmount > workspaceLicensePlanInfoResponse.getMaxUserAmount()) {
                log.error("[CHECK WORKSPACE MAX USER AMOUNT] maximum workspace user amount(by license) : [{}], request user amount [{}], current workspace user amount : [{}]", workspaceLicensePlanInfoResponse.getMaxUserAmount(), requestUserAmount, workspaceUserAmount);
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_MAX_USER_AMOUNT_OVER);
            }
        } else {
            // 라이선스를 구매하지 않은 워크스페이스는 기본값으로 체크
            if (requestUserAmount + workspaceUserAmount > MAX_WORKSPACE_USER_AMOUNT) {
                log.error("[CHECK WORKSPACE MAX USER AMOUNT] maximum workspace user amount(by workspace) : [{}], request user amount [{}], current workspace user amount : [{}]", MAX_WORKSPACE_USER_AMOUNT, requestUserAmount, workspaceUserAmount);
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_MAX_USER_AMOUNT_OVER);
            }
        }
    }

    /**
     * 워크스페이스 시트 계정 삭제 및 워크스페이스에서 내보내기
     *
     * @param workspaceId             - 삭제 대상 워크스페이스 식별자
     * @param memberSeatDeleteRequest - 삭제 대상 유저 및 삭제 요청 유저 정보
     * @return - 삭제 결과
     */
    public MemberSeatDeleteResponse deleteWorkspaceMemberSeat(String workspaceId, MemberSeatDeleteRequest
            memberSeatDeleteRequest) {
        //1-1. 요청한 사람의 권한 체크
        Workspace workspace = workspaceRepository.findByUuid(workspaceId).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_NOT_FOUND));
        checkSeatMemberManagementPermission(workspace, memberSeatDeleteRequest.getUserId());

        //1-2. 요청받은 유저가 시트유저인지 체크
        WorkspaceUserPermission deleteUserPermission = workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberSeatDeleteRequest.getDeleteUserId()).orElseThrow(() -> new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_NOT_FOUND));
        if (deleteUserPermission.getWorkspaceRole().getRole() != Role.SEAT) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_SEAT_USER_DELETE);
        }

        //2. 라이선스 해제 요청
        MyLicenseInfoListResponse myLicenseInfoListResponse = getMyLicenseInfoRequestHandler(workspaceId, memberSeatDeleteRequest.getDeleteUserId());
        if (!myLicenseInfoListResponse.getLicenseInfoList().isEmpty()) {
            List<String> productList = myLicenseInfoListResponse.getLicenseInfoList().stream().map(MyLicenseInfoResponse::getProductName).collect(Collectors.toList());
            productList.forEach(productName -> revokeWorkspaceLicenseToUser(workspaceId, memberSeatDeleteRequest.getUserId(), productName));
        }

        //3. 유저서버 삭제 요청
        deleteUserByUserId(memberSeatDeleteRequest.getDeleteUserId());

        //4. 웤스 서버에서 삭제
        WorkspaceUser workspaceUser = deleteUserPermission.getWorkspaceUser();
        workspaceUserPermissionRepository.delete(deleteUserPermission);
        workspaceUserRepository.delete(workspaceUser);

        return new MemberSeatDeleteResponse(true, LocalDateTime.now());
    }

    /**
     * 유저서버 - 유저 삭제
     *
     * @param userId - 삭제 대상 유저 식별자
     */
    private void deleteUserByUserId(String userId) {
        ApiResponse<UserDeleteRestResponse> apiResponse = userRestService.userDeleteRequest(userId, "workspace-server");
        if (apiResponse.getCode() != 200) {
            log.error("[DELETE USER BY USER UUID] request userId : {}, response code : {}", userId, apiResponse.getCode());
        }
    }
}

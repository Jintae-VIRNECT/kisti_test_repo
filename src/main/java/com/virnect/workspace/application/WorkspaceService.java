package com.virnect.workspace.application;

import com.virnect.workspace.dao.*;
import com.virnect.workspace.domain.*;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.dto.MemberInfoDTO;
import com.virnect.workspace.dto.request.WorkspaceCreateRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteMailRequest;
import com.virnect.workspace.dto.request.WorkspaceInviteRequest;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.exception.BusinessException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.constant.Permission;
import com.virnect.workspace.global.constant.Role;
import com.virnect.workspace.global.constant.UUIDType;
import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.global.util.RandomStringTokenUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceUserRepository workspaceUserRepository;
    private final WorkspaceRoleRepository workspaceRoleRepository;
    private final WorkspacePermissionRepository workspacePermissionRepository;
    private final WorkspaceUserPermissionRepository workspaceUserPermissionRepository;
    private final UserRestService userRestService;
    private final ModelMapper modelMapper;
    private final GroupService groupService;
    private final RedisService redisService;
    private final MessageRestService messageRestService;

    @Value("${serverUrl}")
    private String serverUrl;

    /**
     * 워크스페이스 생성
     *
     * @param workspaceCreateRequest - 생성 할 워크스페이스 정보
     * @return - 생성 된 워크스페이스 정보
     */
    public ApiResponse<WorkspaceCreateResponse> createWorkspace(WorkspaceCreateRequest workspaceCreateRequest) {
        //User Service 에서 유저 조회
        if (getUserInfo(workspaceCreateRequest.getUserId()).getUserType().equals("SUB_USER")) {
            throw new BusinessException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //워크스페이스 생성
        String uuid = RandomStringTokenUtil.generate(UUIDType.UUID_WITH_SEQUENCE, 0);
        String pinNumber = RandomStringTokenUtil.generate(UUIDType.PIN_NUMBER, 0);

        Workspace newWorkspace = Workspace.builder()
                .uuid(uuid)
                .userId(workspaceCreateRequest.getUserId())
                .description(workspaceCreateRequest.getDescription())
                .pinNumber(pinNumber)
                .build();

        this.workspaceRepository.save(newWorkspace);

        // 워크스페이스 소속 할당
        WorkspaceUser newWorkspaceUser = WorkspaceUser.builder()
                .userId(workspaceCreateRequest.getUserId())
                .workspace(newWorkspace)
                .build();
        this.workspaceUserRepository.save(newWorkspaceUser);

        // 워크스페이스 권한 할당
        WorkspaceRole workspaceRole = WorkspaceRole.builder().id(Role.MASTER.getValue()).build();
        WorkspacePermission workspacePermission = WorkspacePermission.builder().id(Permission.ALL.getValue()).build();
        WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceRole(workspaceRole)
                .workspacePermission(workspacePermission)
                .workspaceUser(newWorkspaceUser)
                .build();
        this.workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

        return new ApiResponse<>(new WorkspaceCreateResponse(newWorkspace));
    }

    /**
     * 사용자 소속 워크스페이스 조회
     *
     * @param userId - 사용자 uuid
     * @return - 소속된 워크스페이스 정보
     */
    public ApiResponse<WorkspaceListResponse> getUserWorkspaces(String userId) {
        List<WorkspaceListResponse.WorkspaceInfo> workspaceListResponse = new ArrayList<>();
        this.workspaceUserRepository.findByUserId(userId).forEach(workspaceUser -> {
            Workspace workspace = workspaceUser.getWorkspace();
            WorkspaceListResponse.WorkspaceInfo workspaceInfo = modelMapper.map(workspace, WorkspaceListResponse.WorkspaceInfo.class);
            WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser);
            workspaceInfo.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            workspaceListResponse.add(workspaceInfo);
        });
        return new ApiResponse<>(new WorkspaceListResponse(workspaceListResponse));
    }

    public ApiResponse<MemberListResponse> getMembers(String workspaceId, String userId, String search, String filter, Pageable pageable) {
        //1. 검색어, 정렬 검증(UserService에서)
        UserInfoListResponse userInfoListResponse = this.userRestService.getUserInfoListUserIdAndSearchKeyword(userId, search, pageable).getData();

        List<MemberInfoDTO> memberInfoList = userInfoListResponse.getUserInfoList().stream().map(object -> {
            MemberInfoDTO memberInfo = modelMapper.map(object, MemberInfoDTO.class);
            memberInfo.setRole(getWorkspaceUserRole(workspaceId, memberInfo.getUuid()).getRole());
            return memberInfo;
        }).collect(Collectors.toList());

        List<MemberInfoDTO> resultMemberListResponse;

        //2. 필터 검증
        if (StringUtils.hasText(filter)) {
            resultMemberListResponse = this.workspaceUserPermissionRepository.findUserInfoListFilterd(memberInfoList, workspaceId, filter);
        } else {
            resultMemberListResponse = memberInfoList;
        }

        PageMetadataResponse pageMetadataResponse = new PageMetadataResponse();
        pageMetadataResponse.setCurrentPage(pageable.getPageNumber());
        pageMetadataResponse.setCurrentSize(pageable.getPageSize());
        pageMetadataResponse.setTotalElements(resultMemberListResponse.size());
        pageMetadataResponse.setTotalPage(userInfoListResponse.getPageMeta().getTotalPage());
        return new ApiResponse<>(new MemberListResponse(resultMemberListResponse, pageMetadataResponse));
    }

    /**
     * 워크스페이스 정보 조회(현재는 멤버수만 return 추후에.. +라이선스, +워크스페이스 Strorage 정보)
     *
     * @param workspaceId - 워크스페이스 uuid
     * @param userId      - 사용자 uuid
     * @return - 워크스페이스 정보
     */
    public ApiResponse<WorkspaceInfoResponse> getWorkspaceInfo(String workspaceId, String userId) {
        //마스터 인지 체크
        if (getUserInfo(userId).getUserType().equals("SUB_USER")) {
            throw new BusinessException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        //워크스페이스 소속 멤버 수 조회
        long countUsers = this.workspaceUserRepository.countWorkspaceUser(workspaceId);

        //라이선스 수 조회
        //제품 이용 멤버 수 조회(Remote, Make, View)
        //워크스페이스 Storage 수 조회

        return new ApiResponse<>(new WorkspaceInfoResponse(countUsers));
    }

    /**
     * 유저 정보 조회(User Service)
     *
     * @param userId - 유저 uuid
     * @return - 유저 정보
     */
    private UserInfoResponse getUserInfo(String userId) {
        ApiResponse<UserInfoResponse> userInfoResponse = this.userRestService.getUserInfoByUserId(userId);
        return userInfoResponse.getData();
    }


    /**
     * 워크스페이스 유저 초대(추후에 + User서버 api, +라이선스 검사)
     *
     * @param workspaceId                - 초대 할 워크스페이스 uuid
     * @param workspaceInviteRequestList - 초대 유저 정보
     * @return
     */
    public ApiResponse<WorkspaceInviteResponse> inviteWorkspace(String workspaceId, String userId, List<WorkspaceInviteRequest> workspaceInviteRequestList) {
        //1. 요청한 사람이 마스터유저 또는 매니저유저인지 체크
        if (getWorkspaceUserRole(workspaceId, userId).getRole().equals("MEMBER")) {
            throw new BusinessException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        //2. 라이선스검사(해야됨)

        //3. 이미 초대된 사용자인지 체크(해야됨)
        /*ResponseMessage responseMessage = this.userRestService.getUserInfoByEmail();*/

        //4. 이메일 발송
        WorkspaceInviteMailRequest.InviteInfo inviteInfo = new WorkspaceInviteMailRequest.InviteInfo();
        List<WorkspaceInviteMailRequest.InviteInfo> inviteInfos = new ArrayList<>();
        WorkspaceInviteMailRequest workspaceInviteMailRequest = new WorkspaceInviteMailRequest();

        for (WorkspaceInviteRequest workspaceInviteRequest : workspaceInviteRequestList) {
            inviteInfo.setInviteUserEmail(workspaceInviteRequest.getUserEmail());
            inviteInfo.setInviteUserName("초대받은사람");
            inviteInfos.add(inviteInfo);
        }

        String acceptUrl = serverUrl + "/" + workspaceId + "/invite/accept";
        String inviteCode = RandomStringTokenUtil.generate(UUIDType.INVITE_CODE, 6);

        workspaceInviteMailRequest.setAcceptUrl(acceptUrl);
        workspaceInviteMailRequest.setInviteCode(inviteCode);
        workspaceInviteMailRequest.setInviteInfos(inviteInfos);
        workspaceInviteMailRequest.setRequestUserId(userId);
        workspaceInviteMailRequest.setRequestUserName("초대한사람");

        boolean workspaceInvite = this.messageRestService.sendMail(workspaceInviteMailRequest).getData().isWorkspaceInvite();

        //5. redis에 초대 정보 넣기
        this.redisService.setInviteInfo(userId, workspaceId, inviteCode, workspaceInviteRequestList);
        return new ApiResponse<>(new WorkspaceInviteResponse(workspaceInvite));

    }

    /**
     * 워크스페이스 내 role 조회
     *
     * @param workspaceId - 조회 대상 워크스페이스 uuid
     * @param userId      - 조회 대상 사용자 uuid
     * @return - WorkspaceRole 엔티티
     */
    public WorkspaceRole getWorkspaceUserRole(String workspaceId, String userId) {
        return this.workspaceUserPermissionRepository.findWorkspaceUserRole(workspaceId, userId);
    }

    /**
     * 워크스페이스 초대 수락(소속 권한 부여)
     *
     * @param workspaceId - 수락한 워크스페이스 uuid
     * @param userId      - 수락한 사용자 uuid
     * @param code        - 초대 시 받은 코드
     * @return
     */
    public ApiResponse<WorkspaceInviteAcceptResponse> inviteWorkspaceAccept(String workspaceId, String userId, String code) {
        //1. redis에서 초대 정보 확인
        UserInvite userInvite = this.redisService.getInviteInfo(userId, code);
        if (userInvite == null) {
            throw new BusinessException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //2. 워크스페이스 소속 넣기 (workspace_user)
        WorkspaceUser workspaceUser = WorkspaceUser.builder()
                .userId(userId)
                .workspace(this.workspaceRepository.findByUuid(workspaceId))
                .build();
        this.workspaceUserRepository.save(workspaceUser);

        //3. 워크스페이스 권한 부여하기 (workspace_user_permission)
        if (userInvite.getPermission().size() > 0) {
            for (long permissionId : userInvite.getPermission()) {

                WorkspaceRole workspaceRole = WorkspaceRole.builder().id(Role.MANAGER.getValue()).build();
                WorkspacePermission workspacePermission = WorkspacePermission.builder().id(permissionId).build();

                WorkspaceUserPermission workspaceUserPermission = WorkspaceUserPermission.builder()
                        .workspaceRole(workspaceRole)
                        .workspacePermission(workspacePermission)
                        .workspaceUser(workspaceUser)
                        .build();
                this.workspaceUserPermissionRepository.save(workspaceUserPermission);
            }
        }

        //3. 그룹 소속 넣기(group_user, group_user_permission)
        if (userInvite.getGroups().size() > 0) {
            this.groupService.setGroupUsers(userInvite.getGroups(), workspaceUser);
        }

        return new ApiResponse<>(new WorkspaceInviteAcceptResponse(true));
    }

}

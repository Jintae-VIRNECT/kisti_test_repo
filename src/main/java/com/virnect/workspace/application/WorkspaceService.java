package com.virnect.workspace.application;

import com.virnect.workspace.dao.*;
import com.virnect.workspace.domain.*;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.dto.GroupInfoDTO;
import com.virnect.workspace.dto.MemberInfoDTO;
import com.virnect.workspace.dto.redis.WorkspaceInviteRedisRequest;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.*;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.constant.Permission;
import com.virnect.workspace.global.constant.Role;
import com.virnect.workspace.global.constant.UUIDType;
import com.virnect.workspace.global.error.ErrorCode;
import com.virnect.workspace.global.util.RandomStringTokenUtil;
import com.virnect.workspace.infra.file.FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final ProcessRestService processRestService;
    private final FileUploadService fileUploadService;

    @Value("${serverUrl}")
    private String serverUrl;

    @Value("${file.upload-path}")
    private String fileUploadPath;

    /**
     * 워크스페이스 생성
     *
     * @param workspaceCreateRequest - 생성 할 워크스페이스 정보
     * @return - 생성 된 워크스페이스 정보
     */
    public ApiResponse<WorkspaceCreateResponse> createWorkspace(WorkspaceCreateRequest workspaceCreateRequest) {
        //User Service 에서 유저 조회
        UserInfoRestResponse userInfoRestResponse = getUserInfo(workspaceCreateRequest.getUserId());

        //서브유저(유저가 만들어낸 유저)는 워크스페이스를 가질 수 없다.
        if (userInfoRestResponse.getUserType().equals("SUB_USER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //워크스페이스 생성
        String uuid = RandomStringTokenUtil.generate(UUIDType.UUID_WITH_SEQUENCE, 0);
        String pinNumber = RandomStringTokenUtil.generate(UUIDType.PIN_NUMBER, 0);

        String profile = null;
        if (workspaceCreateRequest.getProfile() != null) {
            try {
                profile = this.fileUploadService.upload(workspaceCreateRequest.getProfile());
            } catch (IOException e) {

            }

        } else {
            profile = null;//디폴트 이미지 경로.
        }

        String workspaceNickName = null;
        if (!StringUtils.hasText(workspaceCreateRequest.getName())) {
            workspaceNickName = userInfoRestResponse.getNickName() + "'s Workspace";
        }

        Workspace newWorkspace = Workspace.builder()
                .uuid(uuid)
                .userId(workspaceCreateRequest.getUserId())
                .name(workspaceNickName)
                .description(workspaceCreateRequest.getDescription())
                .profile(profile)
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
    public ApiResponse<WorkspaceInfoListResponse> getUserWorkspaces(String userId) {
        List<WorkspaceInfoListResponse.WorkspaceInfoResponse> workspaceList = new ArrayList<>();
        this.workspaceUserRepository.findByUserId(userId).forEach(workspaceUser -> {
            Workspace workspace = workspaceUser.getWorkspace();
            WorkspaceInfoListResponse.WorkspaceInfoResponse workspaceInfo = modelMapper.map(workspace, WorkspaceInfoListResponse.WorkspaceInfoResponse.class);
            WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser);

            workspaceInfo.setJoinDate(workspaceUser.getCreatedDate());
            UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
            workspaceInfo.setMasterName(userInfoRestResponse.getName());
            workspaceInfo.setMasterProfile(userInfoRestResponse.getProfile());
            workspaceInfo.setRole(workspaceUserPermission.getWorkspaceRole().getRole());

            workspaceList.add(workspaceInfo);
        });
        return new ApiResponse<>(new WorkspaceInfoListResponse(workspaceList));
    }

    /**
     * 멤버 조회
     *
     * @param workspaceId - 조회 대상 workspace uuid
     * @param userId      - 조회 중인 유저 uuid
     * @param search      - (옵션) 검색값
     * @param filter      - (옵션) 필터값 ex.MEMBER, MASTER
     * @param pageable    - 페이징 처리 값
     * @return - 멤버 정보 리스트
     */
    public ApiResponse<MemberListResponse> getMembers(String workspaceId, String userId, String search, String filter, com.virnect.workspace.global.common.PageRequest pageRequest) {

        Workspace workspace = this.workspaceRepository.findByUuid(workspaceId);
        /*
            필터가 있을 시 -> workspace 단에서 페이징하고 정렬한다.
            필터가 없을 때 > user 단에서 페이징하고 정렬한다.
        */
        MemberListResponse memberListResponse;
        if (StringUtils.hasText(filter)) {
            memberListResponse = filterdMemberList(workspace, userId, search, filter, pageRequest.of(true));
        } else {
            memberListResponse = notFilterdMemberList(workspace, userId, search, pageRequest.of(false));
        }


        return new ApiResponse<>(memberListResponse);
    }

    public MemberListResponse filterdMemberList(Workspace workspace, String userId, String search, String filter, Pageable pageable) {
        //필터로 넘어온 권한을 리스트로 변환.
        List<WorkspaceRole> workspaceRoleList = new ArrayList<>();
        if (filter.contains("MASTER")){
            workspaceRoleList.add(WorkspaceRole.builder().id(1L).build());
        }
        if (filter.contains("MANAGER")){
            workspaceRoleList.add(WorkspaceRole.builder().id(2L).build());
        }
        if (filter.contains("MEMBER")){
            workspaceRoleList.add(WorkspaceRole.builder().id(3L).build());
        }

        //user 서비스에서 받아온 유저 정보를 가지고 workspace_user를 찾는다. -> workspace 에서 필터이용해서 페이징하는 데에 사용.
        List<WorkspaceUser> workspaceUserList = new ArrayList<>();

        //user 서비스에서 페이징 안된 membList 받아온다.
        UserInfoListRestResponse userInfoListResponse = this.userRestService.getUserInfoListUserIdAndSearchKeyword(userId, search, false, pageable).getData();

        List<MemberInfoDTO> memberInfoList = userInfoListResponse.getUserInfoList().stream().map(object -> {
            workspaceUserList.add(this.workspaceUserRepository.findByUserIdAndWorkspace(object.getUuid(), workspace));

            MemberInfoDTO memberInfo = modelMapper.map(object, MemberInfoDTO.class);
            memberInfo.setRole(getWorkspaceUserRole(workspace.getUuid(), memberInfo.getUuid()).getRole());
            SubProcessCountResponse subProcessCountResponse = this.processRestService.getSubProcessCount(object.getUuid()).getData();
            memberInfo.setCountAssigned(subProcessCountResponse.getCountAssigned());
            memberInfo.setCountProgressing(subProcessCountResponse.getCountProgressing());

            return memberInfo;
        }).collect(Collectors.toList());

        //정렬은 생략, 필터링 및 페이징은 해서 pageMember 만듬.
        PageRequest newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<WorkspaceUserPermission> pageMember = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUserIsInAndWorkspaceRoleIsIn(
                workspace, workspaceUserList, workspaceRoleList, newPageable);

        List<MemberInfoDTO> resultMemberList = new ArrayList<>();

        List<String> filterdWorkspaceUserIdList =
                pageMember.stream().map(workspaceUserPermission -> workspaceUserPermission.getWorkspaceUser().getUserId()).collect(Collectors.toList());

        //필터 조건에 해당하지 않는 유저는 제외해서 memberList에 넣는다.
        memberInfoList.stream().forEach(memberInfoDTO -> {
                    if (filterdWorkspaceUserIdList.contains(memberInfoDTO.getUuid())) {
                        resultMemberList.add(memberInfoDTO);
                    }
                }
        );

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(pageMember.getTotalElements());//전체 데이터 수
        pageMetadataResponse.setTotalPage(pageMember.getTotalPages());//전체 페이지 수
        pageMetadataResponse.setCurrentPage(pageable.getPageNumber() + 1);
        pageMetadataResponse.setCurrentSize(pageable.getPageSize());

        return new MemberListResponse(resultMemberList, pageMetadataResponse);
    }

    public MemberListResponse notFilterdMemberList(Workspace workspace, String userId, String search, Pageable pageable) {

        //필터값이 없으면 페이징 처리한 값을 리턴 받는다.
        UserInfoListRestResponse userInfoListResponse = this.userRestService.getUserInfoListUserIdAndSearchKeyword(userId, search, true, pageable).getData();

        //조회 대상의 워크스페이스에 소속된 유저들만 검색 대상으로 지정.
        List<MemberInfoDTO> resultMemberList = new ArrayList<>();

        userInfoListResponse.getUserInfoList().stream().forEach(userInfoRestResponse -> {
            WorkspaceUser workspaceUser = workspaceUserRepository.findByUserIdAndWorkspace(userInfoRestResponse.getUuid(), workspace);
            if (workspaceUser != null) {
                MemberInfoDTO memberInfo = modelMapper.map(userInfoRestResponse, MemberInfoDTO.class);
                memberInfo.setRole(getWorkspaceUserRole(workspace.getUuid(), memberInfo.getUuid()).getRole());
                SubProcessCountResponse subProcessCountResponse = this.processRestService.getSubProcessCount(userInfoRestResponse.getUuid()).getData();
                memberInfo.setCountAssigned(subProcessCountResponse.getCountAssigned());
                memberInfo.setCountProgressing(subProcessCountResponse.getCountProgressing());

                resultMemberList.add(memberInfo);
            }
        });

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(userInfoListResponse.getPageMeta().getTotalElements());
        pageMetadataResponse.setTotalPage(userInfoListResponse.getPageMeta().getTotalPage());
        pageMetadataResponse.setCurrentPage(pageable.getPageNumber());
        pageMetadataResponse.setCurrentSize(pageable.getPageSize());

        return new MemberListResponse(resultMemberList, pageMetadataResponse);
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
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
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
    private UserInfoRestResponse getUserInfo(String userId) {
        ApiResponse<UserInfoRestResponse> userInfoResponse = this.userRestService.getUserInfoByUserId(userId);
        return userInfoResponse.getData();
    }


    /**
     * 워크스페이스 유저 초대(추후에 +라이선스 검사)
     *
     * @param workspaceId            - 초대 할 워크스페이스 uuid
     * @param workspaceInviteRequest - 초대 유저 정보
     * @return
     */
    public ApiResponse<WorkspaceInviteRestResponse> inviteWorkspace(String workspaceId, String
            userId, WorkspaceInviteRequest workspaceInviteRequest) {
        //1. 요청한 사람이 마스터유저 또는 매니저유저인지 체크
        if (getWorkspaceUserRole(workspaceId, userId).getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        //2. 라이선스검사(해야됨)

        //3. 계정 중복 체크(user 서비스)
        List<String> emailList = new ArrayList<>();
        workspaceInviteRequest.getUserInfoList().stream().forEach(userInfo -> emailList.add(userInfo.getUserEmail()));

        InviteUserInfoRestResponse inviteUserInfoRestResponse = this.userRestService.getUserInfoByEmailList(emailList.stream().toArray(String[]::new)).getData();

        WorkspaceInviteRedisRequest workspaceInviteRedisRequest = new WorkspaceInviteRedisRequest();
        List<WorkspaceInviteRedisRequest.UserInfo> userListRedisRequest = new ArrayList<>();

        WorkspaceInviteMailRequest.InviteInfo inviteInfo = new WorkspaceInviteMailRequest.InviteInfo();
        List<WorkspaceInviteMailRequest.InviteInfo> inviteInfoList = new ArrayList<>();

        List<WorkspaceInviteRequest.UserInfo> getUserInfo = workspaceInviteRequest.getUserInfoList();
        getUserInfo.forEach(userInfo -> {
            WorkspaceInviteRedisRequest.UserInfo userRedisRequest = new WorkspaceInviteRedisRequest.UserInfo();
            for (InviteUserInfoRestResponse.InviteUserResponse inviteUserResponse : inviteUserInfoRestResponse.getInviteUserInfoList()) {
                //Redis data set
                userRedisRequest.setGroups(userInfo.getGroups());
                userRedisRequest.setPermission(userInfo.getWorkspacePermission());
                userRedisRequest.setExistUser(false);
                userRedisRequest.setName(inviteUserResponse.getName());
                userRedisRequest.setEmail(inviteUserResponse.getEmail());
                //Message data set
                inviteInfo.setInviteUserName(inviteUserResponse.getName());
                inviteInfo.setInviteUserEmail(inviteUserResponse.getEmail());
                if (emailList.contains(inviteUserResponse.getEmail())) {
                    userRedisRequest.setExistUser(true);
                }
            }
            inviteInfoList.add(inviteInfo);
            userListRedisRequest.add(userRedisRequest);

        });

        String inviteCode = RandomStringTokenUtil.generate(UUIDType.INVITE_CODE, 6);

        //4. Redis 에서 초대 정보 넣기
        workspaceInviteRedisRequest.setUserInfoList(userListRedisRequest);
        workspaceInviteRedisRequest.setInviteCode(inviteCode);
        workspaceInviteRedisRequest.setUserId(userId);
        workspaceInviteRedisRequest.setUserId(workspaceId);
        this.redisService.setInviteInfo(workspaceInviteRedisRequest);

        //5. 초대 메일 전송
        String acceptUrl = serverUrl + "/" + workspaceId + "/invite/accept";
        WorkspaceInviteMailRequest workspaceInviteMailRequest = new WorkspaceInviteMailRequest();
        workspaceInviteMailRequest.setAcceptUrl(acceptUrl);
        workspaceInviteMailRequest.setInviteCode(inviteCode);
        workspaceInviteMailRequest.setInviteInfos(inviteInfoList);
        workspaceInviteMailRequest.setRequestUserId(userId);
        workspaceInviteMailRequest.setRequestUserName("초대한사람");

        WorkspaceInviteRestResponse workspaceInvite = this.messageRestService.sendMail(workspaceInviteMailRequest).getData();

        return new ApiResponse<>(workspaceInvite);

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
    public ApiResponse<WorkspaceInviteAcceptResponse> inviteWorkspaceAccept(String workspaceId, String
            userId, String code) {
        //1. redis에서 초대 정보 확인
        UserInvite userInvite = this.redisService.getInviteInfo(userId, code);
        if (userInvite == null) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //2. 워크스페이스 소속 넣기 (workspace_user)
        WorkspaceUser workspaceUser = setWorkspaceUserInfo(workspaceId, userId);

        //3. 워크스페이스 직책 및 권한 부여하기 (workspace_user_permission)
        if (!userInvite.getPermission().isEmpty()) {
            setWorkspaceUserPermissionInfo(userInvite.getPermission(), workspaceUser);
        }

        //4. 그룹 소속 넣기(group_user, group_user_permission)
        if (!userInvite.getGroups().isEmpty()) {
            List<GroupInfoDTO> groupInfoDTOList = userInvite.getGroups().stream().map(groupInfo -> modelMapper.map(groupInfo, GroupInfoDTO.class)).collect(Collectors.toList());
            this.groupService.setGroupUser(groupInfoDTOList, workspaceUser);
        }

        //5. 회원가입 요청

        return new ApiResponse<>(new WorkspaceInviteAcceptResponse(true));
    }

    /**
     * 워크스페이스에서 유저 생성
     *
     * @param workspaceId       - 유저 소속 workspace uuid
     * @param userId            - 유저를 생성할 마스터 또는 매니저의 uuid
     * @param userCreateRequest - 생성할 유저 정보
     * @return - 유저 생성 결과값
     */
    public ApiResponse<UsersCreateResponse> createUsers(String workspaceId, String
            userId, UsersCreateRequest userCreateRequest) {
        //1. 생성 권한 확인
        if (getWorkspaceUserRole(workspaceId, userId).getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        //2. 아이디 중복 확인
        String[] emailList = userCreateRequest.getUserInfoList().stream().map(userInfo -> userInfo.getEmail()).toArray(String[]::new);
        InviteUserInfoRestResponse inviteUserInfoRestResponse = this.userRestService.getUserInfoByEmailList(emailList).getData();
        for (InviteUserInfoRestResponse.InviteUserResponse inviteUserResponse : inviteUserInfoRestResponse.getInviteUserInfoList()) {
            if (Arrays.asList(emailList).contains(inviteUserResponse.getEmail())) {
                //이메일이 중복되는 사용자가 있어서 계정을 생성할 수 없는 경우에 에러 리턴
                throw new WorkspaceException(ErrorCode.ERR_INVALID_VALUE);
            }
        }

        //3. 워크스페이스 소속 부여 / 권한 및 직책 부여
        for (UsersCreateRequest.UserInfo userInfo : userCreateRequest.getUserInfoList()) {
            //임시 유저 uuid
            WorkspaceUser workspaceUser = setWorkspaceUserInfo(workspaceId, RandomStringTokenUtil.generate(UUIDType.UUID_WITH_SEQUENCE, 0));
            if (!userInfo.getWorkspacePermission().isEmpty()) {
                setWorkspaceUserPermissionInfo(userInfo.getWorkspacePermission(), workspaceUser);
            }

            //4. group_users, group_user_permission insert
            if (userInfo.getGroups() != null) {
                this.groupService.setGroupUser(userInfo.getGroups(), workspaceUser);
            }
        }
        //5. user insert (해야됨) : user server의 회원가입 url로 넣기

        return new ApiResponse<>(new UsersCreateResponse(true));
    }

    public Resource downloadFile(String fileName) throws IOException {
        Path file = Paths.get(fileUploadPath).resolve(fileName);
        Resource resource = new UrlResource(file.toUri());
        if (resource.getFile().exists()) {
            return resource;
        } else {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_VALUE);
        }

    }

    /**
     * 워크스페이스 소속 부여 : insert workspace_user table
     *
     * @param workspaceId - 소속 부여 대상 워크스페이스 uuid
     * @param userId      - 소속 시킬 유저 uuid
     * @return - 소속 된 워크스페이스 유저 객체
     */
    public WorkspaceUser setWorkspaceUserInfo(String workspaceId, String userId) {
        WorkspaceUser workspaceUser = WorkspaceUser.builder()
                .userId(userId)
                .workspace(this.workspaceRepository.findByUuid(workspaceId))
                .build();
        this.workspaceUserRepository.save(workspaceUser);
        return workspaceUser;
    }

    /**
     * 워크스페이스 권한/직책 부여 : insert workspace_user_permission table
     *
     * @param permissionIdList - 권한 리스트
     * @param workspaceUser    - 권한 및 직책이 부여될 사용자 정보
     */
    public void setWorkspaceUserPermissionInfo(List<Long> permissionIdList, WorkspaceUser workspaceUser) {
        /*
        role이 1이면 -> permission은 1(all)만 가능하다.
        role이 2이면 -> permission은 2~5 사이만 가능하다.
        role이 3이면 -> permission은 6(none)만 가능하다.
        */

        WorkspaceRole workspaceRole;
        for (Long permissionId : permissionIdList) {
            switch (Integer.parseInt(permissionId.toString())) {
                case 1:
                    workspaceRole = this.workspaceRoleRepository.findByRole("MASTER");
                    break;
                case 2:
                    workspaceRole = this.workspaceRoleRepository.findByRole("MANAGER");
                    break;
                case 3:
                    workspaceRole = this.workspaceRoleRepository.findByRole("MANAGER");
                    break;
                case 4:
                    workspaceRole = this.workspaceRoleRepository.findByRole("MANAGER");
                    break;
                case 5:
                    workspaceRole = this.workspaceRoleRepository.findByRole("MANAGER");
                    break;
                case 6:
                    workspaceRole = this.workspaceRoleRepository.findByRole("MEMBER");
                    break;
                default:
                    workspaceRole = null;
            }
            WorkspacePermission workspacePermission = WorkspacePermission.builder().id(permissionId).build();

            WorkspaceUserPermission workspaceUserPermission = WorkspaceUserPermission.builder()
                    .workspaceRole(workspaceRole)
                    .workspacePermission(workspacePermission)
                    .workspaceUser(workspaceUser)
                    .build();

            this.workspaceUserPermissionRepository.save(workspaceUserPermission);

            log.info("[사용자 - " + workspaceUser.getUserId() + " ] [직책 - " + workspaceRole.getRole() + " ] [권한 - " + workspacePermission.getId() + " ]");
        }
    }

    /**
     * 사용자 워크스페이스,그룹 내 권한 변경
     *
     * @param workspaceId                 - 권한 변경이 이루어지는 워크스페이스 uuid
     * @param userId                      - 권한 변경을 하는 워크스페이스 마스터 또는 매니저 유저 uuid
     * @param userPermissionReviseRequest - 변경 될 권한 정보
     * @return - 사용자 권한 정보
     */
    public ApiResponse reviseUserPermission(String workspaceId, String userId, UserPermissionReviseRequest userPermissionReviseRequest) {
        //1. 요청자 권한 확인
        if (!getWorkspaceUserRole(workspaceId, userId).getRole().equals("MASTER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        WorkspaceUser workspaceUser = this.workspaceUserRepository.findByUserIdAndWorkspace(userPermissionReviseRequest.getUserId(), workspaceRepository.findByUuid(workspaceId));

        //2. workspace permission 변경
        if (!userPermissionReviseRequest.getWorkspacePermissions().isEmpty()) {
            WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser);
            WorkspaceRole userRole = workspaceUserPermission.getWorkspaceRole();
            this.workspaceUserPermissionRepository.deleteAllByWorkspaceUser(workspaceUser);
            this.setWorkspaceUserPermissionInfo(userPermissionReviseRequest.getWorkspacePermissions(), workspaceUser);
        }
        //3. group permission 변경
        if (!userPermissionReviseRequest.getGroups().isEmpty()) {
            this.groupService.reviseGroupUserPermission(userPermissionReviseRequest.getGroups(), workspaceUser);
        }

        return new ApiResponse<>();
    }

}

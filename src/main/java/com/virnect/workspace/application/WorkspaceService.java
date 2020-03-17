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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public ApiResponse<WorkspaceInfoListResponse> getUserWorkspaces(String userId) {
        List<WorkspaceInfoListResponse.WorkspaceInfoResponse> workspaceList = new ArrayList<>();
        this.workspaceUserRepository.findByUserId(userId).forEach(workspaceUser -> {
            Workspace workspace = workspaceUser.getWorkspace();
            WorkspaceInfoListResponse.WorkspaceInfoResponse workspaceInfo = modelMapper.map(workspace, WorkspaceInfoListResponse.WorkspaceInfoResponse.class);
            WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser);
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
        /*
            필터가 있을 시 -> workspace 단에서 페이징하고 정렬한다.
            필터가 없거나 전체선택 시 -> user 단에서 페이징하고 정렬한다.
        */

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        List<MemberInfoDTO> resultMemberListResponse;
        if (StringUtils.hasText(filter)) {

            Pageable pageable = pageRequest.of(true);
            //필터 쿼리에 쓰일 woskspace
            Workspace workspace = this.workspaceRepository.findByUuid(workspaceId);

            //필터 퀴리에 쓰일 workspaceUserList
            List<WorkspaceUser> workspaceUserList = new ArrayList<>();

            //필터 쿼리에 쓰일 workspaceRoleList
            List<WorkspaceRole> workspaceRoleList = new ArrayList<>();
            if (filter.contains("MASTER")) workspaceRoleList.add(WorkspaceRole.builder().id(1L).build());
            if (filter.contains("MANAGER")) workspaceRoleList.add(WorkspaceRole.builder().id(2L).build());
            if (filter.contains("MEMBER")) workspaceRoleList.add(WorkspaceRole.builder().id(3L).build());

            //user 서비스에서 페이징 안된 membList 받아온다.
            UserInfoListRestResponse userInfoListResponse = this.userRestService.getUserInfoListUserIdAndSearchKeyword(userId, search, false, pageable).getData();
            List<MemberInfoDTO> memberInfoList = userInfoListResponse.getUserInfoList().stream().map(object -> {
                MemberInfoDTO memberInfo = modelMapper.map(object, MemberInfoDTO.class);
                memberInfo.setRole(getWorkspaceUserRole(workspaceId, memberInfo.getUuid()).getRole());
                workspaceUserList.add(this.workspaceUserRepository.findByUserIdAndWorkspace(memberInfo.getUuid(), workspace));
                return memberInfo;
            }).collect(Collectors.toList());


            Page<WorkspaceUserPermission> pageMember;


            // 정렬 검증 - 정렬을 해야 할 때 -> 임시 생략
            /*if (pageable.getSort().isSorted()) {
                PageRequest newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
                pageMember = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUserIsInAndWorkspaceRoleIsIn(
                        workspace, workspaceUserList, workspaceRoleList, newPageable);

                List<MemberInfoDTO> filterdMemberList = new ArrayList<>();

                List<String> filterdWorkspaceUserIdList =
                        pageMember.stream().map(workspaceUserPermission -> workspaceUserPermission.getWorkspaceUser().getUserId()).collect(Collectors.toList());

                //필터 조건에 해당하지 않는 유저는 제외해서 memberList에 넣는다.
                memberInfoList.stream().forEach(memberInfoDTO -> {
                            if (filterdWorkspaceUserIdList.contains(memberInfoDTO.getUuid())) {
                                filterdMemberList.add(memberInfoDTO);
                            }
                        }
                );

                //필터된 memberList 를 가지고 정렬한다.
                String sortName = pageable.getSort().toString().split(":")[0].trim();//sort의 기준이 될 열
                String sortDirection = pageable.getSort().toString().split(":")[1].trim();//sort의 방향 : 내림차순 or 오름차순

                //이메일을 기준으로 asc 정렬
                if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("asc")) {
                    resultMemberListResponse = Sort.EMAIL_ASC.sorting(filterdMemberList);
                }
                //이메일을 기준으로 desc 정렬
                if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("desc")) {
                    resultMemberListResponse = Sort.EMAIL_DESC.sorting(filterdMemberList);
                }
                //이름을 기준으로 asc 정렬
                if (sortName.equalsIgnoreCase("name") && sortDirection.equalsIgnoreCase("asc")) {
                    resultMemberListResponse = Sort.NAME_ASC.sorting(filterdMemberList);
                }
                //이름을 기준으로 desc 정렬
                if (sortName.equalsIgnoreCase("name") && sortDirection.equalsIgnoreCase("desc")) {
                    resultMemberListResponse = Sort.NAME_DESC.sorting(filterdMemberList);
                }

                resultMemberListResponse = filterdMemberList;

            } else {*/
            //정렬을 하지 않아도 될때는 필터처리만 해서 넘긴다.
                /*
                pageMember = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUserIsInAndWorkspaceRoleIsIn(
                        workspace, workspaceUserList, workspaceRoleList, pageable);
*/
            //임시로 정렬 자체 생략해서 넘김
            PageRequest newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            pageMember = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUserIsInAndWorkspaceRoleIsIn(
                    workspace, workspaceUserList, workspaceRoleList, newPageable);

            List<MemberInfoDTO> filterdMemberList = new ArrayList<>();

            List<String> filterdWorkspaceUserIdList =
                    pageMember.stream().map(workspaceUserPermission -> workspaceUserPermission.getWorkspaceUser().getUserId()).collect(Collectors.toList());

            //필터 조건에 해당하지 않는 유저는 제외해서 memberList에 넣는다.
            memberInfoList.stream().forEach(memberInfoDTO -> {
                        if (filterdWorkspaceUserIdList.contains(memberInfoDTO.getUuid())) {
                            filterdMemberList.add(memberInfoDTO);
                        }
                    }
            );
            resultMemberListResponse = filterdMemberList;
            // }

            pageMetadataResponse.setTotalElements(pageMember.getTotalElements());//전체 데이터 수
            pageMetadataResponse.setTotalPage(pageMember.getTotalPages());//전체 페이지 수
            pageMetadataResponse.setCurrentPage(pageable.getPageNumber() + 1);
            pageMetadataResponse.setCurrentSize(pageable.getPageSize());

        } else {
            Pageable pageable = pageRequest.of(false);

            //필터값이 없으면 페이징 처리한 값을 리턴 받아 그대로 리턴한다.

            UserInfoListRestResponse userInfoListResponse = this.userRestService.getUserInfoListUserIdAndSearchKeyword(userId, search, true, pageable).getData();

            List<MemberInfoDTO> memberInfoList = userInfoListResponse.getUserInfoList().stream().map(object -> {
                MemberInfoDTO memberInfo = modelMapper.map(object, MemberInfoDTO.class);
                memberInfo.setRole(getWorkspaceUserRole(workspaceId, memberInfo.getUuid()).getRole());
                return memberInfo;
            }).collect(Collectors.toList());

            resultMemberListResponse = memberInfoList;
            pageMetadataResponse.setTotalElements(userInfoListResponse.getPageMeta().getTotalElements());
            pageMetadataResponse.setTotalPage(userInfoListResponse.getPageMeta().getTotalPage());
            pageMetadataResponse.setCurrentPage(pageable.getPageNumber() + 1);
            pageMetadataResponse.setCurrentSize(pageable.getPageSize());
        }


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
            throw new BusinessException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
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
            throw new BusinessException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
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
            throw new BusinessException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        //2. 아이디 중복 확인
        String[] emailList = userCreateRequest.getUserInfoList().stream().map(userInfo -> userInfo.getEmail()).toArray(String[]::new);
        InviteUserInfoRestResponse inviteUserInfoRestResponse = this.userRestService.getUserInfoByEmailList(emailList).getData();
        for (InviteUserInfoRestResponse.InviteUserResponse inviteUserResponse : inviteUserInfoRestResponse.getInviteUserInfoList()) {
            if (Arrays.asList(emailList).contains(inviteUserResponse.getEmail())) {
                //이메일이 중복되는 사용자가 있어서 계정을 생성할 수 없는 경우에 에러 리턴
                throw new BusinessException(ErrorCode.ERR_INVALID_VALUE);
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
            throw new BusinessException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
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

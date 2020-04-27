package com.virnect.workspace.application;

import com.virnect.workspace.dao.*;
import com.virnect.workspace.dao.redis.UserInviteRepository;
import com.virnect.workspace.domain.*;
import com.virnect.workspace.domain.redis.UserInvite;
import com.virnect.workspace.dto.MemberInfoDTO;
import com.virnect.workspace.dto.UserInfoDTO;
import com.virnect.workspace.dto.WorkspaceInfoDTO;
import com.virnect.workspace.dto.WorkspaceNewMemberInfoDTO;
import com.virnect.workspace.dto.request.*;
import com.virnect.workspace.dto.response.MemberListResponse;
import com.virnect.workspace.dto.response.UsersCreateResponse;
import com.virnect.workspace.dto.response.WorkspaceInfoListResponse;
import com.virnect.workspace.dto.response.WorkspaceInfoResponse;
import com.virnect.workspace.dto.rest.*;
import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.constant.Permission;
import com.virnect.workspace.global.constant.Role;
import com.virnect.workspace.global.constant.Sort;
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
    private final MessageRestService messageRestService;
    private final ProcessRestService processRestService;
    private final FileUploadService fileUploadService;
    private final UserInviteRepository userInviteRepository;


    @Value("${file.upload-path}")
    private String fileUploadPath;

    /**
     * 워크스페이스 생성
     *
     * @param workspaceCreateRequest - 생성 할 워크스페이스 정보
     * @return - 생성 된 워크스페이스 정보
     */
    public ApiResponse<WorkspaceInfoDTO> createWorkspace(WorkspaceCreateRequest workspaceCreateRequest) {
        //필수 값 체크
        if (!StringUtils.hasText(workspaceCreateRequest.getUserId()) || !StringUtils.hasText(workspaceCreateRequest.getName()) || !StringUtils.hasText(workspaceCreateRequest.getDescription())) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        //User Service 에서 유저 조회
        UserInfoRestResponse userInfoRestResponse = getUserInfo(workspaceCreateRequest.getUserId());

        //서브유저(유저가 만들어낸 유저)는 워크스페이스를 가질 수 없다.
        if (userInfoRestResponse.getUserType().equals("SUB_USER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        // 이미 생성한 워크스페이스가 있는지 확인
        boolean userHasWorkspace = this.workspaceRepository.existsByUserId(workspaceCreateRequest.getUserId());

        if (userHasWorkspace) {
            throw new WorkspaceException(ErrorCode.ERR_MASTER_WORKSPACE_ALREADY_EXIST);
        }

        //워크스페이스 생성
        String uuid = RandomStringTokenUtil.generate(UUIDType.UUID_WITH_SEQUENCE, 0);
        String pinNumber = RandomStringTokenUtil.generate(UUIDType.PIN_NUMBER, 0);

        String profile = null;
        if (workspaceCreateRequest.getProfile() != null) {
            try {
                profile = this.fileUploadService.upload(workspaceCreateRequest.getProfile());
            } catch (IOException e) {
                throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
            }
        } else {
            profile = null;//디폴트 이미지 경로.
        }

        Workspace newWorkspace = Workspace.builder()
                .uuid(uuid)
                .userId(workspaceCreateRequest.getUserId())
                .name(workspaceCreateRequest.getName())
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

        WorkspaceInfoDTO workspaceInfoDTO = modelMapper.map(newWorkspace, WorkspaceInfoDTO.class);
        workspaceInfoDTO.setMasterUserId(newWorkspace.getUserId());
        return new ApiResponse<>(workspaceInfoDTO);
    }

    /**
     * 사용자 소속 워크스페이스 조회
     *
     * @param userId - 사용자 uuid
     * @return - 소속된 워크스페이스 정보
     */
    public ApiResponse<WorkspaceInfoListResponse> getUserWorkspaces(String userId, Pageable pageable) {
        List<WorkspaceInfoListResponse.WorkspaceInfo> workspaceList = new ArrayList<>();
        Page<WorkspaceUser> workspaceUserPage = this.workspaceUserRepository.findByUserId(userId, pageable);
        workspaceUserPage.forEach(workspaceUser -> {
            Workspace workspace = workspaceUser.getWorkspace();
            WorkspaceInfoListResponse.WorkspaceInfo workspaceInfo = modelMapper.map(workspace, WorkspaceInfoListResponse.WorkspaceInfo.class);
            WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser);

            workspaceInfo.setJoinDate(workspaceUser.getCreatedDate());
            UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
            workspaceInfo.setMasterName(userInfoRestResponse.getName());
            workspaceInfo.setMasterProfile(userInfoRestResponse.getProfile());
            workspaceInfo.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            workspaceInfo.setMasterNickName(userInfoRestResponse.getNickname());
            workspaceList.add(workspaceInfo);
        });
        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(workspaceUserPage.getTotalElements());
        pageMetadataResponse.setTotalPage(workspaceUserPage.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageable.getPageNumber());
        pageMetadataResponse.setCurrentSize(pageable.getPageSize());

        return new ApiResponse<>(new WorkspaceInfoListResponse(workspaceList, pageMetadataResponse));
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
    public ApiResponse<MemberListResponse> getMembers(String workspaceId, String search, String filter, com.virnect.workspace.global.common.PageRequest pageRequest) {
        //Pageable로 Sort처리를 할 수 없기때문에 sort값을 제외한 Pageable을 만든다.
        Pageable newPageable = PageRequest.of(pageRequest.of().getPageNumber(), pageRequest.of().getPageSize());

        List<WorkspaceRole> workspaceRoleList = new ArrayList<>();
        if (StringUtils.hasText(filter) && filter.contains("MASTER")) {
            workspaceRoleList.add(WorkspaceRole.builder().id(1L).build());
        }
        if (StringUtils.hasText(filter) && filter.contains("MANAGER")) {
            workspaceRoleList.add(WorkspaceRole.builder().id(2L).build());
        }
        if (StringUtils.hasText(filter) && filter.contains("MEMBER")) {
            workspaceRoleList.add(WorkspaceRole.builder().id(3L).build());
        }

        Workspace workspace = this.workspaceRepository.findByUuid(workspaceId);
        //워크스페이스에 해당하는 유저들에 대한 정보만 불러온다. (+ search)
        List<WorkspaceUser> workspaceUserList = this.workspaceUserRepository.findByWorkspace_Uuid(workspaceId);
        String[] workspaceUserIdList = workspaceUserList.stream().map(workspaceUser -> workspaceUser.getUserId()).toArray(String[]::new);
        UserInfoListRestResponse userInfoListRestResponse = this.userRestService.getUserInfoList(search, workspaceUserIdList).getData();

        List<MemberInfoDTO> memberInfoDTOList = new ArrayList<>();

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();

        //불러온 정보들에서 userId 가지고 페이징 처리를 한다. (+ filter)
        if (workspaceRoleList.size() > 0) {
            List<WorkspaceUser> workspaceUsers = userInfoListRestResponse.getUserInfoList().stream().map(userInfoRestResponse -> {
                return this.workspaceUserRepository.findByUserIdAndWorkspace(userInfoRestResponse.getUuid(), workspace);

            }).collect(Collectors.toList());
            Page<WorkspaceUserPermission> workspaceUserPermissionPage = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUserIsInAndWorkspaceRoleIsIn(workspace, workspaceUsers, workspaceRoleList, newPageable);
            List<WorkspaceUserPermission> filterdWorkspaceUserList = workspaceUserPermissionPage.toList();

            userInfoListRestResponse.getUserInfoList().stream().forEach(userInfoRestResponse -> {
                WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, userInfoRestResponse.getUuid());
                if (filterdWorkspaceUserList.contains(workspaceUserPermission)) {
                    MemberInfoDTO memberInfoDTO = this.modelMapper.map(userInfoRestResponse, MemberInfoDTO.class);
                    memberInfoDTO.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
                    memberInfoDTO.setJoinDate(workspaceUserPermission.getWorkspaceUser().getCreatedDate());
                    SubProcessCountResponse subProcessCountResponse = this.processRestService.getSubProcessCount(userInfoRestResponse.getUuid()).getData();
                    memberInfoDTO.setCountAssigned(subProcessCountResponse.getCountAssigned());
                    memberInfoDTO.setCountProgressing(subProcessCountResponse.getCountProgressing());
                    memberInfoDTOList.add(memberInfoDTO);
                }
            });

            pageMetadataResponse.setTotalElements(workspaceUserPermissionPage.getTotalElements());
            pageMetadataResponse.setTotalPage(workspaceUserPermissionPage.getTotalPages());
            pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
            pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());
        } else {
            List<String> userIdList = userInfoListRestResponse.getUserInfoList().stream().map(userInfoRestResponse -> userInfoRestResponse.getUuid()).collect(Collectors.toList());

            Page<WorkspaceUser> workspaceUserPage = this.workspaceUserRepository.findByWorkspace_UuidAndUserIdIn(workspaceId, userIdList, newPageable);
            List<WorkspaceUser> resultWorkspaceUser = workspaceUserPage.toList();

            userInfoListRestResponse.getUserInfoList().stream().forEach(userInfoRestResponse -> {
                WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, userInfoRestResponse.getUuid());
                if (resultWorkspaceUser.contains(workspaceUserPermission.getWorkspaceUser())) {
                    MemberInfoDTO memberInfoDTO = this.modelMapper.map(userInfoRestResponse, MemberInfoDTO.class);
                    memberInfoDTO.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
                    memberInfoDTO.setJoinDate(workspaceUserPermission.getWorkspaceUser().getCreatedDate());
                    SubProcessCountResponse subProcessCountResponse = this.processRestService.getSubProcessCount(userInfoRestResponse.getUuid()).getData();
                    memberInfoDTO.setCountAssigned(subProcessCountResponse.getCountAssigned());
                    memberInfoDTO.setCountProgressing(subProcessCountResponse.getCountProgressing());
                    memberInfoDTOList.add(memberInfoDTO);
                }
            });
            pageMetadataResponse.setTotalElements(workspaceUserPage.getTotalElements());
            pageMetadataResponse.setTotalPage(workspaceUserPage.getTotalPages());
            pageMetadataResponse.setCurrentPage(pageRequest.of().getPageNumber() + 1);
            pageMetadataResponse.setCurrentSize(pageRequest.of().getPageSize());
        }
        List<MemberInfoDTO> resultMemberListResponse = new ArrayList<>();

        //sort처리
        if (pageRequest.of().getSort() != null) {
            resultMemberListResponse = getSortedMemberList(pageRequest,memberInfoDTOList);
        } else {
            resultMemberListResponse = memberInfoDTOList;
        }

        return new ApiResponse<>(new MemberListResponse(resultMemberListResponse, pageMetadataResponse));
    }

    public List<MemberInfoDTO> getSortedMemberList(com.virnect.workspace.global.common.PageRequest pageRequest,List<MemberInfoDTO> memberInfoDTOList) {

        String sortName = pageRequest.of().getSort().toString().split(":")[0].trim();//sort의 기준이 될 열
        String sortDirection = pageRequest.of().getSort().toString().split(":")[1].trim();//sort의 방향 : 내림차순 or 오름차순

        if (sortName.equalsIgnoreCase("role") && sortDirection.equalsIgnoreCase("asc")) {
            return Sort.ROLE_ASC.sorting(memberInfoDTOList);
        }
        if (sortName.equalsIgnoreCase("role") && sortDirection.equalsIgnoreCase("desc")) {
            return Sort.ROLE_DESC.sorting(memberInfoDTOList);
        }
        if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("asc")) {
            return  Sort.EMAIL_ASC.sorting(memberInfoDTOList);
        }
        if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("desc")) {
            return Sort.EMAIL_DESC.sorting(memberInfoDTOList);
        }
        if (sortName.equalsIgnoreCase("joinDate") && sortDirection.equalsIgnoreCase("asc")) {
            return Sort.JOINDATE_ASC.sorting(memberInfoDTOList);
        }
        if (sortName.equalsIgnoreCase("joinDate") && sortDirection.equalsIgnoreCase("desc")) {
            return Sort.JOINDATE_DESC.sorting(memberInfoDTOList);
        }
        if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("asc")) {
            return Sort.NICKNAME_ASC.sorting(memberInfoDTOList);
        }
        if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("desc")) {
            return Sort.NICKNAME_DESC.sorting(memberInfoDTOList);
        }
        return null;
    }

/*
    public MemberListResponse filterdMemberList(Workspace workspace, String userId, String search, String filter, Pageable pageable) {
        //필터로 넘어온 권한을 리스트로 변환.
        List<WorkspaceRole> workspaceRoleList = new ArrayList<>();
        if (filter.contains("MASTER")) {
            workspaceRoleList.add(WorkspaceRole.builder().id(1L).build());
        }
        if (filter.contains("MANAGER")) {
            workspaceRoleList.add(WorkspaceRole.builder().id(2L).build());
        }
        if (filter.contains("MEMBER")) {
            workspaceRoleList.add(WorkspaceRole.builder().id(3L).build());
        }

        UserInfoListRestResponse userInfoListResponse = this.userRestService.getUserInfoListUserIdAndSearchKeyword(userId, search, false, pageable).getData();

        List<MemberInfoDTO> resultMemberInfoList = new ArrayList<>();
        List<WorkspaceUser> workspaceUserList = new ArrayList<>();

        userInfoListResponse.getUserInfoList().stream().forEach(userInfoRestResponse -> {
            WorkspaceUser workspaceUser = this.workspaceUserRepository.findByUserIdAndWorkspace(userInfoRestResponse.getUuid(), workspace);
            WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser);

            if (workspaceUser != null && workspaceUserPermission != null && filter.contains(workspaceUserPermission.getWorkspaceRole().getRole())) {
                MemberInfoDTO memberInfo = this.modelMapper.map(userInfoRestResponse, MemberInfoDTO.class);
                memberInfo.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
               *//* SubProcessCountResponse subProcessCountResponse = this.processRestService.getSubProcessCount(userInfoRestResponse.getUuid()).getData();
                memberInfo.setCountAssigned(subProcessCountResponse.getCountAssigned());
                memberInfo.setCountProgressing(subProcessCountResponse.getCountProgressing());*//*
                memberInfo.setJoinDate(workspaceUser.getCreatedDate());
                resultMemberInfoList.add(memberInfo);
            }

            workspaceUserList.add(workspaceUser);
        });

        PageRequest newPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<WorkspaceUserPermission> permissionPage =
                this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUserIsInAndWorkspaceRoleIsIn(workspace, workspaceUserList, workspaceRoleList, newPageable);

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(permissionPage.getTotalElements());
        pageMetadataResponse.setTotalPage(permissionPage.getTotalPages());
        pageMetadataResponse.setCurrentPage(pageable.getPageNumber() + 1);
        pageMetadataResponse.setCurrentSize(pageable.getPageSize());

        return new MemberListResponse(resultMemberInfoList, pageMetadataResponse);
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

                WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser);
                WorkspaceRole workspaceRole = workspaceUserPermission.getWorkspaceRole();
                memberInfo.setRole(workspaceRole.getRole());
                *//*SubProcessCountResponse subProcessCountResponse = this.processRestService.getSubProcessCount(userInfoRestResponse.getUuid()).getData();
                memberInfo.setCountAssigned(subProcessCountResponse.getCountAssigned());
                memberInfo.setCountProgressing(subProcessCountResponse.getCountProgressing());*//*
                memberInfo.setJoinDate(workspaceUser.getCreatedDate());

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
    */

    /**
     * 워크스페이스 정보 조회
     *
     * @param workspaceId - 워크스페이스 uuid
     * @param userId      - 사용자 uuid
     * @return - 워크스페이스 정보
     */
    public ApiResponse<WorkspaceInfoResponse> getWorkspaceInfo(String workspaceId) {
        //workspace 정보 set
        Workspace workspace = this.workspaceRepository.findByUuid(workspaceId);
        WorkspaceInfoDTO workspaceInfo = modelMapper.map(workspace, WorkspaceInfoDTO.class);
        workspaceInfo.setMasterUserId(workspace.getUserId());

        //user 정보 set
        List<WorkspaceUser> workspaceUserList = this.workspaceUserRepository.findByWorkspace_Uuid(workspaceId);
        List<UserInfoDTO> userInfoList = new ArrayList<>();
        workspaceUserList.stream().forEach(workspaceUser -> {
            UserInfoRestResponse userInfoRestResponse = this.userRestService.getUserInfoByUserId(workspaceUser.getUserId()).getData();
            UserInfoDTO userInfoDTO = modelMapper.map(userInfoRestResponse, UserInfoDTO.class);
            userInfoList.add(userInfoDTO);
            WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser);
            userInfoDTO.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
        });

        //role 정보 set
        long masterUserCount = this.workspaceUserPermissionRepository.countByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MASTER");
        long managerUserCount = this.workspaceUserPermissionRepository.countByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");
        long memberUserCount = this.workspaceUserPermissionRepository.countByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MEMBER");

        return new ApiResponse<>(new WorkspaceInfoResponse(workspaceInfo, userInfoList, masterUserCount, managerUserCount, memberUserCount));
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
    public ApiResponse<Boolean> inviteWorkspace(String workspaceId, WorkspaceInviteRequest workspaceInviteRequest) {
        //1. 요청한 사람이 마스터유저 또는 매니저유저인지 체크
        Workspace workspace = this.workspaceRepository.findByUuid(workspaceId);
        WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, workspaceInviteRequest.getUserId());
        if (workspaceUserPermission.getWorkspaceRole().getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //TODO: 라이선스 체크 - 최대 멤버 수(9명) 체크하기

        //2. 계정 유효성 체크(user 서비스)
        List<String> emailList = new ArrayList<>();
        workspaceInviteRequest.getUserInfoList().stream().forEach(userInfo -> emailList.add(userInfo.getEmail()));
        InviteUserInfoRestResponse inviteUserInfoRestResponse = this.userRestService.getUserInfoByEmailList(emailList.stream().toArray(String[]::new)).getData();

        //2-1. 유효하지 않은 이메일을 가진 사용자가 포함되어 있는 경우.
        if (emailList.size() != inviteUserInfoRestResponse.getInviteUserInfoList().size()) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
            // TODO: 서브유저는 아닌지 체크하기
        }

        String inviteCode = RandomStringTokenUtil.generate(UUIDType.INVITE_CODE, 6);

        //2-2. 이미 이 워크스페이스에 소속되어 있는 경우
        inviteUserInfoRestResponse.getInviteUserInfoList().stream().forEach(inviteUserResponse -> {
            if (this.workspaceUserRepository.findByUserIdAndWorkspace(inviteUserResponse.getUserUUID(), workspace) != null) {
                throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
            }
            //3. redis 초대데이터 set
            //Redis data set
            workspaceInviteRequest.getUserInfoList().stream().forEach(userInfo -> {
                if (inviteUserResponse.getEmail().equals(userInfo.getEmail())) {
                    UserInvite userInvite = UserInvite.builder()
                            .joinUserId(inviteUserResponse.getUserUUID())
                            .inviteUserId(workspaceInviteRequest.getUserId())
                            .workspaceId(workspaceId)
                            .name(inviteUserResponse.getName())
                            .email(inviteUserResponse.getEmail())
                            .code(inviteCode)
                            .role(userInfo.getRole())
                            .makeType(userInfo.getMakeType())
                            .viewType(userInfo.getViewType())
                            .expireTime(3L)
                            .build();
                    this.userInviteRepository.save(userInvite);
                }
            });
        });


        //5. 초대 메일 전송(마스터 닉네임, 마스터 계정, 멤버 닉네임, 멤버 계정)
        WorkspaceInviteMailRequest.InviteInfo inviteInfo = new WorkspaceInviteMailRequest.InviteInfo();
        List<WorkspaceInviteMailRequest.InviteInfo> inviteInfoList = new ArrayList<>();

        //String acceptUrl = serverUrl + "/" + workspaceId + "/invite/accept";
        String acceptUrl = "/" + workspaceId + "/invite/accept";
        WorkspaceInviteMailRequest workspaceInviteMailRequest = new WorkspaceInviteMailRequest();
        workspaceInviteMailRequest.setAcceptUrl(acceptUrl);
        //workspaceInviteMailRequest.setInviteCode(inviteCode);
        workspaceInviteMailRequest.setInviteInfos(inviteInfoList);
        // workspaceInviteMailRequest.setRequestUserId(workspaceInviteRequest.getUserId());
        //workspaceInviteMailRequest.setRequestUserName("초대한사람");

        // WorkspaceInviteRestResponse workspaceInvite = this.messageRestService.sendMail(workspaceInviteMailRequest).getData();

        return new ApiResponse<>(true);
    }


    /**
     * 워크스페이스 초대 수락(소속 권한 부여)
     *
     * @param workspaceId - 수락한 워크스페이스 uuid
     * @param userId      - 수락한 사용자 uuid
     * @param code        - 초대 시 받은 코드
     * @return
     */
    public ApiResponse<Boolean> inviteWorkspaceAccept(String workspaceId, String
            userId, String code) {
        //1. redis에서 초대 정보 확인
        UserInvite userInvite = this.userInviteRepository.findByJoinUserIdAndAndCode(userId, code);
        if (userInvite == null) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //2. 워크스페이스 소속 넣기 (workspace_user)
        WorkspaceUser workspaceUser = setWorkspaceUserInfo(workspaceId, userId);

        //3. 워크스페이스 권한 부여하기 (workspace_user_permission)
        WorkspaceRole workspaceRole = this.workspaceRoleRepository.findByRole(userInvite.getRole().toUpperCase());

        WorkspaceUserPermission workspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceUser(workspaceUser)
                .workspaceRole(workspaceRole)
                .build();
        this.workspaceUserPermissionRepository.save(workspaceUserPermission);

        //4. TODO: 라이선스 플랜 부여하기

        return new ApiResponse<>(true);
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
        if (this.workspaceUserPermissionRepository.findWorkspaceUserRole(workspaceId, userId).getRole().equals("MEMBER")) {
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

    public ApiResponse<Boolean> reviseMemberInfo(String workspaceId, MemberUpdateRequest memberUpdateRequest) {
        //1. 요청자 권한 확인(마스터, 매니저 권한만 가능)
        Workspace workspace = workspaceRepository.findByUuid(workspaceId);
        WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberUpdateRequest.getMasterUserId());
        String role = workspaceUserPermission.getWorkspaceRole().getRole();
        if (role == null || role.equalsIgnoreCase("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //2. 대상자 권한 확인(매니저, 멤버 권한만 가능)
        WorkspaceUserPermission userPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberUpdateRequest.getUuid());
        String userRole = userPermission.getWorkspaceRole().getRole();
        if (userRole == null || userRole.equalsIgnoreCase("MASTER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //2. 권한 변경
        WorkspaceRole workspaceRole = this.workspaceRoleRepository.findByRole(memberUpdateRequest.getRole().toUpperCase());
        userPermission.setWorkspaceRole(workspaceRole);

        this.workspaceUserPermissionRepository.save(userPermission);

        return new ApiResponse<>(true);
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

    public ApiResponse<List<WorkspaceNewMemberInfoDTO>> getWorkspaceNewUserInfo(String workspaceId) {

        List<WorkspaceUser> workspaceUserList = this.workspaceUserRepository.findTop4ByWorkspace_UuidOrderByCreatedDateDesc(workspaceId);//최신 4명만 가져와서

        List<WorkspaceNewMemberInfoDTO> workspaceNewMemberInfoList = new ArrayList<>();
        workspaceUserList.stream().forEach(workspaceUser -> {
            UserInfoRestResponse userInfoRestResponse = userRestService.getUserInfoByUserId(workspaceUser.getUserId()).getData();
            WorkspaceNewMemberInfoDTO newMemberInfo = modelMapper.map(userInfoRestResponse, WorkspaceNewMemberInfoDTO.class);
            newMemberInfo.setJoinDate(workspaceUser.getCreatedDate());
            newMemberInfo.setRole(this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser).getWorkspaceRole().getRole());
            workspaceNewMemberInfoList.add(newMemberInfo);
        });
        return new ApiResponse<>(workspaceNewMemberInfoList);
    }

    public ApiResponse<WorkspaceInfoDTO> setWorkspace(WorkspaceUpdateRequest workspaceUpdateRequest) {
        if (!StringUtils.hasText(workspaceUpdateRequest.getUserId()) || !StringUtils.hasText(workspaceUpdateRequest.getName())
                || !StringUtils.hasText(workspaceUpdateRequest.getDescription()) || !StringUtils.hasText(workspaceUpdateRequest.getWorkspaceId())) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }

        //마스터 유저 체크
        Workspace workspace = this.workspaceRepository.findByUuid(workspaceUpdateRequest.getWorkspaceId());
        if (!workspace.getUserId().equals(workspaceUpdateRequest.getUserId())) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        workspace.setName(workspaceUpdateRequest.getName());
        workspace.setDescription(workspaceUpdateRequest.getDescription());

        String profile = workspace.getProfile();
        if (workspaceUpdateRequest.getProfile() != null) {
            //기존꺼 삭제
            if (StringUtils.hasText(profile)) {
                this.fileUploadService.delete(profile.substring(profile.lastIndexOf("/") + 1));
            }
            try {
                profile = this.fileUploadService.upload(workspaceUpdateRequest.getProfile());
            } catch (Exception e) {
                throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
            }
        }
        workspace.setProfile(profile);

        this.workspaceRepository.save(workspace);

        WorkspaceInfoDTO workspaceInfoDTO = modelMapper.map(workspace, WorkspaceInfoDTO.class);
        workspaceInfoDTO.setMasterUserId(workspace.getUserId());

        return new ApiResponse<>(workspaceInfoDTO);
    }

    public ApiResponse<UserInfoDTO> getMemberInfo(String workspaceId, String userId) {
        Workspace workspace = this.workspaceRepository.findByUuid(workspaceId);
        WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, userId);
        UserInfoRestResponse userInfoRestResponse = this.userRestService.getUserInfoByUserId(userId).getData();

        UserInfoDTO userInfoDTO = modelMapper.map(userInfoRestResponse, UserInfoDTO.class);
        userInfoDTO.setRole(workspaceUserPermission.getWorkspaceRole().getRole());

        return new ApiResponse<>(userInfoDTO);
    }

    public ApiResponse<Boolean> kickOutMember(String workspaceId, MemberKickOutRequest memberKickOutRequest) {
        Workspace workspace = this.workspaceRepository.findByUuid(workspaceId);

        //내쫓는 자의 권한 확인
        WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberKickOutRequest.getUserId());
        if (workspaceUserPermission.getWorkspaceRole().getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //내쫓기는 자의 권한 확인
        WorkspaceUserPermission kickedUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberKickOutRequest.getKickedUserId());
        if (kickedUserPermission.getWorkspaceRole().getRole().equals("MASTER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //라이선스 삭제 처리


        //workspace_user_permission 삭제(history 테이블 기록)
        this.workspaceUserPermissionRepository.delete(kickedUserPermission);

        //workspace_user 삭제(history 테이블 기록)
        this.workspaceUserRepository.delete(kickedUserPermission.getWorkspaceUser());

        return new ApiResponse<>(true);
    }

}

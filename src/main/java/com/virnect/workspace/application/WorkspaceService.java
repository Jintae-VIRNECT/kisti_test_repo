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
import com.virnect.workspace.global.constant.*;
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
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
    private final SpringTemplateEngine springTemplateEngine;


    @Value("${file.upload-path}")
    private String fileUploadPath;

    @Value("${serverUrl}")
    private String serverUrl;

    @Value("${clientUrl}")
    private String clientUrl;

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
                    memberInfoDTO.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
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
                    memberInfoDTO.setRoleId(workspaceUserPermission.getWorkspaceRole().getId());
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
        resultMemberListResponse = getSortedMemberList(pageRequest, memberInfoDTOList);


        return new ApiResponse<>(new MemberListResponse(resultMemberListResponse, pageMetadataResponse));
    }

    public List<MemberInfoDTO> getSortedMemberList(com.virnect.workspace.global.common.PageRequest pageRequest, List<MemberInfoDTO> memberInfoDTOList) {

        String sortName = pageRequest.of().getSort().toString().split(":")[0].trim();//sort의 기준이 될 열
        String sortDirection = pageRequest.of().getSort().toString().split(":")[1].trim();//sort의 방향 : 내림차순 or 오름차순


        if (sortName.equalsIgnoreCase("role") && sortDirection.equalsIgnoreCase("asc")) {
            return memberInfoDTOList.stream().sorted(Comparator.comparing(MemberInfoDTO::getRoleId, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
            //return Sort.ROLE_ASC.sorting(memberInfoDTOList);
        }
        if (sortName.equalsIgnoreCase("role") && sortDirection.equalsIgnoreCase("desc")) {
            return memberInfoDTOList.stream().sorted(Comparator.comparing(MemberInfoDTO::getRoleId, Comparator.nullsFirst(Comparator.reverseOrder()))).collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("asc")) {
            return memberInfoDTOList.stream().sorted(Comparator.comparing(MemberInfoDTO::getEmail, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("email") && sortDirection.equalsIgnoreCase("desc")) {
            return memberInfoDTOList.stream().sorted(Comparator.comparing(MemberInfoDTO::getEmail, Comparator.nullsFirst(Comparator.reverseOrder()))).collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("joinDate") && sortDirection.equalsIgnoreCase("asc")) {
            return memberInfoDTOList.stream().sorted(Comparator.comparing(MemberInfoDTO::getJoinDate, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("joinDate") && sortDirection.equalsIgnoreCase("desc")) {
            return memberInfoDTOList.stream().sorted(Comparator.comparing(MemberInfoDTO::getJoinDate, Comparator.nullsFirst(Comparator.reverseOrder()))).collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("asc")) {
            return memberInfoDTOList.stream().sorted(Comparator.comparing(MemberInfoDTO::getNickName, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
        }
        if (sortName.equalsIgnoreCase("nickname") && sortDirection.equalsIgnoreCase("desc")) {
            return memberInfoDTOList.stream().sorted(Comparator.comparing(MemberInfoDTO::getNickName, Comparator.nullsFirst(Comparator.reverseOrder()))).collect(Collectors.toList());
        } else {
            return memberInfoDTOList.stream().sorted(Comparator.comparing(MemberInfoDTO::getUpdatedDate, Comparator.nullsFirst(Comparator.reverseOrder()))).collect(Collectors.toList());

        }
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
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //TODO: 라이선스 체크 - 최대 멤버 수(9명) 체크하기

        //2. 계정 유효성 체크(user 서비스)
        List<String> emailList = new ArrayList<>();
        workspaceInviteRequest.getUserInfoList().stream().forEach(userInfo -> emailList.add(userInfo.getEmail()));
        InviteUserInfoRestResponse inviteUserInfoRestResponse = this.userRestService.getUserInfoByEmailList(emailList.stream().toArray(String[]::new)).getData();

        // 유효하지 않은 이메일을 가진 사용자가 포함되어 있는 경우.
        if (emailList.size() != inviteUserInfoRestResponse.getInviteUserInfoList().size()) {
            throw new WorkspaceException(ErrorCode.ERR_INVALID_VALUE);
        }
        //서브유저로 등록되어 있는 사용자가 포함되어 있는 경우.
        /*inviteUserInfoRestResponse.getInviteUserInfoList().stream().forEach(inviteUserResponse -> {
            if(inviteUserResponse.getUserType().equals("SUB_USER")){
                throw new WorkspaceException(ErrorCode.ERR_INVALID_VALUE);
            }
        });*/

        //마스터 유저 정보
        UserInfoRestResponse materUser = this.userRestService.getUserInfoByUserId(workspace.getUserId()).getData();

        String inviteCode = RandomStringTokenUtil.generate(UUIDType.INVITE_CODE, 6);

        //메일 form set
        Context context = new Context();
        context.setVariable("workspaceMasterNickName", materUser.getNickname());
        context.setVariable("workspaceMasterEmail", materUser.getEmail());
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workstationHomeUrl", clientUrl);

        Long duration = Duration.ofDays(7).getSeconds();
        inviteUserInfoRestResponse.getInviteUserInfoList().stream().forEach(inviteUserResponse -> {
            //이미 이 워크스페이스에 소속되어 있는 경우
            if (this.workspaceUserRepository.findByUserIdAndWorkspace(inviteUserResponse.getUserUUID(), workspace) != null) {
                throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_USER_ALREADY_EXIST);
            }
            workspaceInviteRequest.getUserInfoList().stream().forEach(userInfo -> {
                //초대받은 사람의 유저의 권한은 매니저 또는 멤버만 가능하도록 체크
                if (userInfo.getRole().equals("MASTER")) {
                    throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
                }
                if (inviteUserResponse.getEmail().equals(userInfo.getEmail())) {
                    //redis 긁어서 이미 초대한 정보 있는지 확인하고, 있으면 시간만 갱신.
                    UserInvite userInvite = this.userInviteRepository.findById(inviteUserResponse.getUserUUID()).orElse(null);
                    if (userInvite != null) {
                        userInvite.setExpireTime(duration);
                        this.userInviteRepository.save(userInvite);
                        log.info("REDIS SET - {}", userInvite.toString());

                    } else {
                        UserInvite newUserInvite = UserInvite.builder()
                                .responseUserId(inviteUserResponse.getUserUUID())
                                .responseUserEmail(inviteUserResponse.getEmail())
                                .responseUserName(inviteUserResponse.getName())
                                .responseUserNickName(inviteUserResponse.getNickName())
                                .requestUserId(materUser.getUuid())
                                .requestUserEmail(materUser.getEmail())
                                .requestUserName(materUser.getName())
                                .requestUserNickName(materUser.getNickname())
                                .workspaceId(workspace.getUuid())
                                .workspaceName(workspace.getName())
                                .code(inviteCode)
                                .role(userInfo.getRole())
                                .makeType(userInfo.getMakeType())
                                .viewType(userInfo.getViewType())
                                .expireTime(duration)
                                .build();

                        this.userInviteRepository.save(newUserInvite);
                        log.info("REDIS SET - {}", newUserInvite.toString());
                    }
                    //메일은 이미 초대한 것 여부와 관계없이 발송한다.
                    String rejectUrl = serverUrl + "/workspaces/" + workspaceId + "/invite/accept?userId=" + inviteUserResponse.getUserUUID() + "&code=reject";
                    String acceptUrl = serverUrl + "/workspaces/" + workspaceId + "/invite/accept?userId=" + inviteUserResponse.getUserUUID() + "&code=" + inviteCode;
                    context.setVariable("rejectUrl", rejectUrl);
                    context.setVariable("acceptUrl", acceptUrl);
                    context.setVariable("responseUserName", inviteUserResponse.getName());
                    context.setVariable("responseUserEmail", inviteUserResponse.getEmail());
                    context.setVariable("responseUserNickName", inviteUserResponse.getNickName());
                    context.setVariable("role", userInfo.getRole());

                    String html = springTemplateEngine.process("workspace_invite", context);
                    List<String> emailReceiverList = new ArrayList<>();
                    emailReceiverList.add(inviteUserResponse.getEmail());
                    this.sendMailRequest(html, emailReceiverList, MailSender.MASTER, MailSubject.WORKSPACE_INVITE);
                }
            });
        });

        return new ApiResponse<>(true);
    }

    /**
     * message 서비스로 메일 발송 요청
     *
     * @param html
     * @param receivers
     * @param mailSender
     * @param mailSubject
     */
    private void sendMailRequest(String html, List<String> receivers, MailSender mailSender, MailSubject mailSubject) {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setHtml(html);
        mailRequest.setReceivers(receivers);
        mailRequest.setSender(mailSender.getSender());
        mailRequest.setSubject(mailSubject.getSubject());
        this.messageRestService.sendMail(mailRequest);
    }

    /**
     * 워크스페이스 초대 수락(소속 권한 부여)
     *
     * @param workspaceId - 수락한 워크스페이스 uuid
     * @param userId      - 수락한 사용자 uuid
     * @param code        - 초대 시 받은 코드
     * @return
     */
    public ApiResponse<Boolean> inviteWorkspaceAccept(String workspaceId, String userId, String code) {
        //REDIS 에서 초대정보 조회
        UserInvite userInvite = this.userInviteRepository.findById(userId).orElse(null);
        if (userInvite == null) {
            throw new WorkspaceException(ErrorCode.ERR_NOT_FOUND_INVITE_WORKSPACE_INFO);
        }
        Workspace workspace = this.workspaceRepository.findByUuid(workspaceId);

        //초대 또는 수락 결과에 대한 메일을 받을 사용자 LIST
        List<String> emailReceiverList = new ArrayList<>();
        UserInfoRestResponse masterUser = this.userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
        emailReceiverList.add(masterUser.getEmail());
        List<WorkspaceUserPermission> workspaceUserPermissionList = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceRole_Role(workspace, "MANAGER");

        if (workspaceUserPermissionList != null) {
            workspaceUserPermissionList.stream().forEach(workspaceUserPermission -> {
                UserInfoRestResponse managerUser = this.userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
                emailReceiverList.add(managerUser.getEmail());
            });
        }

        if (code.equals("reject")) {
            //redis에서 삭제
            this.userInviteRepository.deleteById(userId);

            //MAIL 발송
            Context context = new Context();
            context.setVariable("rejectUserNickname", userInvite.getResponseUserNickName());
            context.setVariable("rejectUserEmail", userInvite.getResponseUserEmail());
            context.setVariable("workspaceName", workspace.getName());

            // String html = springTemplateEngine.process("workspace_invite", context);
            //this.sendMailRequest("<html> aaa </html>", emailReceiverList, MailSender.MASTER, MailSubject.WORKSPACE_INVITE_REJECT);

        } else {
            //초대 코드 대조
            if (!userInvite.getCode().equals(code)) {
                throw new WorkspaceException(ErrorCode.ERR_INCORRECT_INVITE_WORKSPACE_CODE);
            }

            //워크스페이스 소속 넣기 (workspace_user)
            WorkspaceUser workspaceUser = setWorkspaceUserInfo(workspaceId, userId);

            //워크스페이스 권한 부여하기 (workspace_user_permission)
            WorkspaceRole workspaceRole = this.workspaceRoleRepository.findByRole(userInvite.getRole().toUpperCase());

            WorkspaceUserPermission workspaceUserPermission = WorkspaceUserPermission.builder()
                    .workspaceUser(workspaceUser)
                    .workspaceRole(workspaceRole)
                    .build();
            this.workspaceUserPermissionRepository.save(workspaceUserPermission);
            // String html = springTemplateEngine.process("workspace_invite", context);

            //this.sendMailRequest("<html> aaa </html>", emailReceiverList, MailSender.MASTER, MailSubject.WORKSPACE_INVITE_REJECT);

            //TODO: 라이선스 플랜 부여하기

            //MAIL 발송
            Context context = new Context();
            context.setVariable("workspaceName", workspace.getName());
            context.setVariable("workspaceMasterNickName", masterUser.getNickname());
            context.setVariable("workspaceMasterEmail", masterUser.getEmail());
            context.setVariable("acceptUserNickName", userInvite.getResponseUserNickName());
            context.setVariable("acceptUserEmail", userInvite.getResponseUserEmail());
            context.setVariable("role", userInvite.getRole());

            // String html = springTemplateEngine.process("workspace_invite", context);
            //this.sendMailRequest("<html> aaa </html>", emailReceiverList, MailSender.MASTER, MailSubject.WORKSPACE_INVITE_ACCEPT);
            //redis 에서 삭제
            this.userInviteRepository.deleteById(userId);
        }

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
        //1. 요청자 권한 확인(마스터만 가능)
        Workspace workspace = workspaceRepository.findByUuid(workspaceId);
        WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberUpdateRequest.getMasterUserId());
        String role = workspaceUserPermission.getWorkspaceRole().getRole();
        if (role == null || !role.equalsIgnoreCase("MASTER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //2. 대상자 권한 확인(매니저, 멤버 권한만 가능)
        WorkspaceUserPermission userPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberUpdateRequest.getUuid());
        String userRole = userPermission.getWorkspaceRole().getRole();
        if (userRole == null || userRole.equalsIgnoreCase("MASTER")) {
            throw new WorkspaceException(ErrorCode.ERR_WORKSPACE_INVALID_PERMISSION);
        }

        //3. 권한 변경
        WorkspaceRole workspaceRole = this.workspaceRoleRepository.findByRole(memberUpdateRequest.getRole().toUpperCase());
        userPermission.setWorkspaceRole(workspaceRole);

        this.workspaceUserPermissionRepository.save(userPermission);

        //4. 메일 발송
        UserInfoRestResponse masterUser = this.userRestService.getUserInfoByUserId(workspace.getUserId()).getData();
        UserInfoRestResponse user = this.userRestService.getUserInfoByUserId(memberUpdateRequest.getUuid()).getData();
        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", masterUser.getNickname());
        context.setVariable("workspaceMasterEmail", masterUser.getEmail());
        context.setVariable("responseUserNickName", user.getNickname());
        context.setVariable("responseUserEmail", user.getEmail());
        context.setVariable("role", workspaceRole.getRole());

        // String html = springTemplateEngine.process("workspace_invite", context);
        List<String> receiverEmailList = new ArrayList<>();
        receiverEmailList.add(user.getEmail());
        //this.sendMailRequest("<html> aaa </html>", receiverEmailList, MailSender.MASTER, MailSubject.WORKSPACE_INFO_UPDATE);

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
        String oldWorkspaceName = workspace.getName();
        if (!workspace.getUserId().equals(workspaceUpdateRequest.getUserId())) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }
        workspace.setName(workspaceUpdateRequest.getName());
        workspace.setDescription(workspaceUpdateRequest.getDescription());

        if (workspaceUpdateRequest.getProfile() != null) {
            String oldProfile = workspace.getProfile();
            //기존 프로필 이미지 삭제
            if (StringUtils.hasText(oldProfile)) {
                this.fileUploadService.delete(oldProfile.substring(oldProfile.lastIndexOf("/") + 1));
            }
            //새 프로필 이미지 등록
            try {
                workspace.setProfile(this.fileUploadService.upload(workspaceUpdateRequest.getProfile()));
            } catch (Exception e) {
                throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
            }

        }

        this.workspaceRepository.save(workspace);

        WorkspaceInfoDTO workspaceInfoDTO = modelMapper.map(workspace, WorkspaceInfoDTO.class);
        workspaceInfoDTO.setMasterUserId(workspace.getUserId());

        //메일 발송
        List<String> receiverEmailList = new ArrayList<>();
        Context context = new Context();
        context.setVariable("beforeWorkspaceName", oldWorkspaceName);
        context.setVariable("afterWorkspaceName", workspaceUpdateRequest.getName());
        List<WorkspaceUser> workspaceUserList = this.workspaceUserRepository.findByWorkspace_Uuid(workspace.getUuid());
        workspaceUserList.stream().forEach(workspaceUser -> {
            UserInfoRestResponse userInfoRestResponse = this.userRestService.getUserInfoByUserId(workspaceUser.getUserId()).getData();
            receiverEmailList.add(userInfoRestResponse.getEmail());
            if (userInfoRestResponse.getUuid().equals(workspace.getUserId())) {
                context.setVariable("workspaceMasterNickName", userInfoRestResponse.getNickname());

            }
        });

        // String html = springTemplateEngine.process("workspace_invite", context);
        // this.sendMailRequest("<html> aaa </html>", receiverEmailList, MailSender.MASTER, MailSubject.WORKSPACE_INFO_REVISE);

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
        UserInfoRestResponse userInfoRestResponse = this.userRestService.getUserInfoByUserId(workspace.getUserId()).getData();

        //내쫓는 자의 권한 확인
        WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberKickOutRequest.getUserId());
        if (workspaceUserPermission.getWorkspaceRole().getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //내쫓기는 자의 권한 확인
        WorkspaceUserPermission kickedUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser_WorkspaceAndWorkspaceUser_UserId(workspace, memberKickOutRequest.getKickedUserId());
        if (!kickedUserPermission.getWorkspaceRole().getRole().equals("MEMBER")) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        //라이선스 삭제 처리

        //workspace_user_permission 삭제(history 테이블 기록)
        this.workspaceUserPermissionRepository.delete(kickedUserPermission);

        //workspace_user 삭제(history 테이블 기록)
        this.workspaceUserRepository.delete(kickedUserPermission.getWorkspaceUser());

        //메일 발송
        Context context = new Context();
        context.setVariable("workspaceName", workspace.getName());
        context.setVariable("workspaceMasterNickName", userInfoRestResponse.getNickname());
        context.setVariable("workspaceMasterEmail", userInfoRestResponse.getEmail());

        UserInfoRestResponse kickedUser = this.userRestService.getUserInfoByUserId(memberKickOutRequest.getUserId()).getData();

        List<String> receiverEmailList = new ArrayList<>();
        receiverEmailList.add(kickedUser.getEmail());

        // String html = springTemplateEngine.process("workspace_invite", context);
        //this.sendMailRequest("<html> aaa </html>", receiverEmailList, MailSender.MASTER, MailSubject.WORKSPACE_KICKOUT);

        return new ApiResponse<>(true);
    }

}

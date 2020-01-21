package com.virnect.workspace.application;

import com.virnect.workspace.dao.*;
import com.virnect.workspace.domain.Workspace;
import com.virnect.workspace.domain.WorkspaceUser;
import com.virnect.workspace.domain.WorkspaceUserPermission;
import com.virnect.workspace.dto.UserDTO;
import com.virnect.workspace.dto.WorkspaceDTO;
import com.virnect.workspace.exception.SomeException;
import com.virnect.workspace.global.common.ResponseMessage;
import com.virnect.workspace.global.constant.UUIDType;
import com.virnect.workspace.global.util.RandomStringTokenUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    public ResponseMessage createWorkspace(WorkspaceDTO.WorkspaceInfo workspaceInfo) {
        //이메일 계정이 있는 사용자 인지 체크 : 마스터가 만들어낸 계정은 워크스페이스를 생성할 수 없다. -> UserServer 에 요청
        ResponseMessage userServerResponse = this.userRestService.getUserInfoByUserId(workspaceInfo.getUserId());
        log.info("data: {}", userServerResponse.getData());
        UserDTO.UserInfoDTO requestUserInfo = modelMapper.map(userServerResponse.getData().get("userInfo"), UserDTO.UserInfoDTO.class);
        if (requestUserInfo.getUserType().equals("SUB_USER")) {
            throw new SomeException();
        }

        //워크스페이스 생성 & 워크스페이스 자동 소속 & 워크스페이스 권한 마스터 등록
        String uuid = RandomStringTokenUtil.generate(UUIDType.UUID_WITH_SEQUENCE, 0);
        String pinNumber = RandomStringTokenUtil.generate(UUIDType.PIN_NUMBER, 0);

        Workspace newWorkspace = Workspace.builder()
                .uuid(uuid)
                .userId(workspaceInfo.getUserId())
                .description(workspaceInfo.getDescription())
                .pinNumber(pinNumber)
                .build();

        this.workspaceRepository.save(newWorkspace);

        WorkspaceUser newWorkspaceUser = WorkspaceUser.builder()
                .userId(workspaceInfo.getUserId())
                .workspace(newWorkspace)
                .build();
        this.workspaceUserRepository.save(newWorkspaceUser);

        WorkspaceUserPermission newWorkspaceUserPermission = WorkspaceUserPermission.builder()
                .workspaceRole(workspaceRoleRepository.findById(1L).get())
                .workspacePermission(workspacePermissionRepository.findById(1L).get())
                .workspaceUser(newWorkspaceUser)
                .build();
        this.workspaceUserPermissionRepository.save(newWorkspaceUserPermission);

        return new ResponseMessage().addParam("workspace", newWorkspace);
    }

    public ResponseMessage getUserWorkspaces(String userId) {
        List<WorkspaceDTO.UserWorkspaceInfo> userWorkspaceList = new ArrayList<>();
        this.workspaceUserRepository.findByUserId(userId).forEach(workspaceUser -> {
            Workspace workspace = workspaceUser.getWorkspace();
            WorkspaceDTO.UserWorkspaceInfo userWorkspaceInfo = modelMapper.map(workspace, WorkspaceDTO.UserWorkspaceInfo.class);
            WorkspaceUserPermission workspaceUserPermission = this.workspaceUserPermissionRepository.findByWorkspaceUser(workspaceUser);
            userWorkspaceInfo.setRole(workspaceUserPermission.getWorkspaceRole().getRole());
            userWorkspaceList.add(userWorkspaceInfo);
        });

        return new ResponseMessage().addParam("workspaceList", userWorkspaceList);
    }

    public ResponseMessage getMember(String workspaceUUID, String userId, String search, String filter, String align) {

        ResponseMessage responseMessage = this.userRestService.getUserInfoListUserIdAndSearchKeyword(userId, search);
        Map<String, Object> data = responseMessage.getData();
        log.info("ResponseMessage: {}", data);
        List<Object> results = ((List<Object>) data.get("userInfoList"));
        List<UserDTO.UserInfoDTO> userInfoDTOList = results.stream().map(object -> modelMapper.map(object, UserDTO.UserInfoDTO.class)).collect(Collectors.toList());
        List<UserDTO.UserInfoDTO> result = new ArrayList<>();

        //2. 필터 검증
        if (filter.equals("MASTER")) {
            for (UserDTO.UserInfoDTO userInfo : userInfoDTOList) {
                if (this.hasRoleIs(userInfo.getUuid(), workspaceUUID, "MASTER")) {
                    result.add(userInfo);
                }
            }
        } else {
            result = userInfoDTOList;
        }

        //3. 정렬 검증(기본값은 ㄱ~ㅎ)
        if (align.equals("asc") || !StringUtils.hasText(align)) {
            result.sort(Comparator.comparing(UserDTO.UserInfoDTO::getName));
        } else {
            result.sort((first, second) -> second.getName().compareTo(first.getName()));
        }

        return new ResponseMessage().addParam("memberList", result);
    }

    /**
     * 워크스페이스 유저 역할 검사
     *
     * @param userId      - 유저 고유 아이디
     * @param workspaceUUID - 워크스페이스 고유 아이디
     * @param role        - 비교 대상 역할
     * @return 역할 비교 결과
     */
    private boolean hasRoleIs(final String userId, final String workspaceUUID, final String role) {
        Workspace workspace = this.workspaceRepository.findByUuid(workspaceUUID);
        WorkspaceUser workspaceUser = this.workspaceUserRepository.findByUserIdAndWorkspace(userId, workspace);
        return this.workspaceUserPermissionRepository.findWorkspaceUserRole(workspaceUser).getRole().equals(role);
    }

}


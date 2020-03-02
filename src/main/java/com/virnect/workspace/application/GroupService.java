package com.virnect.workspace.application;

import com.virnect.workspace.dao.GroupRepository;
import com.virnect.workspace.dao.group.GroupRoleRepository;
import com.virnect.workspace.dao.group.GroupUserPermissionRepository;
import com.virnect.workspace.dao.group.GroupUserRepository;
import com.virnect.workspace.domain.Group;
import com.virnect.workspace.domain.GroupUser;
import com.virnect.workspace.domain.GroupUserPermission;
import com.virnect.workspace.domain.WorkspaceUser;
import com.virnect.workspace.dto.GroupInfoDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-01-30
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupService {
    private final GroupUserRepository groupUserRepository;
    private final GroupRepository groupRepository;
    private final ModelMapper modelMapper;
    private final GroupUserPermissionRepository groupUserPermissionRepository;
    private final GroupRoleRepository groupRoleRepository;

    public void setGroupUser(List<GroupInfoDTO> groups, WorkspaceUser workspaceUser) {
        for (GroupInfoDTO groupInfo : groups) {
            //1. group_user insert
            Group group = this.groupRepository.findByName(groupInfo.getGroupName());
            GroupUser groupUser = GroupUser.builder().group(group).workspaceUser(workspaceUser).build();
            this.groupUserRepository.save(groupUser);

            //2. group_user_permission insert
            if(groupInfo.getManagerAssign()) {
                GroupUserPermission groupUserPermission = GroupUserPermission.builder()
                        .groupRole(this.groupRoleRepository.findByRole("MANAGER"))
                        .groupUser(groupUser)
                        .build();
                this.groupUserPermissionRepository.save(groupUserPermission);
            }else {
                GroupUserPermission groupUserPermission = GroupUserPermission.builder()
                        .groupRole(this.groupRoleRepository.findByRole("MEMBER"))
                        .groupUser(groupUser)
                        .build();
                this.groupUserPermissionRepository.save(groupUserPermission);
            }

        }


    }

}

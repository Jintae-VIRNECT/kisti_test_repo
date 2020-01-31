package com.virnect.workspace.application;

import com.virnect.workspace.dao.GroupRepository;
import com.virnect.workspace.dao.group.GroupRoleRepository;
import com.virnect.workspace.dao.group.GroupUserPermissionRepository;
import com.virnect.workspace.dao.group.GroupUserRepository;
import com.virnect.workspace.domain.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public void setGroupUsers(List<Map<String, String>> groups, WorkspaceUser workspaceUser) {
        for(Map<String, String> groupMap : groups){
            //1. group_user insert
            for(Map.Entry<String, String> groupKeyValue : groupMap.entrySet()){
                Group group = this.groupRepository.findByName(groupKeyValue.getKey());
                GroupUser groupUser = GroupUser.builder().group(group).workspaceUser(workspaceUser).build();
                this.groupUserRepository.save(groupUser);

                //2. group_user_permission insert
                if(groupKeyValue.getValue().equals("y")){
                    GroupUserPermission groupUserPermission = GroupUserPermission.builder()
                            .groupRole(this.groupRoleRepository.findByRole("MANAGER"))
                            .groupUser(groupUser)
                            .build();
                    this.groupUserPermissionRepository.save(groupUserPermission);
                }else{
                    GroupUserPermission groupUserPermission = GroupUserPermission.builder()
                            .groupRole(this.groupRoleRepository.findByRole("MEMBER"))
                            .groupUser(groupUser)
                            .build();
                    this.groupUserPermissionRepository.save(groupUserPermission);
                }
            }

        }
    }
}

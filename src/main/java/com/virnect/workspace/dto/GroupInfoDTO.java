package com.virnect.workspace.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2020-03-02
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class GroupInfoDTO {
    private String groupName;
    private Boolean managerAssign;
    private List<Long> groupPermissions;
}

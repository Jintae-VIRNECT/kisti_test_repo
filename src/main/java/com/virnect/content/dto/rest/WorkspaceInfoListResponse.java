package com.virnect.content.dto.rest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.29
 */

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceInfoListResponse {
    private List<WorkspaceInfoResponse> workspaceList;

    public static List<WorkspaceInfoResponse> EMPTY = new ArrayList<>();
}

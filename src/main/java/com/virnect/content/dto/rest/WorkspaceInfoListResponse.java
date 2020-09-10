package com.virnect.content.dto.rest;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	public static List<WorkspaceInfoResponse> EMPTY = new ArrayList<>();
	private List<WorkspaceInfoResponse> workspaceList;
}

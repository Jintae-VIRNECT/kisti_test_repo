package com.virnect.content.dto.response;

import com.virnect.content.global.common.PageMetadataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-02-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@RequiredArgsConstructor
public class WorkspaceSceneGroupListResponse {
    @ApiModelProperty(value = "씬그룹 정보 리스트", notes = "각 씬그룹들의 정보들이 담겨있는 배열입니다.")
    private final List<SceneGroupInfoResponse> sceneGroupInfoList;
    @ApiModelProperty(value = "페이징 정보", notes = "페이지네이션을 하기 위해 필요한 정보입니다.", position = 1)
    private final PageMetadataResponse pageMeta;
}

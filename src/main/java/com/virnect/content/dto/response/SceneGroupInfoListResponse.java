package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModelProperty;
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
public class SceneGroupInfoListResponse {
    @ApiModelProperty(value = "씬그룹들이 속한 컨텐츠의 식별자", notes = "각 컨텐츠 씬그룹들이 등록되어있는 컨텐츠의 식별자 입니다.", example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private final String contentUUID;
    @ApiModelProperty(value = "씬그룹 정보 리스트", notes = "각 씬그룹들의 정보들이 담겨있는 배열입니다.")
    private final List<SceneGroupInfoResponse> sceneGroupInfoList;
}

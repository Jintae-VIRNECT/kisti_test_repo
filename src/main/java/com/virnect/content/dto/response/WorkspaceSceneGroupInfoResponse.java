package com.virnect.content.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-02-17
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@NoArgsConstructor
public class WorkspaceSceneGroupInfoResponse {
    @ApiModelProperty(value = "씬그룹(세부공정) 정보 식별자", notes = "컨텐츠에서 씬그룹 정보를 구별하는데 사용됩니다.", example = "061cc38d-1c45-435b-bf56-4d164fcb5d29")
    private String id;
    @ApiModelProperty(value = "씬그룹(세부공정) 우선 순위", notes = "컨텐츠내에서의 씬그룹의 우선순위 정보입니다.", position = 1, example = "1")
    private int priority;
    @ApiModelProperty(value = "씬그룹(세부공정) 명", notes = "씬그룹의 제목입니다.", position = 2, example = "자재 절단")
    private String name;
    @ApiModelProperty(value = "씬(작업)들의 갯수", notes = "씬그룹에 들어있는 씬들의 갯수입니다.", position = 3, example = "5")
    private int jobTotal;
    @ApiModelProperty(value = "해당 씬그룹 컨텐츠 정보", notes = "씬그룹이 속해 있는 컨텐츠의 정보.", position = 4)
    private ContentInfoResponse content;

    @Builder
    public WorkspaceSceneGroupInfoResponse(String id, int priority, String name, int jobTotal, ContentInfoResponse content) {
        this.id = id;
        this.priority = priority;
        this.name = name;
        this.jobTotal = jobTotal;
        this.content = content;
    }
}

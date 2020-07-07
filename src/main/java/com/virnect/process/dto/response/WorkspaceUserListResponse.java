package com.virnect.process.dto.response;

import com.virnect.process.global.common.PageMetadataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class WorkspaceUserListResponse {
    @ApiModelProperty(value = "워크스페이스 사용자 목록", notes = "작업자의 식별자", example = "449ae69cee53b8a6819053828c94e496")
    private final List<WorkspaceUserInfoResponse> infos;

    @ApiModelProperty(value = "페이지 정보", position = 1, notes = "pangenation 정보")
    private final PageMetadataResponse pageMeta;
}

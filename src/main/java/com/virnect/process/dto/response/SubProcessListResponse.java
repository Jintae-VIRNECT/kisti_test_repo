package com.virnect.process.dto.response;

import com.virnect.process.global.common.PageMetadataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SubProcessListResponse {
    @ApiModelProperty(value = "공정 식별자", notes = "조회한 세부공정의 공정 식별자")
    private final long processId;

    @ApiModelProperty(value = "공정명", notes = "조회한 세부공정의 공정명", position = 1)
    private final String processName;

    @ApiModelProperty(value = "세부공정 목록", notes = "조회한 세부공정의 배열", position = 2)
    private final List<EditSubProcessResponse> subProcesses;

    @ApiModelProperty(value = "페이지 정보", notes = "pangenation 정보", position = 3)
    private final PageMetadataResponse pageMeta;

    // TODO 01 : 이슈 여부에 대한 파라미터 필요.
}

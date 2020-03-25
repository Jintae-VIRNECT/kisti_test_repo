package com.virnect.process.dto.response;

import com.virnect.process.global.common.PageMetadataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class JobListResponse {
    @ApiModelProperty(value = "공정 식별자", notes = "조회한 세부공정의 공정 식별자", example = "2")
    private final long processId;

    @ApiModelProperty(value = "공정명", notes = "조회한 세부공정의 공정명", position = 1, example = "엔진 조립 공정")
    private final String processName;

    @ApiModelProperty(value = "공정 식별자", notes = "조회한 세부공정의 공정 식별자", position = 2, example = "1")
    private final long subProcessId;

    @ApiModelProperty(value = "공정명", notes = "조회한 세부공정의 공정명", position = 3, example = "하부 조립 공정")
    private final String subProcessName;

    @ApiModelProperty(value = "세부공정 목록", notes = "조회한 세부공정의 배열", position = 4)
    private final List<JobResponse> Jobs;

    @ApiModelProperty(value = "페이지 정보", notes = "pangenation 정보", position = 5)
    private final PageMetadataResponse pageMeta;

    @Builder
    public JobListResponse(Long processId, String processName, Long subProcessId, String subProcessName, List<JobResponse> jobs, PageMetadataResponse pageMeta) {
        this.processId = processId;
        this.processName = processName;
        this.subProcessId = subProcessId;
        this.subProcessName = subProcessName;
        Jobs = jobs;
        this.pageMeta = pageMeta;
    }
}

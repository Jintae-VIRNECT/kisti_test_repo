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
    @ApiModelProperty(value = "작업 식별자", notes = "조회한 세부 작업의 작업 식별자", example = "2")
    private final long taskId;

    @ApiModelProperty(value = "작업명", notes = "조회한 세부 작업의 작업명", position = 1, example = "엔진 조립 작업")
    private final String taskName;

    @ApiModelProperty(value = "세부 작업 식별자", notes = "조회한 세부 작업의 작업 식별자", position = 2, example = "1")
    private final long subTaskId;

    @ApiModelProperty(value = "작업명", notes = "조회한 세부 작업의 작업명", position = 3, example = "하부 조립 작업")
    private final String subTaskName;

    @ApiModelProperty(value = "단계 목록", notes = "조회한 세부 작업의 단계 목록", position = 4)
    private final List<JobResponse> steps;

    @ApiModelProperty(value = "페이지 정보", notes = "pangenation 정보", position = 5)
    private final PageMetadataResponse pageMeta;

    @Builder
    public JobListResponse(Long taskId, String taskName, Long subTaskId, String subTaskName, List<JobResponse> steps, PageMetadataResponse pageMeta) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.subTaskId = subTaskId;
        this.subTaskName = subTaskName;
        this.steps = steps;
        this.pageMeta = pageMeta;
    }
}

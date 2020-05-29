package com.virnect.process.dto.response;

import com.virnect.process.global.common.PageMetadataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class SubProcessesOfTargetResponse {

    @NotBlank
    @ApiModelProperty(value = "공정식별자", notes = "공정식별자(UUID)", example = "1")
    private final long taskId;

    @NotBlank
    @ApiModelProperty(value = "공정명", notes = "공정명", position = 1, example = "공정이름")
    private final String taskName;

    @NotBlank
    @ApiModelProperty(value = "컨텐츠식별자", notes = "컨텐츠식별자(UUID)", position = 2, example = "48254844-235e-4421-b713-4ea682994a98")
    private final String contentUUID;

    @NotBlank
    @ApiModelProperty(value = "다운로드 경로", notes = "다운로드 API의 서버 주소 및 포트를 제외한 경로", position = 3, example = "/contents/upload/2.Ares")
    private final String downloadPath;


    @ApiModelProperty(value = "공정 목록", notes = "조회한 공정의 배열", position = 4)
    private final List<SubProcessOfTargetResponse> subTasks;

    @ApiModelProperty(value = "페이지 정보", position = 5, notes = "pangenation 정보")
    private final PageMetadataResponse pageMeta;

    @Builder
    public SubProcessesOfTargetResponse(@NotBlank long taskId, @NotBlank String taskName, @NotBlank String contentUUID, @NotBlank String downloadPath, List<SubProcessOfTargetResponse> subTasks, PageMetadataResponse pageMeta) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.contentUUID = contentUUID;
        this.downloadPath = downloadPath;
        this.subTasks = subTasks;
        this.pageMeta = pageMeta;
    }
}

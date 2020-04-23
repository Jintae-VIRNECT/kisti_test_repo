package com.virnect.process.dto.response;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.State;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ProcessInfoResponse {
    @NotBlank
    @ApiModelProperty(value = "공정식별자", notes = "공정식별자", example = "1")
    private long id;

    @NotBlank
    @ApiModelProperty(value = "공정명", notes = "공정명", position = 1, example = "자제 절단 세부공정")
    private String name;

    @NotBlank
    @ApiModelProperty(value = "컨텐츠 고유 식별자", notes = "해당 식별자를 통해 컨텐츠를 구별합니다.", position = 2, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String contentUUID;

    @ApiModelProperty(value = "컨텐츠 관리자(등록자) 식별자", notes = "해당 컨텐츠를 관리하는(등록한) 사용자의 식별자", position = 3, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String contentManagerUUID;

    @ApiModelProperty(value = "위치", notes = "위치 설명", position = 4, example = "우측")
    private String position;

    @ApiModelProperty(value = "공정평가", notes = "공정의 일정과 진행상태를 고려한 평가", position = 5, example = "FAILED")
    private Conditions conditions;

    @ApiModelProperty(value = "공정생명주기상태", notes = "공정의 생명주기상태", position = 6, example = "progress")
    private State state;

    @ApiModelProperty(value = "공정률", notes = "공정의 진행률", position = 7, example = "20")
    private int progressRate;

    @ApiModelProperty(value = "세부공정수", notes = "세부공정 갯수", position = 8, example = "1")
    private int subProcessTotal;

    @ApiModelProperty(value = "완료된 세부 공정 개수", notes = "완료된 세부공정 수", position = 9, example = "1")
    private int doneCount;

    @ApiModelProperty(value = "작업이슈 개수", notes = "보고된 작업이슈의 개수", position = 10, example = "1")
    private int issuesTotal;

    @ApiModelProperty(value = "공정 시작일", notes = "공정기간의 시작일", position = 11, example = "2020-01-16 13:14:02")
    private LocalDateTime startDate;

    @ApiModelProperty(value = "공정 종료일", notes = "공정기간의 종료일", position = 12, example = "2020-01-16 14:14:02")
    private LocalDateTime endDate;

    @ApiModelProperty(value = "최종 보고일", notes = "공정의 최종 보고일", position = 13, example = "2020-01-16 14:14:02")
    private LocalDateTime reportedDate;

    @ApiModelProperty(value = "공정 생성일", notes = "공정 생성일", position = 14, example = "2020-01-16 14:14:02")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "공정 최종 수정일", notes = "공정 최종 수정일", position = 15, example = "2020-01-16 14:14:02")
    private LocalDateTime updatedDate;

    @ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스를 구별합니다.", position = 16, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String workspaceUUID;

    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", position = 17)
    private List<SubProcessAssignedResponse> subProcessAssign;

    @ApiModelProperty(value = "타겟", notes = "타겟 정보", position = 18)
    private List<ProcessTargetResponse> targets;

    @Builder
    public ProcessInfoResponse(@NotBlank long id, @NotBlank String name, @NotBlank String contentUUID, String contentManagerUUID, String position, Conditions conditions, State state, int progressRate, int subProcessTotal, int doneCount, int issuesTotal, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime reportedDate, LocalDateTime createdDate, LocalDateTime updatedDate, String workspaceUUID, List<SubProcessAssignedResponse> subProcessAssign, List<ProcessTargetResponse> targets) {
        this.id = id;
        this.name = name;
        this.contentUUID = contentUUID;
        this.contentManagerUUID = contentManagerUUID;
        this.position = position;
        this.conditions = conditions;
        this.state = state;
        this.progressRate = progressRate;
        this.subProcessTotal = subProcessTotal;
        this.doneCount = doneCount;
        this.issuesTotal = issuesTotal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportedDate = reportedDate;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.workspaceUUID = workspaceUUID;
        this.subProcessAssign = subProcessAssign;
        this.targets = targets;
    }
}

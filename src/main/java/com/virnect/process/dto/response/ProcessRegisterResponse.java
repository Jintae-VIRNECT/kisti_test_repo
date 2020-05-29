package com.virnect.process.dto.response;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.State;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-20
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@ToString
public class ProcessRegisterResponse {
    @ApiModelProperty(value = "신규 작업 식별자", example = "32")
    private long taskId;
    @ApiModelProperty(value = "신규 작업 이름", position = 1, example = "전기 오토바이 바퀴 교체")
    private String name;
    @ApiModelProperty(value = "신규 작업의 세부작업 수", position = 2, example = "12")
    private long totalSubTask;
    @ApiModelProperty(value = "신규 작업의 작업 시작 일자", position = 3, example = "2020-02-03T14:00")
    private LocalDateTime startDate;
    @ApiModelProperty(value = "신규 작업의 작업 종료 일자", position = 4, example = "2020-02-03T14:00")
    private LocalDateTime endDate;
    @ApiModelProperty(value = "작업 진행 상태", position = 5, example = "WAIT")
    private Conditions conditions;
    @ApiModelProperty(value = "작업률", position = 6, example = "0")
    private Integer progressRate;
    @ApiModelProperty(value = "작업 생명주기 상태", position = 7, example = "CREATED")
    private State state;
    @ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스를 구별합니다.", position = 8, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String workspaceUUID;
    @ApiModelProperty(value = "작업의 타겟 정보", position = 9)
    private List<ProcessTargetResponse> target;

    @Builder
    public ProcessRegisterResponse(long taskId, String name, long totalSubTask, LocalDateTime startDate, LocalDateTime endDate, Conditions conditions, Integer progressRate, State state, String workspaceUUID, List<ProcessTargetResponse> target) {
        this.taskId = taskId;
        this.name = name;
        this.totalSubTask = totalSubTask;
        this.startDate = startDate;
        this.endDate = endDate;
        this.conditions = conditions;
        this.progressRate = progressRate;
        this.state = state;
        this.workspaceUUID = workspaceUUID;
        this.target = target;
    }
}

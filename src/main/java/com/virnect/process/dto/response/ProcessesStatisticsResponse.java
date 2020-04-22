package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProcessesStatisticsResponse {
    @ApiModelProperty(value = "전체 공정률", notes = "모든 공정의 진행률 총계", example = "85")
    private int totalRate;
    @ApiModelProperty(value = "전체 공정 개수", notes = "모든 공정의 수", example = "3")
    private int totalProcesses;

    // -- 전체 공정 정보 --
    @ApiModelProperty(value = "시작 대기 공정", notes = "시작 대기 상태인 공정의 수", example = "3")
    private int categoryWait;
    @ApiModelProperty(value = "시작된 공정", notes = "공정이 시작된 상태인 공정의 수", example = "5")
    private int categoryStarted;
    @ApiModelProperty(value = "종료된 (누적) 공정", notes = "공정이 종료된 상태인 공정의 수", example = "8")
    private int categoryEnded;

    // -- 진행상태별 공정수 --
    @ApiModelProperty(value = "대기", notes = "대기 상태인 공정의 수", example = "3")
    private int wait;
    @ApiModelProperty(value = "미진행", notes = "미진행 상태인 공정의 수", example = "2")
    private int unprogressing;
    @ApiModelProperty(value = "진행", notes = "진행 상태인 공정의 수", example = "5")
    private int progressing;
    @ApiModelProperty(value = "미흡", notes = "미흡 상태인 공정의 수", example = "2")
    private int completed;
    @ApiModelProperty(value = "완료", notes = "완료 상태인 공정의 수", example = "7")
    private int incompleted;
    @ApiModelProperty(value = "미완수", notes = "미완수 상태인 공정의 수", example = "2")
    private int failed;
    @ApiModelProperty(value = "완수", notes = "완수 상태인 공정의 수", example = "6")
    private int success;
    @ApiModelProperty(value = "결함", notes = "결함 상태인 공정의 수", example = "1")
    private int fault;

    @Builder
    public ProcessesStatisticsResponse(int totalRate, int totalProcesses, int categoryWait, int categoryStarted, int categoryEnded, int wait, int unprogressing, int progressing, int completed, int incompleted, int failed, int success, int fault) {
        this.totalRate = totalRate;
        this.totalProcesses = totalProcesses;
        this.categoryWait = categoryWait;
        this.categoryStarted = categoryStarted;
        this.categoryEnded = categoryEnded;
        this.wait = wait;
        this.unprogressing = unprogressing;
        this.progressing = progressing;
        this.completed = completed;
        this.incompleted = incompleted;
        this.failed = failed;
        this.success = success;
        this.fault = fault;
    }
}

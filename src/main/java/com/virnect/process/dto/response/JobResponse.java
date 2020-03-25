package com.virnect.process.dto.response;

import com.virnect.process.domain.Conditions;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class JobResponse {
    @ApiModelProperty(value = "작업 식별자", notes = "조회한 작업의 식별자", example = "1")
    private long id;

    @ApiModelProperty(value = "작업명", notes = "조회한 작업의 이름", position = 1, example = "작업1")
    private String name;

    @ApiModelProperty(value = "작업순번", notes = "조회한 작업의 순번", position = 2, example = "1")
    private int priority;

    @ApiModelProperty(value = "보고일시", notes = "작업의 최근 보고 일시", position = 3, example = "2020-01-16T13:14:02")
    private LocalDateTime reportedDate;

    @ApiModelProperty(value = "진행률", notes = "작업의 진행률", position = 4, example = "80")
    private int progressRate;

    @ApiModelProperty(value = "진행상태", notes = "작업의 진행상태", position = 5, example = "progress")
    private Conditions conditions;

    @ApiModelProperty(value = "리포트", notes = "현재 작업의 리포트", position = 6, example = "2")
    private Report report;

    @ApiModelProperty(value = "이슈", notes = "현재 작업의 이슈", position = 7, example = "1")
    private Issue issue;

    @ApiModelProperty(value = "스마트 툴", notes = "현재 작업의 스마트 툴", position = 8)
    private SmartTool smartTool;

    @Builder
    public JobResponse(long id, String name, int priority, LocalDateTime reportedDate, int progressRate, Conditions conditions, Report report, Issue issue, SmartTool smartTool) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.reportedDate = reportedDate;
        this.progressRate = progressRate;
        this.conditions = conditions;
        this.report = report;
        this.issue = issue;
        this.smartTool = smartTool;
    }

    @Getter
    @Setter
    @ToString
    public static class Report {
        @ApiModelProperty(value = "리포트 식별자", notes = "현재 작업의 리포트 식별자", example = "2")
        private long id;

        @Builder
        public Report(long id) {
            this.id = id;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class Issue {
        @ApiModelProperty(value = "이슈 식별자", notes = "현재 작업의 이슈 식별자", example = "1")
        private long id;

        @Builder
        public Issue(long id) {
            this.id = id;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class SmartTool {
        @ApiModelProperty(value = "스마트 툴", notes = "스마트 툴 오브젝트를 식별하기 위해 사용되는 식별자", example = "1")
        private long id;

        @ApiModelProperty(value = "스마트툴 JobId", notes = "스마트툴의 JobId", position = 1, example = "2")
        private String smartToolJobId;

        @ApiModelProperty(value = "체결수", notes = "현재 작업의 체결 완료한 개수", position = 2, example = "3")
        private long smartToolWorkedCount;

        @ApiModelProperty(value = "체결 총수", notes = "체결해야할 총 개수", position = 3, example = "12")
        private int smartToolBatchTotal;

        @Builder
        public SmartTool(long id, String smartToolJobId, long smartToolWorkedCount, int smartToolBatchTotal) {
            this.id = id;
            this.smartToolJobId = smartToolJobId;
            this.smartToolWorkedCount = smartToolWorkedCount;
            this.smartToolBatchTotal = smartToolBatchTotal;
        }
    }
}

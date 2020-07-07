package com.virnect.process.dto.response;

import com.virnect.process.domain.Conditions;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

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

    @ApiModelProperty(value = "페이퍼", notes = "현재 작업의 페이퍼", position = 6, example = "2")
    private Paper paper;

    @ApiModelProperty(value = "이슈", notes = "현재 작업의 이슈", position = 7, example = "1")
    private List<Issue> issueList;

    @Builder
    public JobResponse(long id, String name, int priority, LocalDateTime reportedDate, int progressRate, Conditions conditions, Paper paper, List<Issue> issueList) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.reportedDate = reportedDate;
        this.progressRate = progressRate;
        this.conditions = conditions;
        this.paper = paper;
        this.issueList = issueList;
    }

    @Getter
    @Setter
    @ToString
    public static class Paper {
        @ApiModelProperty(value = "페이퍼 식별자", notes = "현재 작업의 페이퍼 식별자", example = "2")
        private long id;

        @Builder
        public Paper(long id) {
            this.id = id;
        }
    }

    @Getter
    @Setter
    @ToString
    public static class Issue {
        @ApiModelProperty(value = "이슈 식별자", notes = "현재 작업의 이슈 식별자", example = "1")
        private long issueId;

        @ApiModelProperty(value = "이슈 내용", notes = "현재 작업의 이슈 내용", example = "issue content")
        private String caption;

        @ApiModelProperty(value = "이슈 파일 경로", notes = "현재 작업의 이슈 파일 경로", example = "https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/profile/2020-06-25_3b8baadef4be4bbd8d97a9f3815ab6bfjpg")
        private String photoFilePath;

        @ApiModelProperty(value = "작업자 식별자", notes = "현재 작업의 작업자 식별자", example = "498b1839dc29ed7bb2ee90ad6985c608")
        private String workerUUID;
        
        @Builder
        public Issue(long issueId, String caption, String photoFilePath, String workerUUID) {
            this.issueId = issueId;
            this.caption = caption;
            this.photoFilePath = photoFilePath;
            this.workerUUID = workerUUID;
        }
    }
}

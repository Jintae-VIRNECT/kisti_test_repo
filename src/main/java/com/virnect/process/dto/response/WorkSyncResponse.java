package com.virnect.process.dto.response;

import com.virnect.process.domain.Result;
import com.virnect.process.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-25
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkSyncResponse {
    @ApiModelProperty(value = "작업 내용")
    private List<ProcessResult> tasks = new ArrayList<>();
    @ApiModelProperty(value = "작업 외 이슈 리스트", position = 1)
    private List<IssueResult> issues = new ArrayList<>();

    @Getter
    @Setter
    public static class ProcessResult {
        @ApiModelProperty(value = "작업 식별자")
        private long id;
        @ApiModelProperty(value = "세부 작업 내용", position = 1)
        private List<SubProcessWorkResult> subTasks;

        @Builder
        public ProcessResult(long id, List<SubProcessWorkResult> subTasks) {
            this.id = id;
            this.subTasks = subTasks;
        }

        @Override
        public String toString() {
            return "TaskResult{" +
                    "id=" + id +
                    ", subTasks=" + subTasks +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class SubProcessWorkResult {
        @ApiModelProperty(value = "세부 작업 식별자")
        private long id;
        @ApiModelProperty(value = "세부 작업 담당자 식별자", position = 1)
        private String syncUserUUID;
        @ApiModelProperty(value = "세부 작업 작업 진행 정보", position = 2)
        private List<JobWorkResult> steps;

        @Builder
        public SubProcessWorkResult(long id, String syncUserUUID, List<JobWorkResult> steps) {
            this.id = id;
            this.syncUserUUID = syncUserUUID;
            this.steps = steps;
        }

        @Override
        public String toString() {
            return "SubTaskWorkResult{" +
                    "id=" + id +
                    ", syncUserUUID='" + syncUserUUID + '\'' +
                    ", steps=" + steps +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class JobWorkResult {
        @ApiModelProperty(value = "작업 식별자")
        private long id;
        @ApiModelProperty(value = "보고 여부", notes = "값은 \"YES\" or \"NO\" 둘 중 하나임", position = 1, example = "YES")
        private YesOrNo isReported;
        @ApiModelProperty(value = "정상작업 여부", notes = "값은 \"OK\" or \"NOK\" 둘 중 하나임", position = 2, example = "OK")
        private Result result;
        @ApiModelProperty(value = "작업의 레포트 내용", position = 3)
        private List<ReportWorkResult> reports;
        @ApiModelProperty(value = "작업 이슈 리스트", position = 4)
        private List<WorkIssueResult> issues;

        @Builder
        public JobWorkResult(long id, YesOrNo isReported, Result result, List<ReportWorkResult> reports, List<WorkIssueResult> issues) {
            this.id = id;
            this.isReported = isReported;
            this.result = result;
            this.reports = reports;
            this.issues = issues;
        }

        @Override
        public String toString() {
            return "StepWorkResult{" +
                    "id=" + id +
                    ", isReported=" + isReported +
                    ", result=" + result +
                    ", reports=" + reports +
                    ", issues=" + issues +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ReportWorkResult {
        @ApiModelProperty(value = "작업 레포트 식별자")
        private long id;
        @ApiModelProperty(value = "작업 레포트 액션 정보", position = 1)
        private List<ReportItemWorkResult> actions;

        @Builder
        public ReportWorkResult(long id, List<ReportItemWorkResult> actions) {
            this.id = id;
            this.actions = actions;
        }

        @Override
        public String toString() {
            return "ReportWorkResult{" +
                    "id=" + id +
                    ", actions=" + actions +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ReportItemWorkResult {
        @ApiModelProperty(value = "작업 레포트 액션 식별자")
        private long id;
        @ApiModelProperty(value = "작업 레포트 액션 내용", position = 1)
        private String answer;
        @ApiModelProperty(value = "작업 레포트 액션 사진", position = 2)
        private String photoFile;
        @ApiModelProperty(value = "정상작업 여부", notes = "값은 \"OK\" or \"NOK\" 둘 중 하나임", position = 3, example = "OK")
        private Result result;

        @Builder
        public ReportItemWorkResult(long id, String answer, String photoFile, Result result) {
            this.id = id;
            this.answer = answer;
            this.photoFile = photoFile;
            this.result = result;
        }

        @Override
        public String toString() {
            return "ReportActionWorkResult{" +
                    "id=" + id +
                    ", answer='" + answer + '\'' +
                    ", photoFile=" + photoFile +
                    ", result=" + result +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class WorkIssueResult {
        @ApiModelProperty(value = "작업 이슈 사진")
        private String photoFile;
        @ApiModelProperty(value = "작업 이슈 사진 설명", position = 1)
        private String caption;

        @Builder
        public WorkIssueResult(String photoFile, String caption) {
            this.photoFile = photoFile;
            this.caption = caption;
        }

        @Override
        public String toString() {
            return "WorkIssueResult{" +
                    ", photoFile=" + photoFile +
                    ", caption='" + caption + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class IssueResult {
        @ApiModelProperty(value = "<글로벌> 이슈 보고자의 식별자")
        private String workerUUID;
        @ApiModelProperty(value = "<글로벌> 이슈 사진", position = 1)
        private String photoFile;
        @ApiModelProperty(value = "<글로벌> 이슈 사진 설명", position = 2)
        private String caption;

        @Builder
        public IssueResult(String workerUUID, String photoFile, String caption){
            this.workerUUID = workerUUID;
            this.photoFile = photoFile;
            this.caption = caption;
        }

        @Override
        public String toString() {
            return "IssueResult{" +
                    "workerUUID='" + workerUUID + '\'' +
                    ", photoFile=" + photoFile +
                    ", caption='" + caption + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WorkResultSyncRequest{" +
                "tasks=" + tasks +
                ", issues=" + issues +
                '}';
    }
}

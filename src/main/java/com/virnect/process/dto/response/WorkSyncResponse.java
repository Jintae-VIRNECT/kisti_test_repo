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
 * DESCRIPTION: (Post) /tasks/sync와 동일한 구조를 가진 데이터를 리턴하기 위해 생성
 */
@Getter
@Setter
public class WorkSyncResponse {
    @ApiModelProperty(value = "작업 내용")
    private List<ProcessResult> tasks = new ArrayList<>();

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
        @ApiModelProperty(value = "우선순위")
        private long priority;
        @ApiModelProperty(value = "세부 작업 담당자 식별자", position = 1)
        private String syncUserUUID;
        @ApiModelProperty(value = "세부 작업 작업 진행 정보", position = 2)
        private List<JobWorkResult> steps;

        @Builder
        public SubProcessWorkResult(long id, String syncUserUUID, List<JobWorkResult> steps, long priority) {
            this.id = id;
            this.priority = priority;
            this.syncUserUUID = syncUserUUID;
            this.steps = steps;
        }

        @Override
        public String toString() {
            return "SubTaskWorkResult{" +
                    "id=" + id +
                    ", priority='" + priority + '\'' +
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

        @Builder
        public JobWorkResult(long id, YesOrNo isReported, Result result, List<ReportWorkResult> reports) {
            this.id = id;
            this.isReported = isReported;
            this.result = result;
            this.reports = reports;
        }

        @Override
        public String toString() {
            return "StepWorkResult{" +
                    "id=" + id +
                    ", isReported=" + isReported +
                    ", result=" + result +
                    ", reports=" + reports +
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

    @Override
    public String toString() {
        return "WorkResultSyncRequest{" +
                "tasks=" + tasks +
                '}';
    }
}

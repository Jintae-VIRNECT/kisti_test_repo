package com.virnect.process.dto.request;

import com.virnect.process.domain.Result;
import com.virnect.process.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-02-25
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class WorkResultSyncRequest {
    @ApiModelProperty(value = "공정 작업 내용")
    private List<ProcessResult> processes = new ArrayList<>();
    @ApiModelProperty(value = "공정 작업 외 이슈 리스트", position = 1)
    private List<IssueResult> issues = new ArrayList<>();

    @Getter
    @Setter
    public static class ProcessResult {
        @ApiModelProperty(value = "공정 식별자")
        private long id;
        @ApiModelProperty(value = "세부 공정 작업 내용", position = 1)
        private List<SubProcessWorkResult> subProcesses;

        @Override
        public String toString() {
            return "ProcessResult{" +
                    "id=" + id +
                    ", subProcesses=" + subProcesses +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class SubProcessWorkResult {
        @ApiModelProperty(value = "세부 공정 식별자")
        private long id;
        @ApiModelProperty(value = "세부 공정 담당자 식별자", position = 1)
        private String syncUserUUID;
        @ApiModelProperty(value = "세부 공정 작업 진행 정보", position = 2)
        private List<JobWorkResult> jobs;

        @Override
        public String toString() {
            return "SubProcessWorkResult{" +
                    "id=" + id +
                    ", syncUserUUID='" + syncUserUUID + '\'' +
                    ", jobs=" + jobs +
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
        @ApiModelProperty(value = "작업의 스마트 툴 내용", position = 4)
        private List<SmartToolWorkResult> smartTools;
        @ApiModelProperty(value = "공정 작업 이슈 리스트", position = 5)
        private List<WorkIssueResult> issues;

        @Override
        public String toString() {
            return "JobWorkResult{" +
                    "id=" + id +
                    ", isReported=" + isReported +
                    ", result=" + result +
                    ", reports=" + reports +
                    ", smartTools=" + smartTools +
                    ", issues=" + issues +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ReportWorkResult {
        @ApiModelProperty(value = "작업 레포트 식별자")
        private long id;
        @ApiModelProperty(value = "작업 레포트 아이템 정보", position = 1)
        private List<ReportItemWorkResult> items;

        @Override
        public String toString() {
            return "ReportWorkResult{" +
                    "id=" + id +
                    ", items=" + items +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ReportItemWorkResult {
        @ApiModelProperty(value = "작업 레포트 아이템 식별자")
        private long id;
        @ApiModelProperty(value = "작업 레포트 아이템 내용", position = 1)
        private String answer;
        @ApiModelProperty(value = "작업 레포트 아이템 사진", position = 2)
        private String photoFile;
        @ApiModelProperty(value = "정상작업 여부", notes = "값은 \"OK\" or \"NOK\" 둘 중 하나임", position = 3, example = "OK")
        private Result result;

        @Override
        public String toString() {
            return "ReportItemWorkResult{" +
                    "id=" + id +
                    ", answer='" + answer + '\'' +
                    ", photoFile=" + photoFile +
                    ", result=" + result +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class SmartToolWorkResult {
        @ApiModelProperty(value = "스마트 툴 식별자")
        private long id;
        @ApiModelProperty(value = "스마트 툴 작업 식별자", position = 1)
        private long jobId;
        @ApiModelProperty(value = "스마트 툴 아이템 정보", position = 2)
        private List<SmartToolItemWorkResult> items;

        @Override
        public String toString() {
            return "SmartToolWorkResult{" +
                    "id=" + id +
                    ", jobId=" + jobId +
                    ", items=" + items +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class SmartToolItemWorkResult {
        @ApiModelProperty(value = "스마트 툴 아이템 식별자")
        private long id;
        @ApiModelProperty(value = "스마트 툴 아이템 배치 카운트 값", position = 1)
        private int batchCount;
        @ApiModelProperty(value = "스마트 툴 아이템 작업 토크 값", position = 2)
        private String workingTorque;
        @ApiModelProperty(value = "스마트 툴 아이템 작업 결과", notes = "값은 \"OK\" or \"NOK\" 둘 중 하나임", position = 3, example = "OK")
        private Result result;

        @Override
        public String toString() {
            return "SmartToolItemWorkResult{" +
                    "id=" + id +
                    ", batchCount=" + batchCount +
                    ", workingTorque='" + workingTorque + '\'' +
                    ", result='" + result + '\'' +
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
                "processes=" + processes +
                ", issues=" + issues +
                '}';
    }
}

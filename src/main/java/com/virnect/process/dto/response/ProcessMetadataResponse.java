package com.virnect.process.dto.response;

import com.virnect.process.domain.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ProcessMetadataResponse {

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ProcessesMetadata {
        @ApiModelProperty(value = "공정 목록", notes = "공정 메타데이터 목록")
        private List<Process> processes;

        @Builder
        public ProcessesMetadata(List<Process> processes) {
            this.processes = processes;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Process {
        @ApiModelProperty(value = "컨텐츠 타겟 정보", notes = "컨텐츠에 할당된 타겟의 정보")
        private List<ProcessTargetResponse> targetList;

        @ApiModelProperty(value = "공정 식별자", notes = "공정 식별자", position = 1, example = "1")
        private long processId;

        @ApiModelProperty(value = "공정명", notes = "공정명", position = 2, example = "A공정")
        private String processName;

        @ApiModelProperty(value = "관리자 식별자", notes = "공정의 관리자(컨텐츠 제작자)의 식별자", position = 3, example = "uuid")
        private String managerUUID;

        @ApiModelProperty(value = "위치", notes = "위치 설명", position = 4, example = "우측")
        private String position;

        @ApiModelProperty(value = "세부공정수", notes = "세부공정 갯수", position = 5, example = "1")
        private int subProcessTotal;

        @ApiModelProperty(value = " 공정 시작일", notes = " 공정기간의 시작일", position = 6, example = "2020-01-16 13:14:02")
        private LocalDateTime startDate;

        @ApiModelProperty(value = " 공정 종료일", notes = " 공정기간의 종료일", position = 7, example = "2020-01-16 14:14:02")
        private LocalDateTime endDate;

        @ApiModelProperty(value = "공정평가", notes = "공정의 일정과 진행상태를 고려한 평가", position = 8, example = "FAILED")
        private Conditions conditions;

        @ApiModelProperty(value = "공정진행상태", notes = "공정의 진행상태", position = 9, example = "progress")
        private State state;

        @ApiModelProperty(value = "세부 공정 진행률", notes = "세부 공정 작업의 진행률(%)", position = 10, example = "30")
        private int progressRate;

        @ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스를 구별합니다.", position = 11, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
        private String workspaceUUID;

        @ApiModelProperty(value = "세부 공정 목록", notes = "조회한 세부 공정의 목록", position = 12)
        private List<SubProcess> subProcesses;

        @Builder
        public Process(List<ProcessTargetResponse> targetList, long processId, String processName, String managerUUID, String position, int subProcessTotal, LocalDateTime startDate, LocalDateTime endDate, Conditions conditions, State state, int progressRate, String workspaceUUID, List<SubProcess> subProcesses) {
            this.targetList = targetList;
            this.processId = processId;
            this.processName = processName;
            this.managerUUID = managerUUID;
            this.position = position;
            this.subProcessTotal = subProcessTotal;
            this.startDate = startDate;
            this.endDate = endDate;
            this.conditions = conditions;
            this.state = state;
            this.progressRate = progressRate;
            this.workspaceUUID = workspaceUUID;
            this.subProcesses = subProcesses;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SubProcess {

        @ApiModelProperty(value = "세부공정 식별자", notes = "세부공정 식별자", example = "uuid")
        private long subProcessId;

        @ApiModelProperty(value = "세부공정명", notes = "세부공정명", position = 1, example = "1세부공정")
        private String subProcessName;

        @ApiModelProperty(value = "순번", notes = "순번", position = 2, example = "1")
        private int priority;

        @ApiModelProperty(value = "세부작업 갯수(씬의 갯수)", notes = "세부작업의 갯수(씬의 갯수)", position = 3, example = "5")
        private int jobTotal;

        @ApiModelProperty(value = "세부공정 시작일", notes = "세부공정기간의 시작일", position = 4, example = "2020-01-16 13:14:02")
        private LocalDateTime startDate;

        @ApiModelProperty(value = "세부공정 종료일", notes = "세부공정기간의 종료일", position = 5, example = "2020-01-16 14:14:02")
        private LocalDateTime endDate;

        @ApiModelProperty(value = "세부공정평가", notes = "세부공정의 일정과 진행상태를 고려한 평가", position = 6, example = "FAILED")
        private Conditions conditions;

        @ApiModelProperty(value = "세부 공정 진행률", notes = "세부 공정 작업의 진행률(%)", position = 7, example = "30")
        private int progressRate;

        @ApiModelProperty(value = "작업자 식별자", notes = "세부공정의 작업자의 식별자", position = 8, example = "uuid")
        private String workerUUID;

        @ApiModelProperty(value = "보고일시", notes = "보고일시", position = 9, example = "2020-02-15T16:32:13.305")
        private LocalDateTime syncDate;

        @ApiModelProperty(value = "작업자 식별자", notes = "세부공정의 작업자의 식별자", position = 10, example = "uuid")
        private String syncUserUUID;

        @ApiModelProperty(value = "세부 공정 신규할당 여부", notes = "세부 공정의 신규 작업할당 되었는지의 여부", position = 11, example = "true")
        private YesOrNo isRecent;

        @ApiModelProperty(value = "작업 목록", notes = "세부 공정의 작업 목록", position = 12)
        private List<Job> jobs;

        @Builder
        public SubProcess(long subProcessId, String subProcessName, int priority, int jobTotal, LocalDateTime startDate, LocalDateTime endDate, Conditions conditions, int progressRate, String workerUUID, LocalDateTime syncDate, String syncUserUUID, YesOrNo isRecent, List<Job> jobs) {
            this.subProcessId = subProcessId;
            this.subProcessName = subProcessName;
            this.priority = priority;
            this.jobTotal = jobTotal;
            this.startDate = startDate;
            this.endDate = endDate;
            this.conditions = conditions;
            this.progressRate = progressRate;
            this.workerUUID = workerUUID;
            this.syncDate = syncDate;
            this.syncUserUUID = syncUserUUID;
            this.isRecent = isRecent;
            this.jobs = jobs;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Job {
        @ApiModelProperty(value = "작업 식별자", notes = "조회한 작업의 식별자", example = "1")
        private long id;

        @ApiModelProperty(value = "작업명", notes = "조회한 작업의 이름", position = 1, example = "1")
        private String name;

        @ApiModelProperty(value = "작업순번", notes = "조회한 작업의 순번", position = 2, example = "1")
        private int priority;

        @ApiModelProperty(value = "세부 작업(Object) 의 갯수", notes = "세부 작업(Object)의 갯수", position = 3, example = "20")
        private int subJobTotal;

        @ApiModelProperty(value = "작업진행상태", notes = "작업의 진행상태", position = 4, example = "progress")
        private Conditions conditions;

        @ApiModelProperty(value = "작업의 보고여부", notes = "해당 작업이 보고 되었는지의 여부", position = 5, example = "true")
        private YesOrNo isReported;

        @ApiModelProperty(value = "작업 진행률", notes = "작업의 진행률(%)", position = 6, example = "30")
        private int progressRate;

        @ApiModelProperty(value = "작업 수행 결과", notes = "작업을 수행한 결과", position = 7, example = "NOK")
        private Result result;

        @ApiModelProperty(value = "작업 목록", notes = "세부 공정의 작업 목록", position = 9)
        private List<Report> reports;

        @Builder
        public Job(long id, String name, int priority, int subJobTotal, Conditions conditions, YesOrNo isReported, int progressRate, List<Report> reports, Result result) {
            this.id = id;
            this.name = name;
            this.priority = priority;
            this.subJobTotal = subJobTotal;
            this.conditions = conditions;
            this.isReported = isReported;
            this.progressRate = progressRate;
            this.reports = reports;
            this.result = result;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Report {
        @ApiModelProperty(value = "레포트 식별자", notes = "레포트를 식별하기 위해 사용되는 식별자", example = "b5db6bb8-9976-4865-859c-1b98e57a3dc5")
        private long id;

        @ApiModelProperty(value = "레포트 아이템 정보 리스트", notes = "레포트에 들어있는 아이템들에 대한 정보를 담은 배열", position = 1)
        private List<ReportItem> items;

        @Builder
        public Report(Long id, List<ReportItem> items) {
            this.id = id;
            this.items = items;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ReportItem {

        @ApiModelProperty(value = "리포트아이템 식별자", notes = "리포트아이템의 식별자", example = "1")
        private long id;

        @ApiModelProperty(value = "순번", notes = "항목의 순번", position = 1, example = "1")
        private int priority;

        @ApiModelProperty(value = "레포트 아이템 타임", notes = "레포트 아이템의 종류 입니다", position = 2, example = "PROCESS_DETAIL_REPORT")
        private ItemType type;

        @ApiModelProperty(value = "질문", notes = "항목 질문", position = 3, example = "질문 내용입니까?")
        private String title;

        @ApiModelProperty(value = "답변 또는 설명", notes = "질문에 대한 답변 또는 설명", position = 4, example = "답변 설명입니다.")
        private String answer;

        @ApiModelProperty(value = "사진파일 경로", notes = "사진파일 경로", position = 5, example = "http://localhost:8083/process/issue/photo/1.jpg")
        private String photoFile;

        @ApiModelProperty(value = "리포트 항목 수행 결과", notes = "리포트 항목을 수행한 결과", position = 6, example = "NOK")
        private Result result;

        @Builder
        public ReportItem(long id, int priority, ItemType type, String title, String answer, String photoFile, Result result) {
            this.id = id;
            this.priority = priority;
            this.type = type;
            this.title = title;
            this.answer = answer;
            this.photoFile = photoFile;
            this.result = result;
        }
    }
}

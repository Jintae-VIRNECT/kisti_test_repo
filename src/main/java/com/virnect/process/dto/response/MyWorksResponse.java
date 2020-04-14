package com.virnect.process.dto.response;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.State;
import com.virnect.process.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class MyWorksResponse {
    @NotBlank
    @ApiModelProperty(value = "공정식별자", notes = "공정식별자(UUID)", example = "1")
    private final long processId;

    @NotBlank
    @ApiModelProperty(value = "공정명", notes = "공정명", position = 1, example = "공정이름")
    private final String processName;

    @NotBlank
    @ApiModelProperty(value = "컨텐츠식별자", notes = "컨텐츠식별자(UUID)", position = 2, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String contentUUID;

    @NotBlank
    @ApiModelProperty(value = "다운로드 경로", notes = "다운로드 API의 서버 주소 및 포트를 제외한 경로", position = 3, example = "http://~~~")
    private final String downloadPath;

    @NotBlank
    @ApiModelProperty(value = "세부공정식별자", notes = "세부공정식별자", position = 4, example = "1")
    private final long subProcessId;

    @NotBlank
    @ApiModelProperty(value = "세부공정명", notes = "세부공정명(씬그룹명)", position = 5, example = "자제 절단")
    private final String name;

    @NotBlank
    @ApiModelProperty(value = "세부 공정 순서", notes = "세부 공정의 순서 번호", position = 6, example = "1")
    private final int priority;

    @ApiModelProperty(value = "하위 작업 개수", notes = "세부 공정 하위에 작업의 개수", position = 7, example = "3")
    private final int jobTotal;

    @ApiModelProperty(value = "세부 공정 시작 일시", notes = "세부 공정 시작 일시", position = 8, example = "2020-01-16T13:14:02")
    private final LocalDateTime startDate;

    @ApiModelProperty(value = "세부 공정 종료 일시", notes = "세부 공정 종료 일시", position = 9, example = "2020-01-16T14:14:02")
    private final LocalDateTime endDate;

    @ApiModelProperty(value = "세부 공정 보고일", notes = "세부 공정 작업 보고일", position = 10, example = "2020-01-16T14:14:02")
    private final LocalDateTime reportedDate;

    @ApiModelProperty(value = "세부 공정 상태", notes = "세부 공정의 작업진행 상태", position = 11, example = "PROGRESS")
    private final Conditions conditions;

    @ApiModelProperty(value = "세부 공정 진행률", notes = "세부 공정 작업의 진행률(%)", position = 12, example = "30")
    private final int progressRate;

    @ApiModelProperty(value = "세부 공정 신규할당 여부", notes = "세부 공정의 신규 작업할당 되었는지의 여부", position = 13, example = "true")
    private final YesOrNo isRecent;

    @ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", position = 14, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String workerUUID;

    @ApiModelProperty(value = "완료된 작업수", notes = "완료된 작업의 개수", position = 15, example = "1")
    private final int doneCount;

    @ApiModelProperty(value = "공정 생명주기 상태", notes = "공정 생명주기에서의 생성, 종료, 삭제 등의 상태", position = 16, example = "CREATED")
    private final State state;

    @Builder
    public MyWorksResponse(@NotBlank long processId, @NotBlank String processName, @NotBlank String contentUUID, @NotBlank String downloadPath, @NotBlank long subProcessId, @NotBlank String name, @NotBlank int priority, int jobTotal, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime reportedDate, Conditions conditions, int progressRate, YesOrNo isRecent, String workerUUID, int doneCount, State state) {
        this.processId = processId;
        this.processName = processName;
        this.contentUUID = contentUUID;
        this.downloadPath = downloadPath;
        this.subProcessId = subProcessId;
        this.name = name;
        this.priority = priority;
        this.jobTotal = jobTotal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportedDate = reportedDate;
        this.conditions = conditions;
        this.progressRate = progressRate;
        this.isRecent = isRecent;
        this.workerUUID = workerUUID;
        this.doneCount = doneCount;
        this.state = state;
    }
}

package com.virnect.process.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.State;

@Getter
@Setter
public class ProcessInfoResponse {
	@ApiModelProperty(value = "작업 식별자", notes = "작업식별자", example = "1")
	private long id;

	@ApiModelProperty(value = "작업명", notes = "작업명", position = 1, example = "자제 절단 세부작업")
	private String name;

	@ApiModelProperty(value = "컨텐츠 고유 식별자", notes = "해당 식별자를 통해 컨텐츠를 구별합니다.", position = 2, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
	private String contentUUID;

	@ApiModelProperty(value = "컨텐츠 관리자(등록자) 식별자", notes = "해당 컨텐츠를 관리하는(등록한) 사용자의 식별자", position = 3, example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String contentManagerUUID;

	@ApiModelProperty(value = "위치", notes = "위치 설명", position = 4, example = "우측")
	private String position;

	@ApiModelProperty(value = "작업평가", notes = "작업의 일정과 진행상태를 고려한 평가", position = 5, example = "FAILED")
	private Conditions conditions;

	@ApiModelProperty(value = "작업생명주기상태", notes = "작업의 생명주기상태", position = 6, example = "progress")
	private State state;

	@ApiModelProperty(value = "작업률", notes = "작업의 진행률", position = 7, example = "20")
	private int progressRate;

	@ApiModelProperty(value = "세부작업수", notes = "세부작업 갯수", position = 8, example = "1")
	private int subTaskTotal;

	@ApiModelProperty(value = "완료된 세부 작업 개수", notes = "완료된 세부작업 수", position = 9, example = "1")
	private int doneCount;

	@ApiModelProperty(value = "작업이슈 개수", notes = "보고된 작업이슈의 개수", position = 10, example = "1")
	private int issuesTotal;

	@ApiModelProperty(value = "작업 시작일", notes = "작업기간의 시작일", position = 11, example = "2020-01-16 13:14:02")
	private LocalDateTime startDate;

	@ApiModelProperty(value = "작업 종료일", notes = "작업기간의 종료일", position = 12, example = "2020-01-16 14:14:02")
	private LocalDateTime endDate;

	@ApiModelProperty(value = "최종 보고일", notes = "작업의 최종 보고일", position = 13, example = "2020-01-16 14:14:02")
	private LocalDateTime reportedDate;

	@ApiModelProperty(value = "작업 생성일", notes = "작업 생성일", position = 14, example = "2020-01-16 14:14:02")
	private LocalDateTime createdDate;

	@ApiModelProperty(value = "작업 최종 수정일", notes = "작업 최종 수정일", position = 15, example = "2020-01-16 14:14:02")
	private LocalDateTime updatedDate;

	@ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스를 구별합니다.", position = 16, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
	private String workspaceUUID;

	@ApiModelProperty(value = "작업 담당 사용자 식별자", notes = "작업 담당 사용자의 식별자", position = 17)
	private List<SubProcessAssignedResponse> subTaskAssign;

	@ApiModelProperty(value = "타겟", notes = "타겟 정보", position = 18)
	private List<ProcessTargetResponse> targets;

	@ApiModelProperty(value = "컨텐츠 용량", notes = "컨텐츠 용량", position = 19, example = "938")
	private long contentSize;

	@ApiModelProperty(value = "작업 담당자 식별자", notes = "해당 작업을 담당하는 사용자의 식별자", position = 20, example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String workerUUID;

	@Override
	public String toString() {
		return "ProcessInfoResponse{" +
			"id=" + id +
			", name='" + name + '\'' +
			", contentUUID='" + contentUUID + '\'' +
			", contentManagerUUID='" + contentManagerUUID + '\'' +
			", position='" + position + '\'' +
			", conditions=" + conditions +
			", state=" + state +
			", progressRate=" + progressRate +
			", subTaskTotal=" + subTaskTotal +
			", doneCount=" + doneCount +
			", issuesTotal=" + issuesTotal +
			", startDate=" + startDate +
			", endDate=" + endDate +
			", reportedDate=" + reportedDate +
			", createdDate=" + createdDate +
			", updatedDate=" + updatedDate +
			", workspaceUUID='" + workspaceUUID + '\'' +
			", subTaskAssign=" + subTaskAssign +
			", targets=" + targets +
			", contentSize=" + contentSize +
			", workerUUID='" + workerUUID + '\'' +
			'}';
	}
}

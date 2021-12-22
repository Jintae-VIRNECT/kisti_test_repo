package com.virnect.process.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.virnect.process.domain.TargetType;

@Getter
@Setter
public class ProcessDuplicateRequest {

	@NotNull
	private Long taskId;
	@NotBlank
	@ApiModelProperty(value = "작업으로 등록될 컨텐츠의 식별자", notes = "컨텐츠를 구별하기 위해 사용되는 식별자", required = true, example = "da67f860-8462-11ea-bc55-0242ac130003")
	private String contentUUID;

	@NotBlank
	@ApiModelProperty(value = "작업 이름", notes = "작업 이름(컨텐츠 명)", required = true, position = 1, example = "전기 오토바이 제조")
	private String name;

	@ApiModelProperty(value = "컨텐츠 업로드 사용자 식별자", notes = "컨텐츠 업로드 사용자 식별자", required = true, position = 2, example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String ownerUUID = "";

	@NotNull
	@ApiModelProperty(value = "작업 시작일", notes = "작업 시작일", required = true, position = 3, example = "2020-01-16T11:20:33")
	private LocalDateTime startDate;

	@NotNull
	@ApiModelProperty(value = "작업 종료일", notes = "작업 종료일", required = true, position = 4, example = "2020-01-16T12:20:33")
	private LocalDateTime endDate;

	@ApiModelProperty(value = "작업 위치", notes = "작업 위치 정보", position = 5, example = "A 라인 2번 3번째 기계")
	private String position = "미 입력";
	// 타겟 타입을 contents 서버에서 가져오는 것으로 변경. >> 다시 변경
	@NotNull
	@ApiModelProperty(value = "타겟 종류", notes = "작업 타겟의 종류(QR)", required = true, position = 6, example = "QR")
	private TargetType targetType;

	@NotNull
	@ApiModelProperty(value = "타겟 사이즈", notes = "작업 타겟의 사이즈", required = true, position = 7, example = "10")
	private float targetSize;

	@NotBlank
	@ApiModelProperty(value = "워크스페이스 식별자", notes = "작업의 워크스페이스 식별자", required = true, position = 8, example = "48254844-235e-4421-b713-4ea682994a98")
	private String workspaceUUID;

	@NotNull
	@ApiModelProperty(value = "타겟 설정", notes = "작업 타겟 설정(duplicate, closed)", required = true, position = 9, example = "duplicate")
	private String targetSetting;

	@NotNull
	@ApiModelProperty(value = "세부 작업 정보 배열", notes = "해당 작업에서의 세부 작업 정보들이 담긴 배열", required = true, position = 10)
	private List<NewSubProcessRequest> subTaskList;

	@ApiModelProperty(value = "작업자 유저 식별자", notes = "작업자 유저 식별자. 하위작업이 없는 경우에만 입력받습니다.", required = false, position = 11)
	private String workerUUID;

	@Override
	public String toString() {
		return "ProcessDuplicateRequest{" +
			"taskId=" + taskId +
			", contentUUID='" + contentUUID + '\'' +
			", name='" + name + '\'' +
			", ownerUUID='" + ownerUUID + '\'' +
			", startDate=" + startDate +
			", endDate=" + endDate +
			", position='" + position + '\'' +
			", targetType=" + targetType +
			", targetSize=" + targetSize +
			", workspaceUUID='" + workspaceUUID + '\'' +
			", targetSetting='" + targetSetting + '\'' +
			", subTaskList=" + subTaskList +
			", workerUUID='" + workerUUID + '\'' +
			'}';
	}
}

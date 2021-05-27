package com.virnect.serviceserver.serviceremote.dto.response.file;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ShareFileInfoResponse {
	@ApiModelProperty(value = "워크스페이스 식별자", notes = "해당 식별자를 통해 워크스페이스 구별합니다.")
	private String workspaceId;
	@ApiModelProperty(value = "원격협업 식별자", notes = "해당 식별자를 통해 파일 경로 구분 합니다..", position = 1)
	private String sessionId;
	@ApiModelProperty(value = "파일 식별자 이름", notes = "파일 오리지널 이름 입니다", position = 2, example = "직박구리 파일")
	private String name;
	@ApiModelProperty(value = "파일 이름", notes = "파일 이름 입니다", position = 3, example = "2020-08-28_fjxqMrTwIcVmxFIjgfRC")
	private String objectName;
	@ApiModelProperty(value = "파일 타입", notes = "파일 컨텐츠 타입 입니다", position = 4, example = "image/png")
	private String contentType;
	@ApiModelProperty(value = "파일 크기", notes = "파일 크기로 byte 입니다.", dataType = "int", position = 5, example = "10")
	private long size;
	@ApiModelProperty(value = "만료 유무", notes = "만료 유무를 반환 합니다.", dataType = "Boolean", position = 6, example = "false")
	private boolean isExpired;
	@ApiModelProperty(value = "삭제  유무", notes = "삭제 유무를 반환 합니다.", dataType = "Boolean", position = 7, example = "false")
	private boolean isDeleted;
	/*@ApiModelProperty(value = "저장 경로", notes = "파일이 저장된 경로(url)", position = 9, example = "http://localhost:8000/workspaceId/sessionId/file.extention")
	private String path;*/
	@ApiModelProperty(value = "파일 생성 일자", notes = "생성일자(신규 등록) 기간 정보입니다.", position = 8, example = "2020-02-15 16:32:13")
	private LocalDateTime createdDate; //upload date?
	@ApiModelProperty(value = "파일 만료 일자", notes = "만료일자 정보입니다.", position = 9, example = "2020-02-15 16:32:13")
	private LocalDateTime expirationDate;

	@ApiModelProperty(value = "파일 해상도(넓이)", notes = "파일 해상도 입니다(넓이)", position = 10, example = "120")
	private Integer width;
	@ApiModelProperty(value = "파일 해상도(높이)", notes = "파일 해상도 입니다(높이)", position = 11, example = "70")
	private Integer height;
	@ApiModelProperty(value = "썸네일 다운로드 url", notes = "썸네일 다운로드 url", position = 4, example = "url")
	private String thumbnailDownloadUrl;
}

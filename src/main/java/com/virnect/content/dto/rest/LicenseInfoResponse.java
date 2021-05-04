package com.virnect.content.dto.rest;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.29
 */

@Getter
@Setter
public class LicenseInfoResponse {
	@ApiModelProperty(value = "워크스페이스 식별자", example = "4d6eab0860969a50acbfa4599fbb5ae8")
	private String workspaceId = "";
	@ApiModelProperty(value = "라이선스 플랜 마스터 사용자 식별자", position = 1, example = "498b1839dc29ed7bb2ee90ad6985c608")
	private String masterUserUUID = "";
	@ApiModelProperty(value = "워크스페이스 이용 최대 사용자 수 ", notes = "마스터 계정 포함", position = 2, example = "9")
	private Long maxUserAmount = 0L;
	@ApiModelProperty(value = "워크스페이스 최대 저장 용량(byte 단위)", notes = "byte 단위의 최대 용량", position = 3, example = "9663676416")
	private Long maxStorageSize = 0L;
	@ApiModelProperty(value = "워크스페이스 최대 컨텐츠 다운로드 수", position = 4, example = "5000")
	private Long maxDownloadHit = 0L;
	@ApiModelProperty(value = "워크스페이스 최대 통화 횟수", position = 5, example = "100")
	private Long maxCallTime = 0L;
	@ApiModelProperty(value = "라이선스 플랜 시작일자", position = 6, example = "2020-05-15T13:00:00")
	private LocalDateTime startDate = LocalDateTime.now();
	@ApiModelProperty(value = "라이선스 플랜 종료일자", position = 7, example = "2020-05-15T13:00:00")
	private LocalDateTime endDate = LocalDateTime.now();
	@ApiModelProperty(value = "라이선스 플랜 상태", position = 8, example = "ACTIVE")
	private String planStatus = "";
	@ApiModelProperty(value = "플랜 제품별 정보 라이선스 정보", position = 9)
	private List<LicenseProductInfoResponse> licenseProductInfoList;
	@ApiModelProperty(value = "워크스페이스 총 사용 용량(byte 단위)", notes = "워크스페이스 기준 byte 단위의 총 사용 용량", position = 10)
	private Long workspaceStorage;
	@ApiModelProperty(value = "업로드 용량(byte 단위)", notes = "컨텐츠 파일의 용량 (byte 단위)", position = 11)
	private Long uploadSize;
	@ApiModelProperty(value = "워크스페이스 총 다운로드 수", notes = "워크스페이스 기준 총 다운로드 수", position = 12)
	private Long workspaceDownloadHit;
	@ApiModelProperty(value = "남은 용량", notes = "라이선스의 남은 용량", position = 13)
	private Long usableCapacity;
	/*@ApiModelProperty(value = "남은 다운로드 수", notes = "라이선스의 남은 다운로드 수", position = 14)
	private Long usableDownloadHit;*/
    @ApiModelProperty(value = "워크스페이스 현재 다운로드 횟수 정보", position = 12)
    private Long currentUsageDownloadHit = 0L;
}

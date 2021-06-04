package com.virnect.workspace.dto.response;

import com.virnect.workspace.domain.setting.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class SettingInfoResponse {
    @ApiModelProperty(value = "설정 이름", required = true, example = "")
    private SettingName settingName;

    @ApiModelProperty(value = "설정 기본 값", required = true, example = "")
    private SettingValue settingDefaultValue;

    @ApiModelProperty(value = "설정 후보 값", required = true, example = "")
    private List<SettingValue> settingValueList;

    @ApiModelProperty(value = "설정 상태 값", required = true, example = "")
    private Status status;

    @ApiModelProperty(value = "설정 라이선스 상품", required = true, example = "")
    private Product product;

    @ApiModelProperty(value = "설정 결제 타입", required = true, example = "")
    private PaymentType paymentType;

    @ApiModelProperty(value = "설정 추가 일자", required = true, example = "")
    private LocalDateTime createdDate;

    @ApiModelProperty(value = "설정 수정 일자", required = true, example = "")
    private LocalDateTime updatedDate;
}

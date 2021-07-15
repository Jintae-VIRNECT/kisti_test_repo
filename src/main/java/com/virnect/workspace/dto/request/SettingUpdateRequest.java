package com.virnect.workspace.dto.request;

import com.virnect.workspace.domain.setting.PaymentType;
import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class SettingUpdateRequest {
    @ApiModelProperty(value = "변경 할 설정 이름", required = true, example = "")
    @NotNull
    private SettingName settingName;

    @ApiModelProperty(value = "변경 할 설정의 상태 값", required = true, example = "")
    @NotNull
    private Status status;

    @ApiModelProperty(value = "변경 할 설정의 결제 타입", required = true, example = "")
    @NotNull
    private PaymentType paymentType;

    @Override
    public String toString() {
        return "SettingUpdateRequest{" +
                "settingName=" + settingName +
                ", status=" + status +
                ", paymentType=" + paymentType +
                '}';
    }
}

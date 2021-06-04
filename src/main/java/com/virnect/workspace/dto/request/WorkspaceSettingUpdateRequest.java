package com.virnect.workspace.dto.request;

import com.virnect.workspace.domain.setting.PaymentType;
import com.virnect.workspace.domain.setting.SettingName;
import com.virnect.workspace.domain.setting.SettingValue;
import com.virnect.workspace.domain.setting.Status;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
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
public class WorkspaceSettingUpdateRequest {
    private List<WorkspaceSettingUpdateInfo> workspaceSettingUpdateInfoList;

    private static class WorkspaceSettingUpdateInfo {
        @ApiModelProperty(value = "변경 할 설정 이름", required = true, example = "")
        @NotNull
        private SettingName settingName;

        @ApiModelProperty(value = "변경 할 설정 값", required = true, example = "")
        @NotNull
        private SettingValue settingValue;

        @ApiModelProperty(value = "변경 할 설정 상태 값", required = true, example = "")
        @NotNull
        private Status status;

        @ApiModelProperty(value = "변경 할 설정 결제 타입", required = true, example = "")
        @NotNull
        private PaymentType paymentType;
    }

    @ApiModelProperty(value = "변경 요청 유저 식별자", required = true, example = "")
    @NotNull
    private String userId;


}

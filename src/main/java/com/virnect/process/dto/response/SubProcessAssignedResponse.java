package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SubProcessAssignedResponse {

    @NotBlank
    @ApiModelProperty(value = "세부작업식별자", notes = "세부작업식별자", example = "1")
    private final long subTaskId;

    @NotBlank
    @ApiModelProperty(value = "작업자 식별자", notes = "작업자의 식별자(UUID)", position = 1, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String workerUUID;

    @NotBlank
    @ApiModelProperty(value = "사용자 이름", position = 2, example = "VIRNECT Master")
    private String workerName;

    @NotBlank
    @ApiModelProperty(value = "사용자 프로필 이미지 URL", position = 3, example = "VIRNECT 워크스페이스 유저")
    private String workerProfile;

    @Builder
    public SubProcessAssignedResponse(@NotBlank long subTaskId, @NotBlank String workerUUID, @NotBlank String workerName, @NotBlank String workerProfile) {
        this.subTaskId = subTaskId;
        this.workerUUID = workerUUID;
        this.workerName = workerName;
        this.workerProfile = workerProfile;
    }
}

package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SubProcessAssignedResponse {

    @NotBlank
    @ApiModelProperty(value = "세부공정식별자", notes = "세부공정식별자", position = 2, example = "1")
    private final long subProcessId;

    @NotBlank
    @ApiModelProperty(value = "작업자 식별자", notes = "작업자의 식별자(UUID)", example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String workerUUID;

    @NotBlank
    @ApiModelProperty(value = "작업자 프로필", notes = "작업자의 프로필의 경로", position = 1, example = "http://~~~~")
    private final String profile;

    @Builder
    public SubProcessAssignedResponse(@NotBlank long subProcessId, @NotBlank String workerUUID, @NotBlank String profile) {
        this.subProcessId = subProcessId;
        this.workerUUID = workerUUID;
        this.profile = profile;
    }
}

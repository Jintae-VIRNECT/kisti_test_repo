package com.virnect.data.dto.feign;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopRecordingResponse {
    @ApiModelProperty(value = "Recording Identifier")
    private String recordingIds;
}

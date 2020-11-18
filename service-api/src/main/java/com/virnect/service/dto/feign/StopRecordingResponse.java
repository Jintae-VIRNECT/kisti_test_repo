package com.virnect.service.dto.feign;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StopRecordingResponse {
    @ApiModelProperty(value = "Recording Identifier")
    private List<String> recordingIds;
}

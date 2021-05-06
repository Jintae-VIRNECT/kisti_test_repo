package com.virnect.data.dto.rest;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopRecordingResponse {
    @ApiModelProperty(value = "Recording Identifier")
    private List<String> recordingIds;
}

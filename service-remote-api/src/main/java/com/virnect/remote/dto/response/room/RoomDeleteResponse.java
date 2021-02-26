package com.virnect.remote.dto.response.room;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RoomDeleteResponse {
    @ApiModelProperty(value = "Deleted Remote Session Identifier", example = "ses_NxKh1OiT2S")
    public String sessionId = "";
    @ApiModelProperty(value = "Session delete result", position = 1, example = "true")
    public boolean result = false;
    @ApiModelProperty(value = "Delete date", position = 2, example = "2020-01-20T14:05:30")
    public LocalDateTime deletedDate = LocalDateTime.now();

    @Override
    public String toString() {
        return "RoomDeleteResponse{" +
                "sessionId='" + sessionId + '\'' +
                ", result=" + result +
                ", deletedDate=" + deletedDate +
                '}';
    }
}

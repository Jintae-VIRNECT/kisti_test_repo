package com.virnect.serviceserver.serviceremote.dto.response.member;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ApiModel
public class MemberSecessionResponse {
    @ApiModelProperty(value = "Deleted User Unique Identifier", example = "498b1839dc29ed7bb2ee90ad6985c608")
    private final String uuid;
    @ApiModelProperty(value = "Member delete result", position = 1, example = "true")
    private final boolean result;
    @ApiModelProperty(value = "Delete date", position = 2, example = "2020-01-20T14:05:30")
    private final LocalDateTime deletedDate;

    @Override
    public String toString() {
        return "MemberSecessionResponse{" +
                "uuid='" + uuid + '\'' +
                ", result=" + result +
                ", deletedDate=" + deletedDate +
                '}';
    }
}

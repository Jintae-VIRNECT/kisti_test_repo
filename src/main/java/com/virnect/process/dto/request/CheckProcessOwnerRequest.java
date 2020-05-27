package com.virnect.process.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class CheckProcessOwnerRequest {

    @NotNull
    @ApiModelProperty(value = "작업 식별자", notes = "작업 식별자", required = true, example = "1")
    private Long taskId;

    @ApiModelProperty(value = "사용자 식별자", notes = "", required = true, position = 2, example = "498b1839dc29ed7bb2ee90ad6985c608")
    private String actorUUID = "";
}

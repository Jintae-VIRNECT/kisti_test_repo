package com.virnect.remote.dto.request.room;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class RoomHistoryDeleteRequest {
    @NotBlank
    @ApiModelProperty(value = "User Unique Identifier")
    private String uuid;

    @ApiModelProperty(value = "To delete Remote Session Identifier List", position = 1)
    @NotNull
    private List<String> sessionIdList = new ArrayList<>();

    @Override
    public String toString() {
        return "RoomHistoryDeleteRequest{" +
                "uuid='" + uuid + '\'' +
                "sessionIdList='" + sessionIdList.toString() + '\'' +
                '}';
    }
}

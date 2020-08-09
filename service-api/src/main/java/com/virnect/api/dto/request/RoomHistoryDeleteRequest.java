package com.virnect.api.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ApiModel
public class RoomHistoryDeleteRequest {
    @NotBlank
    @ApiModelProperty(value = "사용자 식별자")
    private String uuid;

    @ApiModelProperty(value = "삭제할 세션 리스트", position = 1)
    private List<String> sessionIdList = new ArrayList<>();

    @Override
    public String toString() {
        return "RoomHistoryDeleteRequest{" +
                "uuid='" + uuid + '\'' +
                "email='" + sessionIdList.toString() + '\'' +
                '}';
    }
}

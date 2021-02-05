package com.virnect.serviceserver.dto.push;

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
public class SendSignalRequest {
    @NotBlank
    @ApiModelProperty(value = "Remote Session Identifier")
    private String sessionId;

    @ApiModelProperty(position = 1)
    private List<String> to = new ArrayList<>();

    @ApiModelProperty(position = 2)
    private String type;

    @ApiModelProperty(position = 3)
    private String data;

    @Override
    public String toString() {
        return "SendSignalRequest{" +
                "sessionId='" + sessionId + '\'' +
                "type='" + type + '\'' +
                "data='" + data + '\'' +
                '}';
    }
}

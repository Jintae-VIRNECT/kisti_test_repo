package com.virnect.file.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel
@AllArgsConstructor
public class ResultResponse {
    @ApiModelProperty(value = "User Identifier", example = "ses_NxKh1OiT2S")
    public String userId = "";
    @ApiModelProperty(value = "Responses result", position = 1, example = "true")
    public Boolean result = false;
    @ApiModelProperty(value = "Response Date", position = 2, example = "2020-01-20T14:05:30")
    public LocalDateTime resultDate = LocalDateTime.now();

    @Override
    public String toString() {
        return "ResultResponse{" +
                "userId='" + userId + '\'' +
                "result='" + result + '\'' +
                "resultDate='" + resultDate + '\'' +
                '}';
    }
}

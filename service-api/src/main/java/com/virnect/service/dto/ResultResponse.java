package com.virnect.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    @ApiModelProperty(value = "User Identifier", example = "410df50ca6e32db0b6acba09bcb457ff")
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

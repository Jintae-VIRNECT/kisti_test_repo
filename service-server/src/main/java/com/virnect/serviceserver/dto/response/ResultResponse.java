package com.virnect.serviceserver.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @ApiModelProperty(value = "Result reason", example = "{\n" +
            "  \"custom1\": \"string\",\n" +
            "  \"custom2\": \"string\"\n" +
            "}", required = true, position = 3)
    private Map<Object, Object> reason = new HashMap<>();

    @Override
    public String toString() {
        return "ResultResponse{" +
                "userId='" + userId + '\'' +
                "result='" + result + '\'' +
                "resultDate='" + resultDate + '\'' +
                '}';
    }
}

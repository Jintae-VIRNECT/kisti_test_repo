package com.virnect.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ApiModel
@RequiredArgsConstructor
public class DeleteResultResponse {
    @ApiModelProperty(value = "User Identifier", example = "410df50ca6e32db0b6acba09bcb457ff")
    public String userId = "";

    @ApiModelProperty(value = "Responses result", position = 1, example = "true")
    public Boolean result = false;

    @ApiModelProperty(value = "Delete date", position = 2, example = "2020-01-20T14:05:30")
    public LocalDateTime deletedDate = LocalDateTime.now();

    @ApiModelProperty(value = "Result reason", example = "{\n" +
            "  \"custom1\": \"string\",\n" +
            "  \"custom2\": \"string\"\n" +
            "}", required = true, position = 3)
    private Map<Object, Object> reason = new HashMap<>();

    @Override
    public String toString() {
        return "DeleteResultResponse{" +
                "userId='" + userId + '\'' +
                "result='" + result + '\'' +
                "deletedDate='" + deletedDate + '\'' +
                '}';
    }
}

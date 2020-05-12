package com.virnect.message.global.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: PF-Message
 * DATE: 2020-02-17
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    @ApiModelProperty(value = "API 응답 데이터", dataType = "object")
    T data;

    @ApiModelProperty(value = "API 응답 상태 코드", dataType = "int")
    int code = 200;

    @ApiModelProperty(value = "API 응답 메세지", dataType = "string")
    String message = "complete";

    public ApiResponse(T data) {
        this.data = data;
    }
}

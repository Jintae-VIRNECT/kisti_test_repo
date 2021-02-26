package com.virnect.data.error;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@ApiModel
public class ErrorResponseMessage {
    @ApiModelProperty(value = "에러 응답 코드")
    private int code;
    @ApiModelProperty(value = "서비스명")
    private String service;
    @ApiModelProperty(value = "에러 응답 메시지")
    private String message;
    @ApiModelProperty(value = "에러 응답 데이터")
    private Map<String, Object> data;

    public ErrorResponseMessage(final ErrorCode error) {
        this.code = error.getCode();
        this.message = error.getMessage();
        this.service = "remote";
        data = new HashMap<>();
    }
}

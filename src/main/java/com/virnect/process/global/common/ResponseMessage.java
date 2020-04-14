package com.virnect.process.global.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Response Message Builder
 */

@ApiModel
@Getter
@Setter
@NoArgsConstructor
public class ResponseMessage {
    @ApiModelProperty(value = "API 응답 데이터를 갖고 있는 객체.", dataType = "object")
    Map<String, Object> data = new ConcurrentHashMap<>();
    @ApiModelProperty(value = "API 처리 결과 상태 코드 값, 200이면 정상 처리 완료.", dataType = "int")
    int code = 200;
    @ApiModelProperty(value = "API 처리 결과에 대한 메시지", dataType = "string")
    String message = "complete";

    public ResponseMessage(Map<String, Object> data) {
        this.data = data;
    }

    public ResponseMessage addParam(String key, Object object) {
        this.data.put(key, object);
        return this;
    }
}

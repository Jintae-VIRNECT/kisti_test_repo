package com.virnect.uaa.infra.rest.message.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Project: PF-Message
 * DATE: 2021-03-08
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class EventSendRequest {
    @ApiModelProperty(value = "이벤트 이름", example = "WORKSPACE_EXPELED", required = true, position = 4)
    private final String eventName;

    @ApiModelProperty(value = "식별자", example = "4ff0606102fbe", required = true, position = 1)
    private final String eventUUID;

    @ApiModelProperty(value = "발송 서비스 명", example = "pf-workspace", required = true, position = 0)
    private final String service;

    @ApiModelProperty(value = "메세지 내용", example = "{\n" +
            "  \"custom1\": \"string\",\n" +
            "  \"custom2\": \"string\"\n" +
            "}", required = true, position = 5)
    private final Map<Object, Object> contents;


}

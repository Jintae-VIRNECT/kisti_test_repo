package com.virnect.message.dto.request;

import com.virnect.message.domain.MessageType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Project: PF-Message
 * DATE: 2021-03-08
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@Setter
public class EventSendRequest {
    @ApiModelProperty(value = "이벤트 이름", example = "WORKSPACE_EXPELED", required = true, position = 4)
    @NotBlank
    private String eventName;

    @ApiModelProperty(value = "식별자", example = "4ff0606102fbe", required = true, position = 1)
    @NotBlank
    private String eventUUID;

    @ApiModelProperty(value = "발송 서비스 명", example = "pf-workspace", required = true, position = 0)
    //@NotBlank
    private String service;

    @ApiModelProperty(value = "메세지 내용", example = "{\n" +
            "  \"custom1\": \"string\",\n" +
            "  \"custom2\": \"string\"\n" +
            "}", required = true, position = 5)
    private Map<Object, Object> contents;


}

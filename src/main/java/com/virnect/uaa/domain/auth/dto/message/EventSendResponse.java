package com.virnect.uaa.domain.auth.dto.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@ApiModel
@Getter
@Setter
public class EventSendResponse {
    @ApiModelProperty(value = "메시지 수신 서비스 명", example = "workspace")
    private String service;
    @ApiModelProperty(value = "메시지 이벤트", position = 1, example = "something event")
    private String eventName;
    @ApiModelProperty(value = "메시지 수신 워크스페이스 식별자", position = 2, example = "4d6eab0860969a50acbfa4599fbb5ae8")
    private String eventUUID;
    @ApiModelProperty(value = "메시지 발신 완료 여부", position = 3, example = "true")
    private boolean isPublished;
    @ApiModelProperty(value = "메시지 발신 일자", position = 4, example = "2020-07-17T15:44:20")
    private LocalDateTime publishDate;

    @Override
    public String toString() {
        return "EventSendResponse{" +
                "service='" + service + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventUUID='" + eventUUID + '\'' +
                ", isPublished=" + isPublished +
                ", publishDate=" + publishDate +
                '}';
    }
}

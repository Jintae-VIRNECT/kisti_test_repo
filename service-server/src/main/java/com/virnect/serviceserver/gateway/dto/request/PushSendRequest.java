package com.virnect.serviceserver.gateway.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ApiModel
public class PushSendRequest {
    @ApiModelProperty(value = "푸시 발송 서비스 명", example = "rm-service", required = true, position = 0)
    @NotBlank
    private String service = "remote";

    @ApiModelProperty(value = "워크스페이스 식별자", example = "4ff0606102fbe", required = true, position = 1)
    @NotBlank
    private String workspaceId;

    @ApiModelProperty(value = "메세지 발행 유저 식별자", example = "4ff0606102fbe", required = true, position = 2)
    @NotBlank
    private String userId;

    @ApiModelProperty(value = "메세지 수신 대상 식별자", example = "[\"4ff0606102fbe\"]", required = true, position = 3)
    @NotNull
    private List<String> targetUserIds;

    @ApiModelProperty(value = "이벤트 이름", example = "ROOM_INVITE", required = true, position = 4)
    @NotBlank
    private String event;

    @ApiModelProperty(value = "메세지 내용", example = "{\n" +
            "  \"custom1\": \"string\",\n" +
            "  \"custom2\": \"string\"\n" +
            "}", required = true, position = 5)
    private Map<Object, Object> contents;
}

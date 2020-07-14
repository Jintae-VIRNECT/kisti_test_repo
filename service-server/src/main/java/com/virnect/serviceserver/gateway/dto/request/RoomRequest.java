package com.virnect.serviceserver.gateway.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ApiModel
public class RoomRequest {
    @NotBlank
    @ApiModelProperty(value = "협업 방 이름", position = 1, example = "Remote")
    private String title;

    @ApiModelProperty(value = "협업 방 소개", position = 2, example = "This is Remote Collaborate, or.. Conference Room(Session)!!")
    private String description;

    @ApiModelProperty(value = "서버 자동 녹화 선택", position = 3, example = "false")
    private boolean autoRecording;

    @ApiModelProperty(value = "리더 유저 uuid", position = 4, example = "410df50ca6e32db0b6acba09bcb457ff")
    @NotNull
    private String leaderId;

    @ApiModelProperty(value = "리더 유저 이메일", position = 5, example = "test18@test.com")
    @NotNull
    private String leaderEmail;

    @ApiModelProperty(value = "워크스페이 uuid", position = 6, example = "40f9bbee9d85dca7a34a0dd205aae718")
    @NotNull
    private String workspaceId;

    @ApiModelProperty(
            value = "협업 방 참여자 정보",
            position = 7,
            dataType = "List",
            example = "[\n" +
                    "  {\n" +
                    "    \"id\": \"4705cf50e6d02c59b0eef9591666e2a3\",\n" +
                    "    \"email\": \"test17@test.com\"\n" +
                    "  }\n" +
                    ",\n" +
                    "  {\n" +
                    "    \"id\": \"473b12854daa6afeb9e505551d1b2743\",\n" +
                    "    \"email\": \"test19@test.com\"\n" +
                    "  }\n" +
                    "\n" +
                    "]"
    )
    private List<Participant> participants;

    /**
     * test17@test.com 4705cf50e6d02c59b0eef9591666e2a3
     * test19@test.com 473b12854daa6afeb9e505551d1b2743
     */
    @Getter
    @Setter
    public static class Participant {
        @ApiModelProperty(value = "초대할 사용자 uuid")
        private String id; //participant uuid
        @ApiModelProperty(value = "초대할 사용자 e-mail")
        private String email; //participant e-mail

        @Override
        public String toString() {
            return "Participant{" +
                    "id='" + id + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}

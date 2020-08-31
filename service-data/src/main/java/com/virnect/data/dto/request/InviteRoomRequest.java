package com.virnect.data.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class InviteRoomRequest {
    @ApiModelProperty(value = "리더 유저 uuid",  example = "410df50ca6e32db0b6acba09bcb457ff")
    @NotNull
    private String leaderId;

    /*@ApiModelProperty(
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
    )*/
    @ApiModelProperty(
            value = "협업 방 참여자 정보",
            position = 1,
            dataType = "List",
            example = "[\n" +
                      " \"4705cf50e6d02c59b0eef9591666e2a3\",\n" +
                      " \"473b12854daa6afeb9e505551d1b2743\",\n" +
                      "\n" +
                      "]"
    )
    @NotNull
    private List<String> participantIds;

    /**
     * test17@test.com 4705cf50e6d02c59b0eef9591666e2a3
     * test19@test.com 473b12854daa6afeb9e505551d1b2743
     */
    /*@Getter
    @Setter
    public static class Participant {
        @ApiModelProperty(value = "초대할 사용자 uuid")
        private String id; //participant uuid
        *//*@ApiModelProperty(value = "초대할 사용자 e-mail")
        private String email; //participant e-mail*//*

        @Override
        public String toString() {
            return "Participant{" +
                    "id='" + id + '\'' +
                    //", email='" + email + '\'' +
                    '}';
        }
    }*/
}

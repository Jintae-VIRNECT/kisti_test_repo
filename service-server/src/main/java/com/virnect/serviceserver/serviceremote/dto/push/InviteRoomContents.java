package com.virnect.serviceserver.serviceremote.dto.push;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteRoomContents {
    private String sessionId;
    private String title;
    private String nickName;
    private String profile;
    private String leaderId;
}

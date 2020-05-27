package com.virnect.workspace.global.constant;

import lombok.Getter;

/**
 * Project: PF-Workspace
 * DATE: 2020-04-28
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum MailSubject {
    WORKSPACE_INVITE("[VIRNECT] 워크스페이스 참여 요청 안내 메일입니다."),
    WORKSPACE_INVITE_REJECT("[VIRNECT] 워크스페이스 참여 요청 안내 메일입니다."),
    WORKSPACE_INVITE_ACCEPT("[VIRNECT] 워크스페이스 신규 참여자 안내 메일입니다."),
    WORKSPACE_KICKOUT("[VIRNECT] 워크스페이스 참여 변동 안내 메일입니다."),
    WORKSPACE_INFO_UPDATE("[VIRNECT] 워크스페이스 정보 변경 안내 메일입니다."),
    WORKSPACE_USER_PERMISSION_UPDATE("[VIRNECT] 워크스페이스 권한 변경 안내 메일입니다."),
    WORKSPACE_USER_PLAN_UPDATE("[VIRNECT] 워크스페이스 플랜 변경 안내 메일입니다.");

    @Getter
    private String subject;

    MailSubject(String subject) {
        this.subject = subject;
    }

}

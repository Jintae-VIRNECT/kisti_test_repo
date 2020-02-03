package com.virnect.workspace.global.constant.mail;

/**
 * Project: PF-Workspace
 * DATE: 2020-02-03
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum MailSender {
    MASTER("발신전용 <no-reply@virnect.com>"),
    MASTER_NAME("관리자");
    private String sender;

    MailSender(String sender){
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }
}

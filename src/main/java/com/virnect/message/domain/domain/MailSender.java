package com.virnect.message.domain.domain;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
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

package com.virnect.message.domain;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum MailSender {
    MASTER("no-reply@virnect.com");

    private String sender;

    MailSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }
}

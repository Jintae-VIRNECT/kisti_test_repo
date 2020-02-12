package com.virnect.message.domain.domain;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public enum MailSubject {
    MAIL_SUBJECT_PREFIX("VIRNECT PLATFORM");

    private String subject;

    MailSubject(String subject){
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}

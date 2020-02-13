package com.virnect.message.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
public enum MailSubject {
    MAIL_SUBJECT_PREFIX("VIRNECT PLATFORM"),
    CONTACT("문의하신 내용이 VIRNECT 팀에게 전달되었습니다.");

    private String subject;

    MailSubject(String subject){
        this.subject = subject;
    }


}

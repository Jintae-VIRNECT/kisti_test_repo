package com.virnect.message.domain;

import lombok.Getter;

/**
 * Project: PF-Message
 * DATE: 2020-02-12
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
public enum MailTemplate {
    // 문의 관련
    CONTACT_REQUEST("inquiry_receive"), // 문의 접수 완료 메일
    CONTACT_RESPONSE("inquiry_send"); // 문의 메일

    private String template;

    MailTemplate(String template) {
        this.template = template;
    }


}

package com.virnect.content.infra.email;

import org.thymeleaf.context.Context;

/**
 * Project: service-server
 * DATE: 2020-01-09
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface MailService {
    void sendTemplateMail(String sender, String to, String subject, String template, Context context);
}

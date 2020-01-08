package com.virnect.workspace.infra.mail;

import org.thymeleaf.context.Context;

/**
 * Project: service-server
 * DATE: 2019-10-30
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public interface MailService {
    void sendTemplateMail(String sender, String to, String subject, String template, Context context);
}

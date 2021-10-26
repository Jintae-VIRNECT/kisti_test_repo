package com.virnect.workspace.event.message;

import com.virnect.workspace.global.constant.Mail;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Locale;

/**
 * Project: PF-Workspace
 * DATE: 2021-07-14
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class MailSendEvent {
    private final Context context;
    private final Mail mailType;
    private final Locale locale;
    private final List<String> receiverList;

    @Override
    public String toString() {
        return "MailSendEvent{" +
                //"context=" + context +
                "mailType=" + mailType +
                ", locale=" + locale +
                ", receiverList=" + receiverList +
                '}';
    }
}

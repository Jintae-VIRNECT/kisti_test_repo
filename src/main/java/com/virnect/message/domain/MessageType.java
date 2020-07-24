package com.virnect.message.domain;

import lombok.Getter;

/**
 * Project: PF-Message
 * DATE: 2020-06-15
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
public enum MessageType {
    EMAIL("email"),
    PUSH("push");

    private String value;
    MessageType(String value) {
        this.value = value;
    }

}

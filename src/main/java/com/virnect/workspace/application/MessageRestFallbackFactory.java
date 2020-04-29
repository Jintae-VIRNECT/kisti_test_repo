package com.virnect.workspace.application;

import feign.hystrix.FallbackFactory;

/**
 * Project: PF-Workspace
 * DATE: 2020-04-29
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class MessageRestFallbackFactory implements FallbackFactory<MessageRestService> {
    @Override
    public MessageRestService create(Throwable cause) {
        return null;
    }
}

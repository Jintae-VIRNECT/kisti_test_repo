package com.virnect.workspace.application;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Project: PF-Workspace
 * DATE: 2021-06-04
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class WorkspaceSettingInterceptor implements HandlerInterceptor {
    private static ThreadLocal<String> THREAD_LOCAL_INFO = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return false;
    }
}

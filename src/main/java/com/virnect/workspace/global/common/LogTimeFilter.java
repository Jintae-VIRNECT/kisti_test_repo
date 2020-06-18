package com.virnect.workspace.global.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Project: PF-Workspace
 * DATE: 2020-05-19
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Component
public class LogTimeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestUrl = null;
        if (request instanceof HttpServletRequest) {
            requestUrl = ((HttpServletRequest) request).getRequestURL().toString();
        }
        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Request Url : {}, take Time : {}(ms)", requestUrl, duration);

    }

}

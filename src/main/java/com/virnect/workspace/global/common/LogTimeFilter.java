package com.virnect.workspace.global.common;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

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
        String method = null;
        if (request instanceof HttpServletRequest) {
            requestUrl = ((HttpServletRequest) request).getRequestURL().toString();
            method = ((HttpServletRequest) request).getMethod();
        }
        String requestParam = request.getParameterMap().entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), Joiner.on(",").join(entry.getValue())))
                .collect(Collectors.joining(", "));
        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        long duration = System.currentTimeMillis() - startTime;
        log.info("Request Url : {}, take Time : {}(ms)", method + " " + requestUrl + "?" + requestParam, duration);

    }

}

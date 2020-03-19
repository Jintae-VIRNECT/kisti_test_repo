package com.virnect.content.global.common;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * @author jeonghyeon.chang (johnmark)
 * @email practice1356@gmail.com
 * @since 2020.02.29
 */

@Slf4j
public class ReadableRequestWrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ReadableRequestWrapper wrapper = new ReadableRequestWrapper((HttpServletRequest) request);
        chain.doFilter(wrapper, response);
    }

    @Override
    public void destroy() {

    }
}

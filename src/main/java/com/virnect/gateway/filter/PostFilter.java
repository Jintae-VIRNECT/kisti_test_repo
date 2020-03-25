package com.virnect.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Gateway
 * @email practice1356@gmail.com
 * @description Response Logging Filter
 * @since 2020.03.17
 */
@Slf4j
public class PostFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        log.info("[REQUEST IP] => [{}] , [REQUEST URL] => [{}] , [AUTHORIZATION] => [{}]", request.getRemoteHost(), request.getRequestURL(), request.getHeader("authorization"));
        log.info("[RESPONSE Status] => [{}]", requestContext.getResponse().getStatus());
        return null;
    }
}

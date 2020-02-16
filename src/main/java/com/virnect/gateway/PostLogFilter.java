package com.virnect.gateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class PostLogFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        log.info("[REQUEST IP] => [{}] , [REQUEST URL] => [{}]", request.getRemoteHost(), request.getRequestURL());
        InputStream inputStream = requestContext.getResponseDataStream();
        try {
            InputStream responseDataStream = new BufferedInputStream(inputStream);
            String responseAsString = StreamUtils.copyToString(responseDataStream, StandardCharsets.UTF_8);
            log.info("[RESPONSE] => [{}]", responseAsString);
            requestContext.setResponseBody(responseAsString);
        } catch (IOException e) {
            log.warn("Error Reading Body", e);
        }
        return null;
    }
}

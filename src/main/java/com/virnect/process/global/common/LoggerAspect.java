package com.virnect.process.global.common;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jeonghyeon.chang (johnmark)
 * @email practice1356@gmail.com
 * @since 2020.02.29
 */

@Component
@Aspect
@Slf4j
public class LoggerAspect {
    private final HttpServletRequest request;

    public LoggerAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Before("execution(* com.virnect.process.domain.*.api..*Controller.*(..))")
    public void requestLogger(JoinPoint joinPoint) {
        String controllerName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> params = new HashMap<>();

        try {
            params.put("ContentType", request.getContentType());
            params.put("IP", ClientRequestUserAgentParser.getClientIP(request));
            params.put("OS", ClientRequestUserAgentParser.getClientOS(request));
            params.put("Browser", ClientRequestUserAgentParser.getClientBrowser(request));
            params.put("RequestUri", request.getRequestURI());
            params.put("HttpMethod", request.getMethod());
            params.put("Controller", controllerName);
            params.put("Method", methodName);
            params.put("Params", getParams(request));
            params.put("Time", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Logger Aspect Error", e);
        }

        log.info("[Request] => [{}]", params);
    }

    private static JsonObject getParams(HttpServletRequest request) {
        JsonObject jsonObject = new JsonObject();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            String replaceParam = param.replaceAll("\\.", "-");
            jsonObject.addProperty(replaceParam, request.getParameter(param));
        }
        return jsonObject;
    }
}

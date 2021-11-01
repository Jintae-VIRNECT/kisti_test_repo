package com.virnect.data.global.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        log.info("Thread Error Exception Message :: " + throwable.getMessage());
        log.info("Method name :: " + method.getName());
        for(Object param : obj) {
            log.info("Param Value ::: " + param);
        }
    }
}
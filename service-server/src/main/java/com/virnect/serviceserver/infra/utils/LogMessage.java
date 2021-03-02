package com.virnect.serviceserver.infra.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogMessage {
    public static String PARAMETER_ERROR = "PARAMETER ERROR";

    public static void formedInfo(String tag, String process , String methodName){
        String comment = String.format("%s::%s::#%s::", tag, process, methodName);
        log.info("{}", comment);
    }

    public static void formedInfo(String tag, String process , String methodName, String reason, String result){
        String comment = String.format("%s::%s::#%s::%s", tag, process, methodName, reason);
        log.info("{} => {}", comment, result);
    }

    public static void formedInfo(String tag, String process , String methodName, String reason){
        String comment = String.format("%s::%s::#%s::%s", tag, process, methodName, reason);
        log.info("{} => not return result", comment);
    }

    public static void formedError(String tag, String process , String methodName, String reason, String result){
        String comment = String.format("%s::%s::#%s::[%s]", tag, process, methodName, reason);
        log.error("{} => {}", comment, result);
    }

    public static void formedError(String tag, String process , String methodName, String reason){
        String comment = String.format("%s::%s::#%s::[%s]", tag, process, methodName, reason);
        log.error("{}", comment);
    }
}

package com.virnect.process.global.common;

import javax.servlet.http.HttpServletRequest;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class ClientRequestUserAgentParser {
    /**
     * 사용자 IP 추출
     *
     * @param request - 사용자 요청
     * @return 아이피 주소
     */
    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");

        // 프록시 환경인 경우
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        // 웹로직 서버인 경우
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 브라우저 정보
     *
     * @param request
     * @return
     */
    public static String getClientBrowser(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        String browser = null;
        if (agent.indexOf("Trident") != -1 || agent.indexOf("MSIE") != -1) {
            browser = "IE";
        } else if (agent.indexOf("Opera") != -1) {
            browser = "Opera";
        } else if (agent.indexOf("Firefox") != -1) {
            browser = "Firefox";
        } else if (agent.indexOf("iPhone") != -1 && agent.indexOf("Mobile") != -1) {
            browser = "iPhone";
        } else if (agent.indexOf("Android") != -1 && agent.indexOf("Mobile") != -1) {
            browser = "Android";
        } else if (agent.indexOf("Safari") != -1) {
            if (agent.indexOf("Chrome") != -1) {
                browser = "Chrome";
            } else {
                browser = "Safari";
            }
        } else {
            browser = "";
        }
        return browser;
    }

    /**
     * os 정보
     *
     * @param request
     * @return
     */
    public static String getClientOS(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        String os = null;
        if (agent.indexOf("Windows") != -1) os = "Windows";
        else if (agent.indexOf("Linux") != -1) os = "Linux";
        else if (agent.indexOf("Macintosh") != -1) os = "Macintosh";
        else os = "";
        return os;
    }
}

package com.virnect.workspace.global.config;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;

/**
 * Project: PF-Workspace
 * DATE: 2021-05-07
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Configuration
public class FeignCustomLogger extends Logger {

    @Override
    protected void log(String configKey, String format, Object... args) {
        log.info(String.format(methodTag(configKey) + format, args));
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        log(configKey, "---> %s %s HTTP/1.1", request.httpMethod().name(), request.url());
        int bodyLength = 0;
        if (request.body() != null) {
            bodyLength = request.length();
            if (logLevel.ordinal() >= Level.FULL.ordinal()) {
                String bodyText =
                        request.charset() != null
                                ? new String(request.body(), request.charset())
                                : null;
                log(configKey, ""); // CRLF
                log(configKey, "%s", bodyText != null ? bodyText : "Binary data");
            }
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        String reason = response.reason() != null && logLevel.compareTo(Level.NONE) > 0 ? " " + response.reason() : "";
        int status = response.status();
        log(configKey, "<--- HTTP/1.1 %s%s (%sms)", status, reason, elapsedTime);

        int bodyLength = 0;
        if (response.body() != null && !(status == 204 || status == 205)) {
            // HTTP 204 No Content "...response MUST NOT include a message-body"
            // HTTP 205 Reset Content "...response MUST NOT include an entity"
            if (logLevel.ordinal() >= Level.FULL.ordinal()) {
                log(configKey, ""); // CRLF
            }
            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            bodyLength = bodyData.length;
            if (logLevel.ordinal() >= Level.FULL.ordinal() && bodyLength > 0) {
                log(configKey, "%s", decodeOrDefault(bodyData, UTF_8, "Binary data"));
            }
            log(configKey, "<--- END HTTP (%s-byte body)", bodyLength);
            return response.toBuilder().body(bodyData).build();
        } else {
            log(configKey, "<--- END HTTP (%s-byte body)", bodyLength);
        }

        return response;
    }
}

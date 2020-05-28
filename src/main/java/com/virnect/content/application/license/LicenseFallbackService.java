package com.virnect.content.application.license;

import com.virnect.content.global.common.ResponseMessage;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Project: PF-ContentManagement
 * DATE: 2020-02-04
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@Service
public class LicenseFallbackService implements FallbackFactory<ResponseMessage> {
    @Override
    public ResponseMessage create(Throwable cause) {
        log.error("USER SERVER FEIGN ERROR: {}", cause.getMessage());
        return new ResponseMessage();
    }
}

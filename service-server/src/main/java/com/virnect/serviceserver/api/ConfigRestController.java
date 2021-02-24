package com.virnect.serviceserver.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.application.ConfigService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class ConfigRestController {

    private static final String TAG = ConfigRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/config";

    private final ConfigService configService;

    /*protected HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }*/

    @GetMapping(value = "config")
    public ResponseEntity<String> getRemoteServiceConfiguration() {

        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServiceConfiguration"
        );

        ResponseEntity<String> responseData = configService.getRemoteServiceConfiguration();
        return responseData;
    }

    @GetMapping(value = "config/version")
    public String getRemoteServiceServerVersion() {

        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServiceServerVersion"
        );

        String responseData = configService.getRemoteServiceServerVersion();
        return responseData;
    }

    @GetMapping(value = "config/publicurl")
    public String getRemoteServicePublicUrl() {

        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServicePublicUrl"
        );

        String responseData = configService.getRemoteServicePublicUrl();
        return responseData;
    }

    @GetMapping(value = "config/recording")
    public Boolean getRemoteServiceRecordingEnabled() {

        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServicePublicUrl"
        );

        Boolean responseData = configService.getRemoteServiceRecordingEnabled();
        return responseData;
    }

    @GetMapping(value = "config/recording-path")
    public String getRemoteServiceRecordingPath() {

        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServiceRecordingPath"
        );

        String responseData = configService.getRemoteServiceRecordingPath();
        return responseData;
    }

    @GetMapping(value = "config/cdr")
    public Boolean getRemoteServiceCdrEnabled() {

        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServiceCdrEnabled"
        );

        Boolean responseData = configService.getRemoteServiceCdrEnabled();
        return responseData;
    }

}

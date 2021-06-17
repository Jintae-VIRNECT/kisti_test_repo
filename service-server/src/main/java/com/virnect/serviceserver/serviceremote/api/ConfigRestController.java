package com.virnect.serviceserver.serviceremote.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.infra.utils.LogMessage;
import com.virnect.serviceserver.serviceremote.application.ConfigService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class ConfigRestController {

    private static final String TAG = ConfigRestController.class.getSimpleName();
    private static final String REST_PATH = "/remote/config";

    private final ConfigService configService;

    @GetMapping(value = "config")
    public ResponseEntity<String> getRemoteServiceConfiguration() {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServiceConfiguration"
        );
        return configService.getRemoteServiceConfiguration();
    }

    @GetMapping(value = "config/version")
    public String getRemoteServiceServerVersion() {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServiceServerVersion"
        );
        return configService.getRemoteServiceServerVersion();
    }

    @GetMapping(value = "config/publicurl")
    public String getRemoteServicePublicUrl() {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServicePublicUrl"
        );
        return configService.getRemoteServicePublicUrl();
    }

    @GetMapping(value = "config/recording")
    public Boolean getRemoteServiceRecordingEnabled() {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServicePublicUrl"
        );
        return configService.getRemoteServiceRecordingEnabled();
    }

    @GetMapping(value = "config/recording-path")
    public String getRemoteServiceRecordingPath() {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServiceRecordingPath"
        );
        return configService.getRemoteServiceRecordingPath();
    }

    @GetMapping(value = "config/cdr")
    public Boolean getRemoteServiceCdrEnabled() {
        LogMessage.formedInfo(
            TAG,
            "REST API: GET " + REST_PATH,
            "getRemoteServiceCdrEnabled"
        );
        return configService.getRemoteServiceCdrEnabled();
    }

}

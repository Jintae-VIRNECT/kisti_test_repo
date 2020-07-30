package com.virnect.serviceserver.gateway.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.virnect.serviceserver.cdr.CDREventName;
import com.virnect.serviceserver.config.RemoteServiceBuildInfo;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.gateway.application.RemoteGatewayService;
import com.virnect.serviceserver.gateway.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.serviceserver.gateway.global.common.ApiResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class ServiceRestController {
    private static final String TAG = ServiceRestController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_SERVICE_PATH = "/remote/service";
    private static final String REST_CONFIG_PATH = "/remote/config";
    private final RemoteGatewayService remoteGatewayService;

   /* @ApiOperation(value = "Send Signal to a Session", notes = "특정 세션의 접속 사용자 에게 커스텀 데이터를 보내는 API 입니다.")
    @PostMapping(value = "signal/{workspaceId}/{sessionId}")
    public ResponseEntity<ApiResponse<WorkspaceMemberInfoListResponse>> getMembers(
            @PathVariable(name = "workspaceId") String workspaceId,
            @PathVariable(name = "sessionId") String sessionId) {

        log.info("REST API: GET {}/{}", REST_PATH, workspaceId != null ? workspaceId.toString() : "{}");

        ApiResponse<WorkspaceMemberInfoListResponse> apiResponse = this.remoteGatewayService.getMembers(workspaceId, filter, page, size);
        log.debug(TAG, apiResponse.toString());
        return ResponseEntity.ok(apiResponse);
    }*/

    private final RemoteServiceConfig remoteServiceConfig;
    private final RemoteServiceBuildInfo remoteServiceBuildInfo;

    protected HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    @GetMapping(value = "config")
    public ResponseEntity<String> getRemoteServiceConfiguration() {

        log.info("REST API: GET {}", REST_CONFIG_PATH);

        JsonObject json = new JsonObject();
        json.addProperty("version", remoteServiceBuildInfo.getVersion());
        json.addProperty("domain_or_public_ip", remoteServiceConfig.getDomainOrPublicIp());
        json.addProperty("https_port", remoteServiceConfig.getHttpsPort());
        json.addProperty("remote_service_publicurl", remoteServiceConfig.getRemoteServicePublicUrl());
        json.addProperty("remote_service_cdr", remoteServiceConfig.isCdrEnabled());
        json.addProperty("remote_service_streams_video_max_recv_bandwidth", remoteServiceConfig.getVideoMaxRecvBandwidth());
        json.addProperty("remote_service_streams_video_min_recv_bandwidth", remoteServiceConfig.getVideoMinRecvBandwidth());
        json.addProperty("remote_service_streams_video_max_send_bandwidth", remoteServiceConfig.getVideoMaxSendBandwidth());
        json.addProperty("remote_service_streams_video_min_send_bandwidth", remoteServiceConfig.getVideoMinSendBandwidth());
        json.addProperty("remote_service_sessions_garbage_interval", remoteServiceConfig.getSessionGarbageInterval());
        json.addProperty("remote_service_sessions_garbage_threshold", remoteServiceConfig.getSessionGarbageThreshold());
        json.addProperty("remote_service_recording", remoteServiceConfig.isRecordingModuleEnabled());
        if (remoteServiceConfig.isRecordingModuleEnabled()) {
            json.addProperty("remote_service_recording_version", remoteServiceConfig.getRemoteServiceRecordingVersion());
            json.addProperty("remote_service_recording_path", remoteServiceConfig.getRemoteServiceRecordingPath());
            json.addProperty("remote_service_recording_public_access", remoteServiceConfig.getRemoteServiceRecordingPublicAccess());
            json.addProperty("remote_service_recording_notification", remoteServiceConfig.getRemoteServiceRecordingNotification().name());
            json.addProperty("remote_service_recording_custom_layout", remoteServiceConfig.getRemoteServiceRecordingCustomLayout());
            json.addProperty("remote_service_recording_autostop_timeout", remoteServiceConfig.getRemoteServiceRecordingAutostopTimeout());
            if (remoteServiceConfig.getRemoteServiceRecordingComposedUrl() != null	&& !remoteServiceConfig.getRemoteServiceRecordingComposedUrl().isEmpty()) {
                json.addProperty("remote_service_recording_composed_url", remoteServiceConfig.getRemoteServiceRecordingComposedUrl());
            }
        }
        json.addProperty("remote_service_webhook", remoteServiceConfig.isWebhookEnabled());
        if (remoteServiceConfig.isWebhookEnabled()) {
            json.addProperty("remote_service_webhook_endpoint", remoteServiceConfig.getRemoteServiceWebhookEndpoint());
            JsonArray webhookHeaders = new JsonArray();
            for (Header header : remoteServiceConfig.getRemoteServiceWebhookHeaders()) {
                webhookHeaders.add(header.getName() + ": " + header.getValue());
            }
            json.add("remote_service_webhook_headers", webhookHeaders);
            JsonArray webhookEvents = new JsonArray();
            for (CDREventName eventName : remoteServiceConfig.getRemoteServiceWebhookEvents()) {
                webhookEvents.add(eventName.name());
            }
            json.add("remote_service_webhook_events", webhookEvents);
        }

        return new ResponseEntity<>(json.toString(), getResponseHeaders(), HttpStatus.OK);
    }

    @GetMapping(value = "config/version")
    public String getRemoteServiceServerVersion() {
        log.info("REST API: GET {}/version", REST_CONFIG_PATH);
        return remoteServiceBuildInfo.getRemoteServiceServerVersion();
    }

    @GetMapping(value = "config/publicurl")
    public String getRemoteServicePublicUrl() {
        log.info("REST API: GET {}/publicurl", REST_CONFIG_PATH);
        return remoteServiceConfig.getFinalUrl();
    }

    @GetMapping(value = "config/recording")
    public Boolean getRemoteServiceRecordingEnabled() {
        log.info("REST API: GET {}/recording", REST_CONFIG_PATH);
        return remoteServiceConfig.isRecordingModuleEnabled();
    }

    @GetMapping(value = "config/recording-path")
    public String getRemoteServiceRecordingPath() {
        log.info("REST API: GET {}/recording-path", REST_CONFIG_PATH);
        return remoteServiceConfig.getRemoteServiceRecordingPath();
    }

    @GetMapping(value = "config/cdr")
    public Boolean getRemoteServiceCdrEnabled() {
        log.info("REST API: GET {}/cdr", REST_CONFIG_PATH);
        return remoteServiceConfig.isCdrEnabled();
    }


}

package com.virnect.serviceserver.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.serviceserver.application.ConfigService;
import com.virnect.serviceserver.global.config.RemoteServiceBuildInfo;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/remote")
public class ConfigRestController {
    //private static final String TAG = ConfigRestController.class.getSimpleName();
    //private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    //private static final String REST_SERVICE_PATH = "/remote/service";
    //private static final String REST_CONFIG_PATH = "/remote/config";

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

    private final ConfigService configService;

    protected HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    @GetMapping(value = "config")
    public ResponseEntity<String> getRemoteServiceConfiguration() {

        ResponseEntity<String> responseData = configService.getRemoteServiceConfiguration();
        return responseData;

		/*log.info("REST API: GET {}", REST_CONFIG_PATH);

		JsonObject json = new JsonObject();
		json.addProperty("version", remoteServiceBuildInfo.getRemoteServiceServerVersion());
		json.addProperty("domain_or_public_ip", remoteServiceConfig.remoteServiceProperties.getDomainOrPublicIp());
		json.addProperty("https_port", remoteServiceConfig.remoteServiceProperties.getHttpsPort());
		json.addProperty(
			"remote_service_publicurl", remoteServiceConfig.remoteServiceProperties.getRemoteServicePublicUrl());
		json.addProperty("remote_service_cdr", remoteServiceConfig.isCdrEnabled());

		//BandwidthProperty
		MediaServerProperties mediaServerProperties = remoteServiceConfig.remoteServiceProperties.mediaServerProperties;
		json.addProperty(
			"remote_service_streams_video_max_recv_bandwidth",
			mediaServerProperties.bandwidthProperty.getStreamsVideoMaxRecvBandwidth()
		);
		json.addProperty(
			"remote_service_streams_video_min_recv_bandwidth",
			mediaServerProperties.bandwidthProperty.getStreamsVideoMinRecvBandwidth()
		);
		json.addProperty(
			"remote_service_streams_video_max_send_bandwidth",
			mediaServerProperties.bandwidthProperty.getStreamsVideoMaxSendBandwidth()
		);
		json.addProperty(
			"remote_service_streams_video_min_send_bandwidth",
			mediaServerProperties.bandwidthProperty.getStreamsVideoMinSendBandwidth()
		);

		json.addProperty(
			"remote_service_sessions_garbage_interval",
			mediaServerProperties.serverProperty.getSessionsGarbageInterval()
		);
		json.addProperty(
			"remote_service_sessions_garbage_threshold",
			mediaServerProperties.serverProperty.getSessionsGarbageThreshold()
		);

		//json.addProperty("remote_service_recording", remoteServiceConfig.remoteServiceProperties.isRecordingModuleEnabled());
        *//*if (remoteServiceConfig.remoteServiceProperties.isRecordingModuleEnabled()) {
            json.addProperty("remote_service_recording_version", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingVersion());
            json.addProperty("remote_service_recording_path", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPath());
            json.addProperty("remote_service_recording_public_access", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPublicAccess());
            json.addProperty("remote_service_recording_notification", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingNotification().name());
            json.addProperty("remote_service_recording_custom_layout", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingCustomLayout());
            json.addProperty("remote_service_recording_autostop_timeout", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingAutostopTimeout());
            if (remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingComposedUrl() != null	&& !remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingComposedUrl().isEmpty()) {
                json.addProperty("remote_service_recording_composed_url", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingComposedUrl());
            }
        }*//*
		json.addProperty("remote_service_webhook", remoteServiceConfig.isWebhookEnabled());
		if (remoteServiceConfig.isWebhookEnabled()) {
			json.addProperty(
				"remote_service_webhook_endpoint",
				remoteServiceConfig.remoteServiceProperties.getRemoteServiceWebhookEndpoint()
			);
			JsonArray webhookHeaders = new JsonArray();
			for (Header header : remoteServiceConfig.remoteServiceProperties.getRemoteServiceWebhookHeaders()) {
				webhookHeaders.add(header.getName() + ": " + header.getValue());
			}
			json.add("remote_service_webhook_headers", webhookHeaders);
			JsonArray webhookEvents = new JsonArray();
			for (CDREventName eventName : remoteServiceConfig.remoteServiceProperties.getRemoteServiceWebhookEvents()) {
				webhookEvents.add(eventName.name());
			}
			json.add("remote_service_webhook_events", webhookEvents);
		}

		return new ResponseEntity<>(json.toString(), getResponseHeaders(), HttpStatus.OK);*/
    }

    @GetMapping(value = "config/version")
    public String getRemoteServiceServerVersion() {
        String responseData = configService.getRemoteServiceServerVersion();
        return responseData;
        /*return remoteServiceBuildInfo.getRemoteServiceServerVersion();*/
    }

    @GetMapping(value = "config/publicurl")
    public String getRemoteServicePublicUrl() {
        String responseData = configService.getRemoteServicePublicUrl();
        return responseData;
		/*log.info("REST API: GET {}/publicurl", REST_CONFIG_PATH);
		return remoteServiceConfig.getFinalUrl();*/
    }

    @GetMapping(value = "config/recording")
    public Boolean getRemoteServiceRecordingEnabled() {
        Boolean responseData = configService.getRemoteServiceRecordingEnabled();
        return responseData;
		/*log.info("REST API: GET {}/recording", REST_CONFIG_PATH);
		//return remoteServiceConfig.isRecordingModuleEnabled();
		return true;*/
    }

    @GetMapping(value = "config/recording-path")
    public String getRemoteServiceRecordingPath() {
        String responseData = configService.getRemoteServiceRecordingPath();
        return responseData;
		/*log.info("REST API: GET {}/recording-path", REST_CONFIG_PATH);
		//return remoteServiceConfig.getRemoteServiceRecordingPath();
		return "";*/
    }

    @GetMapping(value = "config/cdr")
    public Boolean getRemoteServiceCdrEnabled() {
        Boolean responseData = configService.getRemoteServiceCdrEnabled();
        return responseData;
		/*log.info("REST API: GET {}/cdr", REST_CONFIG_PATH);
		return remoteServiceConfig.isCdrEnabled();*/
    }

}

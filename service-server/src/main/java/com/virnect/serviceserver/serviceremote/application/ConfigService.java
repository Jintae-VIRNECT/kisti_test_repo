package com.virnect.serviceserver.serviceremote.application;

import org.apache.http.Header;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.mediaserver.cdr.CDREventName;
import com.virnect.mediaserver.config.MediaServerProperties;
import com.virnect.serviceserver.global.config.RemoteServiceBuildInfo;
import com.virnect.serviceserver.global.config.RemoteServiceConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

	private static final String REST_CONFIG_PATH = "/remote/config";

	private final RemoteServiceConfig remoteServiceConfig;
	private final RemoteServiceBuildInfo remoteServiceBuildInfo;

	protected HttpHeaders getResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}

	public ResponseEntity<String> getRemoteServiceConfiguration() {
		log.info("REST API: GET {}", REST_CONFIG_PATH);

		JsonObject json = new JsonObject();
		json.addProperty("version", remoteServiceBuildInfo.getRemoteServiceServerVersion());
		json.addProperty("domain_or_public_ip", remoteServiceConfig.remoteServiceProperties.getDomainOrPublicIp());
		json.addProperty("https_port", remoteServiceConfig.remoteServiceProperties.getHttpsPort());
		json.addProperty(
			"remote_service_publicurl", remoteServiceConfig.remoteServiceProperties.getRemoteServicePublicUrl());
		json.addProperty("remote_service_cdr", remoteServiceConfig.remoteServiceProperties.isRemoteCdr());

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
        /*if (remoteServiceConfig.remoteServiceProperties.isRecordingModuleEnabled()) {
            json.addProperty("remote_service_recording_version", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingVersion());
            json.addProperty("remote_service_recording_path", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPath());
            json.addProperty("remote_service_recording_public_access", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPublicAccess());
            json.addProperty("remote_service_recording_notification", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingNotification().name());
            json.addProperty("remote_service_recording_custom_layout", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingCustomLayout());
            json.addProperty("remote_service_recording_autostop_timeout", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingAutostopTimeout());
            if (remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingComposedUrl() != null	&& !remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingComposedUrl().isEmpty()) {
                json.addProperty("remote_service_recording_composed_url", remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingComposedUrl());
            }
        }*/
		//json.addProperty("remote_service_webhook", remoteServiceConfig.isWebhookEnabled());
		/*if (remoteServiceConfig.isWebhookEnabled()) {
			json.addProperty(
				"remote_service_webhook_endpoint",
				remoteServiceConfig.remoteServiceProperties.getRemoteWebhookEndpoint()
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
		}*/

		return new ResponseEntity<>(json.toString(), getResponseHeaders(), HttpStatus.OK);
	}

	public String getRemoteServiceServerVersion() {
		return remoteServiceBuildInfo.getRemoteServiceServerVersion();
	}

	public String getRemoteServicePublicUrl() {
		return remoteServiceConfig.remoteServiceProperties.mediaServerProperties.getFinalUrl();
	}

	public Boolean getRemoteServiceRecordingEnabled() {
		//return remoteServiceConfig.isRecordingModuleEnabled();
		return true;
	}

	public String getRemoteServiceRecordingPath() {
		//return remoteServiceConfig.getRemoteServiceRecordingPath();
		return "";
	}

	public Boolean getRemoteServiceCdrEnabled() {
		return remoteServiceConfig.remoteServiceProperties.isRemoteCdr();
	}
}

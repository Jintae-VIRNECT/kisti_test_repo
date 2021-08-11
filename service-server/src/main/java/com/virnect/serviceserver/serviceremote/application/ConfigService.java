package com.virnect.serviceserver.serviceremote.application;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

/*
 * (C) Copyright 2017-2020 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.virnect.serviceserver.rest;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.virnect.mediaserver.cdr.CDREventName;
import com.virnect.serviceserver.config.RemoteServiceBuildInfo;
import com.virnect.serviceserver.config.RemoteServiceConfig;

/**
 *
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
@RestController
@CrossOrigin
@RequestMapping("/config")
public class ConfigRestController {

	private static final Logger log = LoggerFactory.getLogger(ConfigRestController.class);

	@Autowired
	private RemoteServiceConfig remoteServiceConfig;

	@Autowired
	private RemoteServiceBuildInfo remoteServiceBuildInfo;

	@RequestMapping(value = "/remoteservice-version", method = RequestMethod.GET)
	public String getRemoteServiceServerVersion() {

		log.info("REST API: GET /config/remoteservice-version");

		return remoteServiceBuildInfo.getRemoteServiceServerVersion();
	}

	@RequestMapping(value = "/remoteservice-publicurl", method = RequestMethod.GET)
	public String getRemoteServicePublicUrl() {

		log.info("REST API: GET /config/remoteservice-publicurl");

		return remoteServiceConfig.getFinalUrl();
	}

	@RequestMapping(value = "/remoteservice-cdr", method = RequestMethod.GET)
	public Boolean getRemoteServiceCdrEnabled() {

		log.info("REST API: GET /config/remoteservice-cdr");

		return remoteServiceConfig.isCdrEnabled();
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> getRemoteServiceConfiguration() {

		log.info("REST API: GET /config");

		JsonObject json = new JsonObject();
		/*json.addProperty("version", remoteServiceBuildInfo.getVersion());
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
			json.addProperty("remote_service_webhook_endpoint", remoteServiceConfig.remoteServiceProperties.getRemoteServiceWebhookEndpoint());
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

	protected HttpHeaders getResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}

}

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

package com.virnect.remoteserviceserver.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.virnect.remoteserviceserver.cdr.CDREventName;
import com.virnect.remoteserviceserver.config.RemoteServiceBuildInfo;
import com.virnect.remoteserviceserver.config.RemoteServiceConfig;
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

	@RequestMapping(value = "/openvidu-version", method = RequestMethod.GET)
	public String getOpenViduServerVersion() {

		log.info("REST API: GET /config/openvidu-version");

		return remoteServiceBuildInfo.getOpenViduServerVersion();
	}

	@RequestMapping(value = "/openvidu-publicurl", method = RequestMethod.GET)
	public String getOpenViduPublicUrl() {

		log.info("REST API: GET /config/openvidu-publicurl");

		return remoteServiceConfig.getFinalUrl();
	}

	@RequestMapping(value = "/openvidu-recording", method = RequestMethod.GET)
	public Boolean getOpenViduRecordingEnabled() {

		log.info("REST API: GET /config/openvidu-recording");

		return remoteServiceConfig.isRecordingModuleEnabled();
	}

	@RequestMapping(value = "/openvidu-recording-path", method = RequestMethod.GET)
	public String getOpenViduRecordingPath() {

		log.info("REST API: GET /config/openvidu-recording-path");

		return remoteServiceConfig.getOpenViduRecordingPath();
	}

	@RequestMapping(value = "/openvidu-cdr", method = RequestMethod.GET)
	public Boolean getOpenViduCdrEnabled() {

		log.info("REST API: GET /config/openvidu-cdr");

		return remoteServiceConfig.isCdrEnabled();
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> getOpenViduConfiguration() {

		log.info("REST API: GET /config");

		JsonObject json = new JsonObject();
		json.addProperty("VERSION", remoteServiceBuildInfo.getVersion());
		json.addProperty("DOMAIN_OR_PUBLIC_IP", remoteServiceConfig.getDomainOrPublicIp());
		json.addProperty("HTTPS_PORT", remoteServiceConfig.getHttpsPort());
		json.addProperty("OPENVIDU_PUBLICURL", remoteServiceConfig.getOpenViduPublicUrl());
		json.addProperty("OPENVIDU_CDR", remoteServiceConfig.isCdrEnabled());
		json.addProperty("OPENVIDU_STREAMS_VIDEO_MAX_RECV_BANDWIDTH", remoteServiceConfig.getVideoMaxRecvBandwidth());
		json.addProperty("OPENVIDU_STREAMS_VIDEO_MIN_RECV_BANDWIDTH", remoteServiceConfig.getVideoMinRecvBandwidth());
		json.addProperty("OPENVIDU_STREAMS_VIDEO_MAX_SEND_BANDWIDTH", remoteServiceConfig.getVideoMaxSendBandwidth());
		json.addProperty("OPENVIDU_STREAMS_VIDEO_MIN_SEND_BANDWIDTH", remoteServiceConfig.getVideoMinSendBandwidth());
		json.addProperty("OPENVIDU_SESSIONS_GARBAGE_INTERVAL", remoteServiceConfig.getSessionGarbageInterval());
		json.addProperty("OPENVIDU_SESSIONS_GARBAGE_THRESHOLD", remoteServiceConfig.getSessionGarbageThreshold());
		json.addProperty("OPENVIDU_RECORDING", remoteServiceConfig.isRecordingModuleEnabled());
		if (remoteServiceConfig.isRecordingModuleEnabled()) {
			json.addProperty("OPENVIDU_RECORDING_VERSION", remoteServiceConfig.getOpenViduRecordingVersion());
			json.addProperty("OPENVIDU_RECORDING_PATH", remoteServiceConfig.getOpenViduRecordingPath());
			json.addProperty("OPENVIDU_RECORDING_PUBLIC_ACCESS", remoteServiceConfig.getOpenViduRecordingPublicAccess());
			json.addProperty("OPENVIDU_RECORDING_NOTIFICATION",
					remoteServiceConfig.getOpenViduRecordingNotification().name());
			json.addProperty("OPENVIDU_RECORDING_CUSTOM_LAYOUT", remoteServiceConfig.getOpenviduRecordingCustomLayout());
			json.addProperty("OPENVIDU_RECORDING_AUTOSTOP_TIMEOUT",
					remoteServiceConfig.getOpenviduRecordingAutostopTimeout());
			if (remoteServiceConfig.getOpenViduRecordingComposedUrl() != null
					&& !remoteServiceConfig.getOpenViduRecordingComposedUrl().isEmpty()) {
				json.addProperty("OPENVIDU_RECORDING_COMPOSED_URL", remoteServiceConfig.getOpenViduRecordingComposedUrl());
			}
		}
		json.addProperty("OPENVIDU_WEBHOOK", remoteServiceConfig.isWebhookEnabled());
		if (remoteServiceConfig.isWebhookEnabled()) {
			json.addProperty("OPENVIDU_WEBHOOK_ENDPOINT", remoteServiceConfig.getOpenViduWebhookEndpoint());
			JsonArray webhookHeaders = new JsonArray();
			for (Header header : remoteServiceConfig.getOpenViduWebhookHeaders()) {
				webhookHeaders.add(header.getName() + ": " + header.getValue());
			}
			json.add("OPENVIDU_WEBHOOK_HEADERS", webhookHeaders);
			JsonArray webhookEvents = new JsonArray();
			for (CDREventName eventName : remoteServiceConfig.getOpenViduWebhookEvents()) {
				webhookEvents.add(eventName.name());
			}
			json.add("OPENVIDU_WEBHOOK_EVENTS", webhookEvents);
		}

		return new ResponseEntity<>(json.toString(), getResponseHeaders(), HttpStatus.OK);
	}

	protected HttpHeaders getResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}

}

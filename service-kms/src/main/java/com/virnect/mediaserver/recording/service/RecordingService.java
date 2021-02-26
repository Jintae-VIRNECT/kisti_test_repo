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

package com.virnect.mediaserver.recording.service;

import com.virnect.client.RemoteServiceException;
import com.virnect.client.RemoteServiceException.Code;
import com.virnect.java.client.RecordingLayout;
import com.virnect.java.client.RecordingProperties;
import com.virnect.mediaserver.cdr.CallDetailRecord;
import com.virnect.mediaserver.config.MediaServerProperties;
import com.virnect.mediaserver.core.EndReason;
import com.virnect.mediaserver.core.Session;
import com.virnect.mediaserver.recording.Recording;
import com.virnect.mediaserver.recording.RecordingDownloader;
import com.virnect.mediaserver.utils.CommandExecutor;
import com.virnect.mediaserver.utils.CustomFileManager;
import com.virnect.mediaserver.utils.QuarantineKiller;
import com.virnect.mediaserver.utils.RecordingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class RecordingService {

	private static final Logger log = LoggerFactory.getLogger(RecordingService.class);

	protected MediaServerProperties mediaServerProperties;
	protected RecordingManager recordingManager;
	protected RecordingDownloader recordingDownloader;
	protected CallDetailRecord cdr;
	protected QuarantineKiller quarantineKiller;
	protected CustomFileManager fileWriter = new CustomFileManager();

	RecordingService(RecordingManager recordingManager, RecordingDownloader recordingDownloader,
					 MediaServerProperties mediaServerProperties, CallDetailRecord cdr, QuarantineKiller quarantineKiller) {
		this.recordingManager = recordingManager;
		this.recordingDownloader = recordingDownloader;
		this.mediaServerProperties = mediaServerProperties;
		this.cdr = cdr;
		this.quarantineKiller = quarantineKiller;
	}

	public abstract Recording startRecording(Session session, RecordingProperties properties) throws RemoteServiceException;

	public abstract Recording stopRecording(Session session, Recording recording, EndReason reason);

	/**
	 * Generates metadata recording file (".recording.RECORDING_ID" JSON file to
	 * store Recording entity)
	 */
	protected void generateRecordingMetadataFile(Recording recording) {
		String folder = this.mediaServerProperties.recordingProperty.getRecordingPath() + recording.getId();
		boolean newFolderCreated = this.fileWriter.createFolderIfNotExists(folder);

		if (newFolderCreated) {
			log.warn(
					"New folder {} created. This means A) Cluster mode is enabled B) The recording started for a session with no publishers or C) No media type compatible publishers",
					folder);
		} else {
			log.info("Folder {} already existed. Some publisher is already being recorded", folder);
		}

		String filePath = this.mediaServerProperties.recordingProperty.getRecordingPath() + recording.getId() + "/"
				+ RecordingManager.RECORDING_ENTITY_FILE + recording.getId();
		String text = recording.toJson().toString();
		this.fileWriter.createAndWriteFile(filePath, text);
		log.info("Generated recording metadata file at {}", filePath);
	}

	/**
	 * Update and overwrites metadata recording file to set it in "stopped" status.
	 * Recording size and duration will remain as 0
	 * 
	 * @return updated Recording object
	 */
	protected Recording sealRecordingMetadataFileAsStopped(Recording recording) {
		final String entityFile = this.mediaServerProperties.recordingProperty.getRecordingPath() + recording.getId() + "/"
				+ RecordingManager.RECORDING_ENTITY_FILE + recording.getId();
		return this.sealRecordingMetadataFile(recording, 0, 0, com.virnect.java.client.Recording.Status.stopped,
				entityFile);
	}

	/**
	 * Update and overwrites metadata recording file to set it in "ready" (or
	 * "failed") status
	 * 
	 * @return updated Recording object
	 */
	protected Recording sealRecordingMetadataFileAsReady(Recording recording, long size, double duration,
			String metadataFilePath) {
		com.virnect.java.client.Recording.Status status = com.virnect.java.client.Recording.Status.failed
				.equals(recording.getStatus()) ? com.virnect.java.client.Recording.Status.failed
						: com.virnect.java.client.Recording.Status.ready;

		// Status is now failed or ready. Url property must be defined
		recording.setUrl(recordingManager.getRecordingUrl(recording));

		final String entityFile = this.mediaServerProperties.recordingProperty.getRecordingPath() + recording.getId() + "/"
				+ RecordingManager.RECORDING_ENTITY_FILE + recording.getId();
		return this.sealRecordingMetadataFile(recording, size, duration, status, entityFile);
	}

	private Recording sealRecordingMetadataFile(Recording recording, long size, double duration,
												com.virnect.java.client.Recording.Status status, String metadataFilePath) {
		recording.setStatus(status);
		recording.setSize(size); // Size in bytes
		recording.setDuration(duration > 0 ? duration : 0); // Duration in seconds

		if (this.fileWriter.overwriteFile(metadataFilePath, recording.toJson().toString())) {
			log.info("Sealed recording metadata file at {} with status [{}]", metadataFilePath, status.name());
		}

		return recording;
	}

	/**
	 * Returns a new available recording identifier (adding a number tag at the end
	 * of the sessionId if it already exists) and rebuilds RecordingProperties
	 * object to set the final value of "name" property
	 */
	protected PropertiesRecordingId setFinalRecordingNameAndGetFreeRecordingId(Session session,
			RecordingProperties properties) {
		String recordingId = this.recordingManager.getFreeRecordingId(session.getSessionId());
		if (properties.name() == null || properties.name().isEmpty()) {
			// No name provided for the recording file. Use recordingId
			RecordingProperties.Builder builder = new RecordingProperties.Builder().name(recordingId)
					.outputMode(properties.outputMode()).hasAudio(properties.hasAudio())
					.hasVideo(properties.hasVideo());
			if (RecordingUtils.IS_COMPOSED(properties.outputMode()) && properties.hasVideo()) {
				builder.resolution(properties.resolution());
				builder.recordingLayout(properties.recordingLayout());
				if (RecordingLayout.CUSTOM.equals(properties.recordingLayout())) {
					builder.customLayout(properties.customLayout());
				}
				builder.shmSize(properties.shmSize());
			}
			properties = builder.build();
		}

		log.info("New recording id ({}) and final name ({})", recordingId, properties.name());
		return new PropertiesRecordingId(properties, recordingId);
	}

	protected void updateFilePermissions(String folder) {
		String command = "chmod -R 777 " + folder;
		try {
			String response = CommandExecutor.execCommand(5000, "/bin/sh", "-c", command);
			if ("".equals(response)) {
				log.info("KMS recording file permissions successfully updated");
			} else {
				log.error("KMS recording file permissions failed to update: {}", response);
			}
		} catch (IOException | InterruptedException e) {
			log.error("KMS recording file permissions failed to update. Error: {}", e.getMessage());
		}
	}

	protected RemoteServiceException failStartRecording(Session session, Recording recording, String errorMessage) {
		log.error("Recording start failed for session {}: {}", session.getSessionId(), errorMessage);
		recording.setStatus(com.virnect.java.client.Recording.Status.failed);
		this.recordingManager.startingRecordings.remove(recording.getId());
		this.recordingManager.sessionsRecordingsStarting.remove(session.getSessionId());
		this.stopRecording(session, recording, null);
		return new RemoteServiceException(Code.RECORDING_START_ERROR_CODE, errorMessage);
	}

	protected void cleanRecordingMaps(Recording recording) {
		this.recordingManager.sessionsRecordings.remove(recording.getSessionId());
		this.recordingManager.startedRecordings.remove(recording.getId());
	}

	/**
	 * Simple wrapper for returning update RecordingProperties and a free
	 * recordingId when starting a new recording
	 */
	protected class PropertiesRecordingId {

		RecordingProperties properties;
		String recordingId;

		PropertiesRecordingId(RecordingProperties properties, String recordingId) {
			this.properties = properties;
			this.recordingId = recordingId;
		}
	}

}

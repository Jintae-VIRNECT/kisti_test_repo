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

package com.virnect.serviceserver.recording.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;

import com.virnect.client.RemoteServiceException;
import com.virnect.client.RemoteServiceException.Code;
import com.virnect.java.client.RecordingLayout;
import com.virnect.java.client.RecordingProperties;
import com.virnect.serviceserver.cdr.CallDetailRecord;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.core.EndReason;
import com.virnect.serviceserver.core.Participant;
import com.virnect.serviceserver.core.Session;
import com.virnect.serviceserver.kurento.core.KurentoParticipant;
import com.virnect.serviceserver.kurento.core.KurentoSession;
import com.virnect.serviceserver.recording.CompositeWrapper;
import com.virnect.serviceserver.recording.Recording;
import com.virnect.serviceserver.recording.RecordingDownloader;
import com.virnect.serviceserver.recording.RecordingInfoUtils;
import com.virnect.serviceserver.utils.DockerManager;
import com.virnect.serviceserver.utils.QuarantineKiller;

public class ComposedRecordingService extends RecordingService {

	private static final Logger log = LoggerFactory.getLogger(ComposedRecordingService.class);

	protected Map<String, String> containers = new ConcurrentHashMap<>();
	protected Map<String, String> sessionsContainers = new ConcurrentHashMap<>();
	private Map<String, CompositeWrapper> composites = new ConcurrentHashMap<>();

	protected DockerManager dockerManager;

	public ComposedRecordingService(RecordingManager recordingManager, RecordingDownloader recordingDownloader,
									RemoteServiceConfig remoteServiceConfig, CallDetailRecord cdr, QuarantineKiller quarantineKiller) {
		super(recordingManager, recordingDownloader, remoteServiceConfig, cdr, quarantineKiller);
		this.dockerManager = new DockerManager();
	}

	@Override
	public Recording startRecording(Session session, RecordingProperties properties) throws RemoteServiceException {

		PropertiesRecordingId updatePropertiesAndRecordingId = this.setFinalRecordingNameAndGetFreeRecordingId(session,
				properties);
		properties = updatePropertiesAndRecordingId.properties;
		String recordingId = updatePropertiesAndRecordingId.recordingId;

		// Instantiate and store recording object
		Recording recording = new Recording(session.getSessionId(), recordingId, properties);
		this.recordingManager.recordingToStarting(recording);

		if (properties.hasVideo()) {
			// Docker container used
			recording = this.startRecordingWithVideo(session, recording, properties);
		} else {
			// Kurento composite used
			recording = this.startRecordingAudioOnly(session, recording, properties);
		}

		// Increment active recordings
		// ((KurentoSession) session).getKms().getActiveRecordings().incrementAndGet();

		return recording;
	}

	@Override
	public Recording stopRecording(Session session, Recording recording, EndReason reason) {
		recording = this.sealRecordingMetadataFileAsStopped(recording);
		if (recording.hasVideo()) {
			return this.stopRecordingWithVideo(session, recording, reason);
		} else {
			return this.stopRecordingAudioOnly(session, recording, reason, 0);
		}
	}

	public Recording stopRecording(Session session, Recording recording, EndReason reason, long kmsDisconnectionTime) {
		if (recording.hasVideo()) {
			return this.stopRecordingWithVideo(session, recording, reason);
		} else {
			return this.stopRecordingAudioOnly(session, recording, reason, kmsDisconnectionTime);
		}
	}

	public void joinPublisherEndpointToComposite(Session session, String recordingId, Participant participant)
			throws RemoteServiceException {
		log.info("Joining single stream {} to Composite in session {}", participant.getPublisherStreamId(),
				session.getSessionId());

		KurentoParticipant kurentoParticipant = (KurentoParticipant) participant;
		CompositeWrapper compositeWrapper = this.composites.get(session.getSessionId());

		try {
			compositeWrapper.connectPublisherEndpoint(kurentoParticipant.getPublisher());
		} catch (RemoteServiceException e) {
			if (Code.RECORDING_START_ERROR_CODE.getValue() == e.getCodeValue()) {
				// First user publishing triggered RecorderEnpoint start, but it failed
				throw e;
			}
		}
	}

	public void removePublisherEndpointFromComposite(String sessionId, String streamId) {
		CompositeWrapper compositeWrapper = this.composites.get(sessionId);
		compositeWrapper.disconnectPublisherEndpoint(streamId);
	}

	protected Recording startRecordingWithVideo(Session session, Recording recording, RecordingProperties properties)
			throws RemoteServiceException {

		log.info("Starting composed ({}) recording {} of session {}",
				properties.hasAudio() ? "video + audio" : "audio-only", recording.getId(), recording.getSessionId());

		List<String> envs = new ArrayList<>();

		String layoutUrl = this.getLayoutUrl(recording);

		envs.add("DEBUG_MODE=" + remoteServiceConfig.remoteServiceProperties.isRemoteServiceRecordingDebug());
		envs.add("URL=" + layoutUrl);
		envs.add("ONLY_VIDEO=" + !properties.hasAudio());
		envs.add("RESOLUTION=" + properties.resolution());
		envs.add("FRAMERATE=30");
		envs.add("VIDEO_ID=" + recording.getId());
		envs.add("VIDEO_NAME=" + properties.name());
		envs.add("VIDEO_FORMAT=mp4");
		envs.add("RECORDING_JSON=" + recording.toJson().toString());

		log.info(recording.toJson().toString());
		log.info("Recorder connecting to url {}", layoutUrl);

		String containerId;
		try {
			final String container = RecordingManager.IMAGE_NAME + ":" + RecordingManager.IMAGE_TAG;
			final String containerName = "recording_" + recording.getId();
			Volume volume1 = new Volume("/recordings");
			List<Volume> volumes = new ArrayList<>();
			volumes.add(volume1);
			Bind bind1 = new Bind(remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPath(), volume1);
			List<Bind> binds = new ArrayList<>();
			binds.add(bind1);
			containerId = dockerManager.runContainer(container, containerName, null, volumes, binds, "host", envs, null,
					properties.shmSize(), false, null);
			containers.put(containerId, containerName);
		} catch (Exception e) {
			this.cleanRecordingMaps(recording);
			throw this.failStartRecording(session, recording,
					"Couldn't initialize recording container. Error: " + e.getMessage());
		}

		this.sessionsContainers.put(session.getSessionId(), containerId);

		try {
			this.waitForVideoFileNotEmpty(recording);
		} catch (RemoteServiceException e) {
			this.cleanRecordingMaps(recording);
			throw this.failStartRecording(session, recording,
					"Couldn't initialize recording container. Error: " + e.getMessage());
		}

		return recording;
	}

	private Recording startRecordingAudioOnly(Session session, Recording recording, RecordingProperties properties)
			throws RemoteServiceException {

		log.info("Starting composed (audio-only) recording {} of session {}", recording.getId(),
				recording.getSessionId());

		CompositeWrapper compositeWrapper = new CompositeWrapper((KurentoSession) session,
				"file://" + this.remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPath() + recording.getId() + "/" + properties.name()
						+ ".webm");
		this.composites.put(session.getSessionId(), compositeWrapper);

		for (Participant p : session.getParticipants()) {
			if (p.isStreaming()) {
				try {
					this.joinPublisherEndpointToComposite(session, recording.getId(), p);
				} catch (RemoteServiceException e) {
					log.error("Error waiting for RecorderEndpooint of Composite to start in session {}",
							session.getSessionId());
					throw this.failStartRecording(session, recording, e.getMessage());
				}
			}
		}

		this.generateRecordingMetadataFile(recording);

		// Increment active recordings
		((KurentoSession) session).getKms().getActiveRecordings().incrementAndGet();

		return recording;
	}

	protected Recording stopRecordingWithVideo(Session session, Recording recording, EndReason reason) {

		log.info("Stopping composed ({}) recording {} of session {}. Reason: {}",
				recording.hasAudio() ? "video + audio" : "audio-only", recording.getId(), recording.getSessionId(),
				RecordingManager.finalReason(reason));

		String containerId = this.sessionsContainers.remove(recording.getSessionId());

		final String recordingId = recording.getId();

		if (session == null) {
			log.warn(
					"Existing recording {} does not have an active session associated. This usually means a custom recording"
							+ " layout did not join a recorded participant or the recording has been automatically"
							+ " stopped after last user left and timeout passed",
					recording.getId());
		}

		if (containerId == null) {
			if (this.recordingManager.startingRecordings.containsKey(recordingId)) {

				// Session was closed while recording container was initializing
				// Wait until containerId is available and force its stop and deletion
				final Recording recordingAux = recording;
				new Thread(() -> {
					log.warn("Session closed while starting recording container");
					boolean containerClosed = false;
					String containerIdAux;
					int i = 0;
					final int timeout = 30;
					while (!containerClosed && (i < timeout)) {
						containerIdAux = this.sessionsContainers.remove(session.getSessionId());
						if (containerIdAux == null) {
							try {
								log.warn("Waiting for container to be launched...");
								i++;
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							log.warn("Removing container {} for closed session {}...", containerIdAux,
									session.getSessionId());
							dockerManager.removeDockerContainer(containerIdAux, true);
							containers.remove(containerId);
							containerClosed = true;
							log.warn("Container {} for closed session {} succesfully stopped and removed",
									containerIdAux, session.getSessionId());
							log.warn("Deleting unusable files for recording {}", recordingId);
							if (HttpStatus.NO_CONTENT
									.equals(this.recordingManager.deleteRecordingFromHost(recordingId, true))) {
								log.warn("Files properly deleted");
							}
						}
					}
					cleanRecordingMaps(recordingAux);
					if (i == timeout) {
						log.error("Container did not launched in {} seconds", timeout / 2);
						return;
					}
					// Decrement active recordings
					// ((KurentoSession) session).getKms().getActiveRecordings().decrementAndGet();
				}).start();
			}
		} else {

			stopAndRemoveRecordingContainer(recording, containerId, 30);
			recording = updateRecordingAttributes(recording);

			final String folderPath = this.remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPath() + recording.getId() + "/";
			final String metadataFilePath = folderPath + RecordingManager.RECORDING_ENTITY_FILE + recording.getId();
			this.sealRecordingMetadataFileAsReady(recording, recording.getSize(), recording.getDuration(),
					metadataFilePath);
			cleanRecordingMaps(recording);

			final long timestamp = System.currentTimeMillis();
			this.cdr.recordRecordingStatusChanged(recording, reason, timestamp, recording.getStatus());

			if (session != null && reason != null) {
				this.recordingManager.sessionHandler.sendRecordingStoppedNotification(session, recording, reason);
			}

			// Decrement active recordings
			// ((KurentoSession) session).getKms().getActiveRecordings().decrementAndGet();
		}

		return recording;
	}

	private Recording stopRecordingAudioOnly(Session session, Recording recording, EndReason reason,
			long kmsDisconnectionTime) {

		log.info("Stopping composed (audio-only) recording {} of session {}. Reason: {}", recording.getId(),
				recording.getSessionId(), reason);

		String sessionId;
		if (session == null) {
			log.warn(
					"Existing recording {} does not have an active session associated. This means the recording "
							+ "has been automatically stopped after last user left and {} seconds timeout passed",
					recording.getId(), this.remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingAutostopTimeout());
			sessionId = recording.getSessionId();
		} else {
			sessionId = session.getSessionId();
		}

		CompositeWrapper compositeWrapper = this.composites.remove(sessionId);
		final CountDownLatch stoppedCountDown = new CountDownLatch(1);
		compositeWrapper.stopCompositeRecording(stoppedCountDown, kmsDisconnectionTime);

		try {
			if (!stoppedCountDown.await(5, TimeUnit.SECONDS)) {
				recording.setStatus(com.virnect.java.client.Recording.Status.failed);
				log.error("Error waiting for RecorderEndpoint of Composite to stop in session {}",
						recording.getSessionId());
			}
		} catch (InterruptedException e) {
			recording.setStatus(com.virnect.java.client.Recording.Status.failed);
			log.error("Exception while waiting for state change", e);
		}

		compositeWrapper.disconnectAllPublisherEndpoints();

		this.cleanRecordingMaps(recording);

		final Recording[] finalRecordingArray = new Recording[1];
		finalRecordingArray[0] = recording;
		try {
			this.recordingDownloader.downloadRecording(finalRecordingArray[0], null, () -> {
				String filesPath = this.remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPath() + finalRecordingArray[0].getId()
						+ "/";
				File videoFile = new File(filesPath + finalRecordingArray[0].getName() + ".webm");
				long finalSize = videoFile.length();
				double finalDuration = (double) compositeWrapper.getDuration() / 1000;
				this.updateFilePermissions(filesPath);
				finalRecordingArray[0] = this.sealRecordingMetadataFileAsReady(finalRecordingArray[0], finalSize,
						finalDuration,
						filesPath + RecordingManager.RECORDING_ENTITY_FILE + finalRecordingArray[0].getId());

				final long timestamp = System.currentTimeMillis();
				cdr.recordRecordingStatusChanged(finalRecordingArray[0], reason, timestamp,
						finalRecordingArray[0].getStatus());

				// Decrement active recordings once it is downloaded
				((KurentoSession) session).getKms().getActiveRecordings().decrementAndGet();

				// Now we can drop Media Node if waiting-idle-to-terminate
				this.quarantineKiller.dropMediaNode(session.getMediaNodeId());

			});
		} catch (IOException e) {
			log.error("Error while downloading recording {}: {}", finalRecordingArray[0].getName(), e.getMessage());
		}

		if (reason != null && session != null) {
			this.recordingManager.sessionHandler.sendRecordingStoppedNotification(session, finalRecordingArray[0],
					reason);
		}

		return finalRecordingArray[0];
	}

	protected void stopAndRemoveRecordingContainer(Recording recording, String containerId, int secondsOfWait) {
		// Gracefully stop ffmpeg process
		try {
			dockerManager.runCommandInContainer(containerId, "echo 'q' > stop", 0);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// Wait for the container to be gracefully self-stopped
		final int timeOfWait = 30;
		try {
			dockerManager.waitForContainerStopped(containerId, timeOfWait);
		} catch (Exception e) {
			failRecordingCompletion(recording, containerId, new RemoteServiceException(Code.RECORDING_COMPLETION_ERROR_CODE,
					"The recording completion process couldn't finish in " + timeOfWait + " seconds"));
		}

		// Remove container
		dockerManager.removeDockerContainer(containerId, false);
		containers.remove(containerId);
	}

	protected Recording updateRecordingAttributes(Recording recording) {
		try {
			RecordingInfoUtils infoUtils = new RecordingInfoUtils(this.remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPath()
					+ recording.getId() + "/" + recording.getId() + ".info");

			if (!infoUtils.hasVideo()) {
				log.error("COMPOSED recording {} with hasVideo=true has not video track", recording.getId());
				recording.setStatus(com.virnect.java.client.Recording.Status.failed);
			} else {
				recording.setStatus(com.virnect.java.client.Recording.Status.ready);
				recording.setDuration(infoUtils.getDurationInSeconds());
				recording.setSize(infoUtils.getSizeInBytes());
				recording.setResolution(infoUtils.videoWidth() + "x" + infoUtils.videoHeight());
				recording.setHasAudio(infoUtils.hasAudio());
				recording.setHasVideo(infoUtils.hasVideo());
			}
			infoUtils.deleteFilePath();
			return recording;
		} catch (IOException e) {
			recording.setStatus(com.virnect.java.client.Recording.Status.failed);
			throw new RemoteServiceException(Code.RECORDING_REPORT_ERROR_CODE,
					"There was an error generating the metadata report file for the recording: " + e.getMessage());
		}
	}

	protected void waitForVideoFileNotEmpty(Recording recording) throws RemoteServiceException {
		boolean isPresent = false;
		int i = 1;
		int timeout = 150; // Wait for 150*150 = 22500 = 22.5 seconds
		while (!isPresent && timeout <= 150) {
			try {
				Thread.sleep(150);
				timeout++;
				File f = new File(this.remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingPath() + recording.getId() + "/"
						+ recording.getName() + ".mp4");
				isPresent = ((f.isFile()) && (f.length() > 0));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (i == timeout) {
			log.error("Recorder container failed generating video file (is empty) for session {}",
					recording.getSessionId());
			throw new RemoteServiceException(Code.RECORDING_START_ERROR_CODE,
					"Recorder container failed generating video file (is empty)");
		}
	}

	protected void failRecordingCompletion(Recording recording, String containerId, RemoteServiceException e)
			throws RemoteServiceException {
		recording.setStatus(com.virnect.java.client.Recording.Status.failed);
		dockerManager.removeDockerContainer(containerId, true);
		containers.remove(containerId);
		throw e;
	}

	protected String getLayoutUrl(Recording recording) throws RemoteServiceException {
		String secret = remoteServiceConfig.remoteServiceProperties.getRemoteServiceSecret();

		// Check if "customLayout" property defines a final URL
		if (RecordingLayout.CUSTOM.equals(recording.getRecordingLayout())) {
			String layout = recording.getCustomLayout();
			if (!layout.isEmpty()) {
				try {
					URL url = new URL(layout);
					log.info("\"customLayout\" property has a URL format ({}). Using it to connect to custom layout",
							url.toString());
					return this.processCustomLayoutUrlFormat(url, recording.getSessionId());
				} catch (MalformedURLException e) {
					String layoutPath = remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingCustomLayout() + layout;
					layoutPath = layoutPath.endsWith("/") ? layoutPath : (layoutPath + "/");
					log.info(
							"\"customLayout\" property is defined as \"{}\". Using a different custom layout than the default one. Expected path: {}",
							layout, layoutPath + "index.html");
					try {
						final File indexHtml = new File(layoutPath + "index.html");
						if (!indexHtml.exists()) {
							throw new IOException();
						}
						log.info("Custom layout path \"{}\" is valid. Found file {}", layout,
								indexHtml.getAbsolutePath());
					} catch (IOException e1) {
						final String error = "Custom layout path " + layout + " is not valid. Expected file "
								+ layoutPath + "index.html to exist and be readable";
						log.error(error);
						throw new RemoteServiceException(Code.RECORDING_PATH_NOT_VALID, error);
					}
				}
			}
		}

		boolean recordingComposedUrlDefined = remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingComposedUrl() != null
				&& !remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingComposedUrl().isEmpty();
		String recordingUrl = recordingComposedUrlDefined ? remoteServiceConfig.remoteServiceProperties.getRemoteServiceRecordingComposedUrl()
				: remoteServiceConfig.getFinalUrl();
		recordingUrl = recordingUrl.replaceFirst("https://", "");
		boolean startsWithHttp = recordingUrl.startsWith("http://");

		if (startsWithHttp) {
			recordingUrl = recordingUrl.replaceFirst("http://", "");
		}

		if (recordingUrl.endsWith("/")) {
			recordingUrl = recordingUrl.substring(0, recordingUrl.length() - 1);
		}

		String layout, finalUrl;
		if (RecordingLayout.CUSTOM.equals(recording.getRecordingLayout())) {
			layout = recording.getCustomLayout();
			if (!layout.isEmpty()) {
				layout = layout.startsWith("/") ? layout : ("/" + layout);
				layout = layout.endsWith("/") ? layout.substring(0, layout.length() - 1) : layout;
			}
			layout += "/index.html";
			finalUrl = (startsWithHttp ? "http" : "https") + "://OPENVIDUAPP:" + secret + "@" + recordingUrl
					+ "/layouts/custom" + layout + "?sessionId=" + recording.getSessionId() + "&secret=" + secret;
			/*finalUrl = (startsWithHttp ? "http" : "https") + "://remote:" + secret + "@" + recordingUrl
					+ "/layouts/custom" + layout + "?sessionId=" + recording.getSessionId() + "&secret=" + secret;*/
		} else {
			layout = recording.getRecordingLayout().name().toLowerCase().replaceAll("_", "-");
			int port = startsWithHttp ? 80 : 443;
			try {
				port = new URL(remoteServiceConfig.getFinalUrl()).getPort();
			} catch (MalformedURLException e) {
				log.error(e.getMessage());
			}
			String defaultPathForDefaultLayout = recordingComposedUrlDefined ? ""
					: ("/" + remoteServiceConfig.getRemoteServiceFrontendDefaultPath());

			finalUrl = (startsWithHttp ? "http" : "https") + "://OPENVIDUAPP:" + secret + "@" + recordingUrl
					+ defaultPathForDefaultLayout + "/#/layout-" + layout + "/" + recording.getSessionId() + "/"
					+ secret + "/" + port + "/" + !recording.hasAudio();
			/*finalUrl = (startsWithHttp ? "http" : "https") + "://remote:" + secret + "@" + recordingUrl
					+ defaultPathForDefaultLayout + "/#/layout-" + layout + "/" + recording.getSessionId() + "/"
					+ secret + "/" + port + "/" + !recording.hasAudio();*/
		}

		return finalUrl;
	}

	private String processCustomLayoutUrlFormat(URL url, String shortSessionId) {
		String finalUrl = url.getProtocol() + "://" + url.getAuthority();
		if (!url.getPath().isEmpty()) {
			finalUrl += url.getPath();
		}
		finalUrl = finalUrl.endsWith("/") ? finalUrl.substring(0, finalUrl.length() - 1) : finalUrl;
		if (url.getQuery() != null) {
			URI uri;
			try {
				uri = url.toURI();
				finalUrl += "?";
			} catch (URISyntaxException e) {
				String error = "\"customLayout\" property has URL format and query params (" + url.toString()
						+ "), but does not comply with RFC2396 URI format";
				log.error(error);
				throw new RemoteServiceException(Code.RECORDING_PATH_NOT_VALID, error);
			}
			List<NameValuePair> params = URLEncodedUtils.parse(uri, Charset.forName("UTF-8"));
			Iterator<NameValuePair> it = params.iterator();
			boolean hasSessionId = false;
			boolean hasSecret = false;
			while (it.hasNext()) {
				NameValuePair param = it.next();
				finalUrl += param.getName() + "=" + param.getValue();
				if (it.hasNext()) {
					finalUrl += "&";
				}
				if (!hasSessionId) {
					hasSessionId = param.getName().equals("sessionId");
				}
				if (!hasSecret) {
					hasSecret = param.getName().equals("secret");
				}
			}
			if (!hasSessionId) {
				finalUrl += "&sessionId=" + shortSessionId;
			}
			if (!hasSecret) {
				finalUrl += "&secret=" + remoteServiceConfig.remoteServiceProperties.getRemoteServiceSecret();
			}
		}

		if (url.getRef() != null) {
			finalUrl += "#" + url.getRef();
		}

		return finalUrl;
	}

}

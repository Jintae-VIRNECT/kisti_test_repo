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

package com.virnect.java.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class RemoteService {

	private static final Logger log = LoggerFactory.getLogger(RemoteService.class);

	private String secret;
	protected String hostname;
	protected HttpClient httpClient;
	protected Map<String, Session> activeSessions = new ConcurrentHashMap<>();

	protected final static String API_SESSIONS = "api/sessions";
	protected final static String API_TOKENS = "api/tokens";
	protected final static String API_RECORDINGS = "api/recordings";
	protected final static String API_RECORDINGS_START = "/start";
	protected final static String API_RECORDINGS_STOP = "/stop";

	/**
	 * @param hostname Public accessible IP where your instance of RemoteService
	 *                          Server is up an running
	 * @param secret            Secret used on OpenVidu Server initialization
	 */
	public RemoteService(String hostname, String secret) {

		this.hostname = hostname;

		if (!this.hostname.endsWith("/")) {
			this.hostname += "/";
		}

		this.secret = secret;

		TrustStrategy trustStrategy = new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		};

		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("OPENVIDUAPP", this.secret);
		//UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("remote", this.secret);
		provider.setCredentials(AuthScope.ANY, credentials);

		SSLContext sslContext;

		try {
			sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			throw new RuntimeException(e);
		}

		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder = requestBuilder.setConnectTimeout(30000);
		requestBuilder = requestBuilder.setConnectionRequestTimeout(30000);

		this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestBuilder.build())
				.setConnectionTimeToLive(30, TimeUnit.SECONDS).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.setSSLContext(sslContext).setDefaultCredentialsProvider(provider).build();
	}

	/**
	 * Creates an OpenVidu session with the default settings
	 * 
	 * @return The created session
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException
	 */
	public Session createSession() throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		Session s = new Session(this);
		this.activeSessions.put(s.getSessionId(), s);
		return s;
	}

	/**
	 * Creates an OpenVidu session
	 * 
	 * @param properties The specific configuration for this session
	 * 
	 * @return The created session
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException       Value returned from
	 *                                     {@link com.virnect.java.client.RemoteServiceHttpException#getStatus()}
	 *                                     <ul>
	 *                                     <li><code>409</code>: you are trying to
	 *                                     assign an already-in-use custom sessionId
	 *                                     to the session. See
	 *                                     {@link com.virnect.java.client.SessionProperties#customSessionId()}</li>
	 *                                     </ul>
	 */
	public Session createSession(SessionProperties properties)
			throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		Session s = new Session(this, properties);
		this.activeSessions.put(s.getSessionId(), s);
		return s;
	}

	/**
	 * Starts the recording of a {@link com.virnect.java.client.Session}
	 *
	 * @param sessionId  The sessionId of the session you want to start recording
	 * @param properties The configuration for this recording
	 *
	 * @return The new created session
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException       Value returned from
	 *                                     {@link com.virnect.java.client.RemoteServiceHttpException#getStatus()}
	 *                                     <ul>
	 *                                     <li><code>404</code>: no session exists
	 *                                     for the passed <i>sessionId</i></li>
	 *                                     <li><code>406</code>: the session has no
	 *                                     connected participants</li>
	 *                                     <li><code>422</code>: "resolution"
	 *                                     parameter exceeds acceptable values (for
	 *                                     both width and height, min 100px and max
	 *                                     1999px) or trying to start a recording
	 *                                     with both "hasAudio" and "hasVideo" to
	 *                                     false</li>
	 *                                     <li><code>409</code>: the session is not
	 *                                     configured for using
	 *                                     {@link com.virnect.java.client.MediaMode#ROUTED}
	 *                                     or it is already being recorded</li>
	 *                                     <li><code>501</code>: OpenVidu Server
	 *                                     recording module is disabled
	 *                                     (<i>OPENVIDU_RECORDING</i> property set
	 *                                     to <i>false</i>)</li>
	 *                                     </ul>
	 */
	public Recording startRecording(String sessionId, RecordingProperties properties)
			throws RemoteServiceJavaClientException, RemoteServiceHttpException {

		HttpPost request = new HttpPost(this.hostname + API_RECORDINGS + API_RECORDINGS_START);

		JsonObject json = new JsonObject();
		json.addProperty("session", sessionId);
		json.addProperty("name", properties.name());
		json.addProperty("outputMode", properties.outputMode().name());
		json.addProperty("hasAudio", properties.hasAudio());
		json.addProperty("hasVideo", properties.hasVideo());

		if ((Recording.OutputMode.COMPOSED.equals(properties.outputMode()) || (Recording.OutputMode.COMPOSED_QUICK_START.equals(properties.outputMode())))
				&& properties.hasVideo()) {
			json.addProperty("resolution", properties.resolution());
			json.addProperty("recordingLayout",
					(properties.recordingLayout() != null) ? properties.recordingLayout().name() : "");
			if (RecordingLayout.CUSTOM.equals(properties.recordingLayout())) {
				json.addProperty("customLayout", (properties.customLayout() != null) ? properties.customLayout() : "");
			}
		}

		StringEntity params = null;
		try {
			params = new StringEntity(json.toString());
		} catch (UnsupportedEncodingException e1) {
			throw new RemoteServiceJavaClientException(e1.getMessage(), e1.getCause());
		}

		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		request.setEntity(params);

		HttpResponse response;
		try {
			response = this.httpClient.execute(request);
		} catch (IOException e2) {
			throw new RemoteServiceJavaClientException(e2.getMessage(), e2.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_OK)) {
				Recording r = new Recording(httpResponseToJson(response));
				Session activeSession = this.activeSessions.get(r.getSessionId());
				if (activeSession != null) {
					activeSession.setIsBeingRecorded(true);
				} else {
					log.warn("No active session found for sessionId '" + r.getSessionId()
							+ "'. This instance of OpenVidu Java Client didn't create this session");
				}
				return r;
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Starts the recording of a {@link com.virnect.java.client.Session}
	 *
	 * @param sessionId The sessionId of the session you want to start recording
	 * @param name      The name you want to give to the video file. You can access
	 *                  this same value in your clients on recording events
	 *                  (recordingStarted, recordingStopped). <strong>WARNING: this
	 *                  parameter follows an overwriting policy.</strong> If you
	 *                  name two recordings the same, the newest MP4 file will
	 *                  overwrite the oldest one
	 *
	 * @return The started recording. If this method successfully returns the
	 *         Recording object it means that the recording can be stopped with
	 *         guarantees
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException       Value returned from
	 *                                     {@link com.virnect.java.client.RemoteServiceHttpException#getStatus()}
	 *                                     <ul>
	 *                                     <li><code>404</code>: no session exists
	 *                                     for the passed <i>sessionId</i></li>
	 *                                     <li><code>406</code>: the session has no
	 *                                     connected participants</li>
	 *                                     <li><code>422</code>: "resolution"
	 *                                     parameter exceeds acceptable values (for
	 *                                     both width and height, min 100px and max
	 *                                     1999px) or trying to start a recording
	 *                                     with both "hasAudio" and "hasVideo" to
	 *                                     false</li>
	 *                                     <li><code>409</code>: the session is not
	 *                                     configured for using
	 *                                     {@link com.virnect.java.client.MediaMode#ROUTED}
	 *                                     or it is already being recorded</li>
	 *                                     <li><code>501</code>: OpenVidu Server
	 *                                     recording module is disabled
	 *                                     (<i>OPENVIDU_RECORDING</i> property set
	 *                                     to <i>false</i>)</li>
	 *                                     </ul>
	 */
	public Recording startRecording(String sessionId, String name)
			throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		if (name == null) {
			name = "";
		}
		return this.startRecording(sessionId, new RecordingProperties.Builder().name(name).build());
	}

	/**
	 * Starts the recording of a {@link com.virnect.java.client.Session}
	 *
	 * @param sessionId The sessionId of the session you want to start recording
	 *
	 * @return The started recording. If this method successfully returns the
	 *         Recording object it means that the recording can be stopped with
	 *         guarantees
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException       Value returned from
	 *                                     {@link com.virnect.java.client.RemoteServiceHttpException#getStatus()}
	 *                                     <ul>
	 *                                     <li><code>404</code>: no session exists
	 *                                     for the passed <i>sessionId</i></li>
	 *                                     <li><code>406</code>: the session has no
	 *                                     connected participants</li>
	 *                                     <li><code>422</code>: "resolution"
	 *                                     parameter exceeds acceptable values (for
	 *                                     both width and height, min 100px and max
	 *                                     1999px) or trying to start a recording
	 *                                     with both "hasAudio" and "hasVideo" to
	 *                                     false</li>
	 *                                     <li><code>409</code>: the session is not
	 *                                     configured for using
	 *                                     {@link com.virnect.java.client.MediaMode#ROUTED}
	 *                                     or it is already being recorded</li>
	 *                                     <li><code>501</code>: OpenVidu Server
	 *                                     recording module is disabled
	 *                                     (<i>OPENVIDU_RECORDING</i> property set
	 *                                     to <i>false</i>)</li>
	 *                                     </ul>
	 */
	public Recording startRecording(String sessionId) throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		return this.startRecording(sessionId, "");
	}

	/**
	 * Stops the recording of a {@link com.virnect.java.client.Session}
	 *
	 * @param recordingId The id property of the recording you want to stop
	 *
	 * @return The stopped recording
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException       Value returned from
	 *                                     {@link com.virnect.java.client.RemoteServiceHttpException#getStatus()}
	 *                                     <ul>
	 *                                     <li><code>404</code>: no recording exists
	 *                                     for the passed <i>recordingId</i></li>
	 *                                     <li><code>406</code>: recording has
	 *                                     <i>starting</i> status. Wait until
	 *                                     <i>started</i> status before stopping the
	 *                                     recording</li>
	 *                                     </ul>
	 */
	public Recording stopRecording(String recordingId) throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		HttpPost request = new HttpPost(this.hostname + API_RECORDINGS + API_RECORDINGS_STOP + "/" + recordingId);
		HttpResponse response;
		try {
			response = this.httpClient.execute(request);
		} catch (IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_OK)) {
				Recording r = new Recording(httpResponseToJson(response));
				Session activeSession = this.activeSessions.get(r.getSessionId());
				if (activeSession != null) {
					activeSession.setIsBeingRecorded(false);
				} else {
					log.warn("No active session found for sessionId '" + r.getSessionId()
							+ "'. This instance of OpenVidu Java Client didn't create this session");
				}
				return r;
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Gets an existing recording
	 *
	 * @param recordingId The id property of the recording you want to retrieve
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException       Value returned from
	 *                                     {@link com.virnect.java.client.RemoteServiceHttpException#getStatus()}
	 *                                     <ul>
	 *                                     <li><code>404</code>: no recording exists
	 *                                     for the passed <i>recordingId</i></li>
	 *                                     </ul>
	 */
	public Recording getRecording(String recordingId) throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		HttpGet request = new HttpGet(this.hostname + API_RECORDINGS + "/" + recordingId);
		HttpResponse response;
		try {
			response = this.httpClient.execute(request);
		} catch (IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_OK)) {
				return new Recording(httpResponseToJson(response));
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Lists all existing recordings
	 *
	 * @return A {@link List} with all existing recordings
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException
	 */
	public List<Recording> listRecordings() throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		HttpGet request = new HttpGet(this.hostname + API_RECORDINGS);
		HttpResponse response;
		try {
			response = this.httpClient.execute(request);
		} catch (IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_OK)) {
				List<Recording> recordings = new ArrayList<>();
				JsonObject json = httpResponseToJson(response);
				JsonArray array = json.get("items").getAsJsonArray();
				array.forEach(item -> {
					recordings.add(new Recording(item.getAsJsonObject()));
				});
				return recordings;
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Deletes a recording. The recording must have status
	 * {@link com.virnect.java.client.Recording.Status#stopped},
	 * {@link com.virnect.java.client.Recording.Status#ready} or
	 * {@link com.virnect.java.client.Recording.Status#failed}
	 *
	 * @param recordingId The id property of the recording you want to delete
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException       Value returned from
	 *                                     {@link com.virnect.java.client.RemoteServiceHttpException#getStatus()}
	 *                                     <ul>
	 *                                     <li><code>404</code>: no recording exists
	 *                                     for the passed <i>recordingId</i></li>
	 *                                     <li><code>409</code>: the recording has
	 *                                     <i>started</i> status. Stop it before
	 *                                     deletion</li>
	 *                                     </ul>
	 */
	public void deleteRecording(String recordingId) throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		HttpDelete request = new HttpDelete(this.hostname + API_RECORDINGS + "/" + recordingId);
		HttpResponse response;
		try {
			response = this.httpClient.execute(request);
		} catch (IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (!(statusCode == org.apache.http.HttpStatus.SC_NO_CONTENT)) {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Returns the list of active sessions. <strong>This value will remain unchanged
	 * since the last time method {@link com.virnect.java.client.RemoteService#fetch()}
	 * was called</strong>. Exceptions to this rule are:
	 * <ul>
	 * <li>Calling {@link com.virnect.java.client.Session#fetch()} updates that
	 * specific Session status</li>
	 * <li>Calling {@link com.virnect.java.client.Session#close()} automatically
	 * removes the Session from the list of active Sessions</li>
	 * <li>Calling
	 * {@link com.virnect.java.client.Session#forceDisconnect(Connection)}
	 * automatically updates the inner affected connections for that specific
	 * Session</li>
	 * <li>Calling {@link com.virnect.java.client.Session#forceUnpublish(Publisher)}
	 * also automatically updates the inner affected connections for that specific
	 * Session</li>
	 * <li>Calling {@link com.virnect.java.client.RemoteService#startRecording(String)}
	 * and {@link com.virnect.java.client.RemoteService#stopRecording(String)}
	 * automatically updates the recording status of the Session
	 * ({@link com.virnect.java.client.Session#isBeingRecorded()})</li>
	 * </ul>
	 * <br>
	 * To get the list of active sessions with their current actual value, you must
	 * call first {@link com.virnect.java.client.RemoteService#fetch()} and then
	 * {@link com.virnect.java.client.RemoteService#getActiveSessions()}
	 */
	public List<Session> getActiveSessions() {
		return new ArrayList<>(this.activeSessions.values());
	}

	/**
	 * Updates every property of every active Session with the current status they
	 * have in OpenVidu Server. After calling this method you can access the updated
	 * list of active sessions by calling
	 * {@link com.virnect.java.client.RemoteService#getActiveSessions()}
	 * 
	 * @return true if any Session status has changed with respect to the server,
	 *         false if not. This applies to any property or sub-property of any of
	 *         the sessions locally stored in OpenVidu Java Client
	 * 
	 * @throws RemoteServiceHttpException
	 * @throws RemoteServiceJavaClientException
	 */
	public boolean fetch() throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		HttpGet request = new HttpGet(this.hostname + API_SESSIONS);

		HttpResponse response;
		try {
			response = this.httpClient.execute(request);
		} catch (IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_OK)) {
				JsonObject jsonSessions = httpResponseToJson(response);
				JsonArray jsonArraySessions = jsonSessions.get("content").getAsJsonArray();

				// Set to store fetched sessionIds and later remove closed sessions
				Set<String> fetchedSessionIds = new HashSet<>();
				// Boolean to store if any Session has changed
				final boolean[] hasChanged = { false };
				jsonArraySessions.forEach(session -> {
					String sessionId = (session.getAsJsonObject()).get("sessionId").getAsString();
					fetchedSessionIds.add(sessionId);
					this.activeSessions.computeIfPresent(sessionId, (sId, s) -> {
						String beforeJSON = s.toJson();
						s = s.resetSessionWithJson(session.getAsJsonObject());
						String afterJSON = s.toJson();
						boolean changed = !beforeJSON.equals(afterJSON);
						hasChanged[0] = hasChanged[0] || changed;
						log.info("Available session '{}' info fetched. Any change: {}", sessionId, changed);
						return s;
					});
					this.activeSessions.computeIfAbsent(sessionId, sId -> {
						log.info("New session '{}' fetched", sessionId);
						hasChanged[0] = true;
						return new Session(this, session.getAsJsonObject());
					});
				});

				// Remove closed sessions from activeSessions map
				this.activeSessions = this.activeSessions.entrySet().stream().filter(entry -> {
					if (fetchedSessionIds.contains(entry.getKey())) {
						return true;
					} else {
						log.info("Removing closed session {}", entry.getKey());
						hasChanged[0] = true;
						return false;
					}
				}).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
				log.info("Active sessions info fetched: {}", this.activeSessions.keySet());
				return hasChanged[0];
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	private JsonObject httpResponseToJson(HttpResponse response) throws RemoteServiceJavaClientException {
		try {
			JsonObject json = new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
			return json;
		} catch (JsonSyntaxException | ParseException | IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}
	}

}

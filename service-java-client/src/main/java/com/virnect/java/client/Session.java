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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Session {

	private static final Logger log = LoggerFactory.getLogger(Session.class);

	private String sessionId;
	private long createdAt;
	private RemoteService remoteService;
	private SessionProperties properties;
	private Map<String, Connection> activeConnections = new ConcurrentHashMap<>();
	private boolean recording = false;

	protected Session(RemoteService remoteService) throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		this.remoteService = remoteService;
		this.properties = new SessionProperties.Builder().build();
		this.getSessionIdHttp();
	}

	protected Session(RemoteService remoteService, SessionProperties properties)
			throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		this.remoteService = remoteService;
		this.properties = properties;
		this.getSessionIdHttp();
	}

	protected Session(RemoteService remoteService, JsonObject json) {
		this.remoteService = remoteService;
		this.resetSessionWithJson(json);
	}

	/**
	 * Gets the unique identifier of the Session
	 *
	 * @return The sessionId
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * Timestamp when this session was created, in UTC milliseconds (ms since Jan 1,
	 * 1970, 00:00:00 UTC)
	 */
	public long createdAt() {
		return this.createdAt;
	}

	/**
	 * Gets a new token associated to Session object with default values for
	 * {@link com.virnect.java.client.TokenOptions}. This always translates into a
	 * new request to OpenVidu Server
	 *
	 * @return The generated token
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException
	 */
	public String generateToken() throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		return this.generateToken(new TokenOptions.Builder().role(RemoteServiceRole.PUBLISHER).build());
	}

	/**
	 * Gets a new token associated to Session object configured with
	 * <code>tokenOptions</code>. This always translates into a new request to
	 * OpenVidu Server
	 *
	 * @return The generated token
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException
	 */
	public String generateToken(TokenOptions tokenOptions) throws RemoteServiceJavaClientException, RemoteServiceHttpException {

		if (!this.hasSessionId()) {
			this.getSessionId();
		}

		HttpPost request = new HttpPost(this.remoteService.hostname + RemoteService.API_TOKENS);

		JsonObject json = new JsonObject();
		json.addProperty("session", this.sessionId);
		json.addProperty("role", tokenOptions.getRole().name());
		json.addProperty("data", tokenOptions.getData());
		if (tokenOptions.getKurentoOptions() != null) {
			JsonObject kurentoOptions = new JsonObject();
			if (tokenOptions.getKurentoOptions().getVideoMaxRecvBandwidth() != null) {
				kurentoOptions.addProperty("videoMaxRecvBandwidth",
						tokenOptions.getKurentoOptions().getVideoMaxRecvBandwidth());
			}
			if (tokenOptions.getKurentoOptions().getVideoMinRecvBandwidth() != null) {
				kurentoOptions.addProperty("videoMinRecvBandwidth",
						tokenOptions.getKurentoOptions().getVideoMinRecvBandwidth());
			}
			if (tokenOptions.getKurentoOptions().getVideoMaxSendBandwidth() != null) {
				kurentoOptions.addProperty("videoMaxSendBandwidth",
						tokenOptions.getKurentoOptions().getVideoMaxSendBandwidth());
			}
			if (tokenOptions.getKurentoOptions().getVideoMinSendBandwidth() != null) {
				kurentoOptions.addProperty("videoMinSendBandwidth",
						tokenOptions.getKurentoOptions().getVideoMinSendBandwidth());
			}
			if (tokenOptions.getKurentoOptions().getAllowedFilters().length > 0) {
				JsonArray allowedFilters = new JsonArray();
				for (String filter : tokenOptions.getKurentoOptions().getAllowedFilters()) {
					allowedFilters.add(filter);
				}
				kurentoOptions.add("allowedFilters", allowedFilters);
			}
			json.add("kurentoOptions", kurentoOptions);
		}
		StringEntity params;
		try {
			params = new StringEntity(json.toString());
		} catch (UnsupportedEncodingException e1) {
			throw new RemoteServiceJavaClientException(e1.getMessage(), e1.getCause());
		}

		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		request.setEntity(params);

		HttpResponse response;
		try {
			response = this.remoteService.httpClient.execute(request);
		} catch (IOException e2) {
			throw new RemoteServiceJavaClientException(e2.getMessage(), e2.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_OK)) {
				String token = httpResponseToJson(response).get("id").getAsString();
				log.info("Returning a TOKEN: {}", token);
				return token;
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Gracefully closes the Session: unpublishes all streams and evicts every
	 * participant
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException
	 */
	public void close() throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		HttpDelete request = new HttpDelete(this.remoteService.hostname + RemoteService.API_SESSIONS + "/" + this.sessionId);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

		HttpResponse response;
		try {
			response = this.remoteService.httpClient.execute(request);
		} catch (IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_NO_CONTENT)) {
				this.remoteService.activeSessions.remove(this.sessionId);
				log.info("Session {} closed", this.sessionId);
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Updates every property of the Session with the current status it has in
	 * OpenVidu Server. This is especially useful for getting the list of active
	 * connections to the Session
	 * ({@link com.virnect.java.client.Session#getActiveConnections()}) and use
	 * those values to call
	 * {@link com.virnect.java.client.Session#forceDisconnect(Connection)} or
	 * {@link com.virnect.java.client.Session#forceUnpublish(Publisher)}. <br>
	 * 
	 * To update every Session object owned by OpenVidu object, call
	 * {@link com.virnect.java.client.RemoteService#fetch()}
	 * 
	 * @return true if the Session status has changed with respect to the server,
	 *         false if not. This applies to any property or sub-property of the
	 *         object
	 * 
	 * @throws RemoteServiceHttpException
	 * @throws RemoteServiceJavaClientException
	 */
	public boolean fetch() throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		String beforeJSON = this.toJson();
		HttpGet request = new HttpGet(this.remoteService.hostname + RemoteService.API_SESSIONS + "/" + this.sessionId);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

		HttpResponse response;
		try {
			response = this.remoteService.httpClient.execute(request);
		} catch (IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_OK)) {
				this.resetSessionWithJson(httpResponseToJson(response));
				String afterJSON = this.toJson();
				boolean hasChanged = !beforeJSON.equals(afterJSON);
				log.info("Session info fetched for session '{}'. Any change: {}", this.sessionId, hasChanged);
				return hasChanged;
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Forces the user represented by <code>connection</code> to leave the session.
	 * OpenVidu Browser will trigger the proper events on the client-side
	 * (<code>streamDestroyed</code>, <code>connectionDestroyed</code>,
	 * <code>sessionDisconnected</code>) with reason set to
	 * "forceDisconnectByServer" <br>
	 * 
	 * You can get <code>connection</code> parameter with
	 * {@link com.virnect.java.client.Session#fetch()} and then
	 * {@link com.virnect.java.client.Session#getActiveConnections()}
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException
	 */
	public void forceDisconnect(Connection connection) throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		this.forceDisconnect(connection.getConnectionId());
	}

	/**
	 * Forces the user with Connection <code>connectionId</code> to leave the
	 * session. OpenVidu Browser will trigger the proper events on the client-side
	 * (<code>streamDestroyed</code>, <code>connectionDestroyed</code>,
	 * <code>sessionDisconnected</code>) with reason set to
	 * "forceDisconnectByServer" <br>
	 * 
	 * You can get <code>connectionId</code> parameter with
	 * {@link com.virnect.java.client.Session#fetch()} (use
	 * {@link com.virnect.java.client.Connection#getConnectionId()} to get the
	 * `connectionId` you want)
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException
	 */
	public void forceDisconnect(String connectionId) throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		HttpDelete request = new HttpDelete(
				this.remoteService.hostname + RemoteService.API_SESSIONS + "/" + this.sessionId + "/connection/" + connectionId);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

		HttpResponse response = null;
		try {
			response = this.remoteService.httpClient.execute(request);
		} catch (IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_NO_CONTENT)) {
				// Remove connection from activeConnections map
				Connection connectionClosed = this.activeConnections.remove(connectionId);
				// Remove every Publisher of the closed connection from every subscriber list of
				// other connections
				if (connectionClosed != null) {
					for (Publisher publisher : connectionClosed.getPublishers()) {
						String streamId = publisher.getStreamId();
						for (Connection connection : this.activeConnections.values()) {
							connection.setSubscribers(connection.getSubscribers().stream()
									.filter(subscriber -> !streamId.equals(subscriber)).collect(Collectors.toList()));
						}
					}
				} else {
					log.warn(
							"The closed connection wasn't fetched in OpenVidu Java Client. No changes in the collection of active connections of the Session");
				}
				log.info("Connection {} closed", connectionId);
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally

		{
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Forces some user to unpublish a Stream. OpenVidu Browser will trigger the
	 * proper events on the client-side (<code>streamDestroyed</code>) with reason
	 * set to "forceUnpublishByServer".<br>
	 * 
	 * You can get <code>publisher</code> parameter with
	 * {@link com.virnect.java.client.Session#getActiveConnections()} and then for
	 * each Connection you can call
	 * {@link com.virnect.java.client.Connection#getPublishers()}. Remember to call
	 * {@link com.virnect.java.client.Session#fetch()} before to fetch the current
	 * actual properties of the Session from OpenVidu Server
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException
	 */
	public void forceUnpublish(Publisher publisher) throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		this.forceUnpublish(publisher.getStreamId());
	}

	/**
	 * Forces some user to unpublish a Stream. OpenVidu Browser will trigger the
	 * proper events on the client-side (<code>streamDestroyed</code>) with reason
	 * set to "forceUnpublishByServer". <br>
	 * 
	 * You can get <code>streamId</code> parameter with
	 * {@link com.virnect.java.client.Session#getActiveConnections()} and then for
	 * each Connection you can call
	 * {@link com.virnect.java.client.Connection#getPublishers()}. Finally
	 * {@link com.virnect.java.client.Publisher#getStreamId()}) will give you the
	 * <code>streamId</code>. Remember to call
	 * {@link com.virnect.java.client.Session#fetch()} before to fetch the current
	 * actual properties of the Session from OpenVidu Server
	 * 
	 * @throws RemoteServiceJavaClientException
	 * @throws RemoteServiceHttpException
	 */
	public void forceUnpublish(String streamId) throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		HttpDelete request = new HttpDelete(
				this.remoteService.hostname + RemoteService.API_SESSIONS + "/" + this.sessionId + "/stream/" + streamId);
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

		HttpResponse response;
		try {
			response = this.remoteService.httpClient.execute(request);
		} catch (IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_NO_CONTENT)) {
				for (Connection connection : this.activeConnections.values()) {
					// Try to remove the Publisher from the Connection publishers collection
					if (connection.publishers.remove(streamId) != null) {
						continue;
					}
					// Try to remove the Publisher from the Connection subscribers collection
					connection.subscribers.remove(streamId);
				}
				log.info("Stream {} unpublished", streamId);
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	/**
	 * Returns the list of active connections to the session. <strong>This value
	 * will remain unchanged since the last time method
	 * {@link com.virnect.java.client.Session#fetch()} was called</strong>.
	 * Exceptions to this rule are:
	 * <ul>
	 * <li>Calling {@link com.virnect.java.client.Session#forceUnpublish(String)}
	 * updates each affected Connection status</li>
	 * <li>Calling {@link com.virnect.java.client.Session#forceDisconnect(String)}
	 * updates each affected Connection status</li>
	 * </ul>
	 * <br>
	 * To get the list of active connections with their current actual value, you
	 * must call first {@link com.virnect.java.client.Session#fetch()} and then
	 * {@link com.virnect.java.client.Session#getActiveConnections()}
	 */
	public List<Connection> getActiveConnections() {
		return new ArrayList<>(this.activeConnections.values());
	}

	/**
	 * Returns whether the session is being recorded or not
	 */
	public boolean isBeingRecorded() {
		return this.recording;
	}

	/**
	 * Returns the properties defining the session
	 */
	public SessionProperties getProperties() {
		return this.properties;
	}

	@Override
	public String toString() {
		return this.sessionId;
	}

	private boolean hasSessionId() {
		return (this.sessionId != null && !this.sessionId.isEmpty());
	}

	private void getSessionIdHttp() throws RemoteServiceJavaClientException, RemoteServiceHttpException {
		if (this.hasSessionId()) {
			return;
		}

		HttpPost request = new HttpPost(this.remoteService.hostname + RemoteService.API_SESSIONS);

		JsonObject json = new JsonObject();
		json.addProperty("mediaMode", properties.mediaMode().name());
		json.addProperty("recordingMode", properties.recordingMode().name());
		json.addProperty("defaultOutputMode", properties.defaultOutputMode().name());
		json.addProperty("defaultRecordingLayout", properties.defaultRecordingLayout().name());
		json.addProperty("defaultCustomLayout", properties.defaultCustomLayout());
		json.addProperty("customSessionId", properties.customSessionId());
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
			response = this.remoteService.httpClient.execute(request);
		} catch (IOException e2) {
			throw new RemoteServiceJavaClientException(e2.getMessage(), e2.getCause());
		}
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if ((statusCode == org.apache.http.HttpStatus.SC_OK)) {
				JsonObject responseJson = httpResponseToJson(response);
				this.sessionId = responseJson.get("id").getAsString();
				this.createdAt = responseJson.get("createdAt").getAsLong();
				log.info("Session '{}' created", this.sessionId);
			} else if (statusCode == org.apache.http.HttpStatus.SC_CONFLICT) {
				// 'customSessionId' already existed
				this.sessionId = properties.customSessionId();
			} else {
				throw new RemoteServiceHttpException(statusCode);
			}
		} finally {
			EntityUtils.consumeQuietly(response.getEntity());
		}
	}

	private JsonObject httpResponseToJson(HttpResponse response) throws RemoteServiceJavaClientException {
		JsonObject json;
		try {
			json = new Gson().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
		} catch (JsonSyntaxException | IOException e) {
			throw new RemoteServiceJavaClientException(e.getMessage(), e.getCause());
		}
		return json;
	}

	protected void setIsBeingRecorded(boolean recording) {
		this.recording = recording;
	}

	protected Session resetSessionWithJson(JsonObject json) {
		this.sessionId = json.get("sessionId").getAsString();
		this.createdAt = json.get("createdAt").getAsLong();
		this.recording = json.get("recording").getAsBoolean();
		SessionProperties.Builder builder = new SessionProperties.Builder()
				.mediaMode(MediaMode.valueOf(json.get("mediaMode").getAsString()))
				.recordingMode(RecordingMode.valueOf(json.get("recordingMode").getAsString()))
				.defaultOutputMode(Recording.OutputMode.valueOf(json.get("defaultOutputMode").getAsString()));
		if (json.has("defaultRecordingLayout")) {
			builder.defaultRecordingLayout(RecordingLayout.valueOf(json.get("defaultRecordingLayout").getAsString()));
		}
		if (json.has("defaultCustomLayout")) {
			builder.defaultCustomLayout(json.get("defaultCustomLayout").getAsString());
		}
		if (this.properties != null && this.properties.customSessionId() != null) {
			builder.customSessionId(this.properties.customSessionId());
		} else if (json.has("customSessionId")) {
			builder.customSessionId(json.get("customSessionId").getAsString());
		}
		this.properties = builder.build();
		JsonArray jsonArrayConnections = (json.get("connections").getAsJsonObject()).get("content").getAsJsonArray();
		this.activeConnections.clear();
		jsonArrayConnections.forEach(connection -> {
			JsonObject con = connection.getAsJsonObject();

			Map<String, Publisher> publishers = new ConcurrentHashMap<>();
			JsonArray jsonArrayPublishers = con.get("publishers").getAsJsonArray();
			jsonArrayPublishers.forEach(publisher -> {
				JsonObject pubJson = publisher.getAsJsonObject();
				JsonObject mediaOptions = pubJson.get("mediaOptions").getAsJsonObject();
				Publisher pub = new Publisher(pubJson.get("streamId").getAsString(),
						pubJson.get("createdAt").getAsLong(), mediaOptions.get("hasAudio").getAsBoolean(),
						mediaOptions.get("hasVideo").getAsBoolean(), mediaOptions.get("audioActive"),
						mediaOptions.get("videoActive"), mediaOptions.get("frameRate"), mediaOptions.get("typeOfVideo"),
						mediaOptions.get("videoDimensions"));
				publishers.put(pub.getStreamId(), pub);
			});

			List<String> subscribers = new ArrayList<>();
			JsonArray jsonArraySubscribers = con.get("subscribers").getAsJsonArray();
			jsonArraySubscribers.forEach(subscriber -> {
				subscribers.add((subscriber.getAsJsonObject()).get("streamId").getAsString());
			});

			this.activeConnections.put(con.get("connectionId").getAsString(),
					new Connection(con.get("connectionId").getAsString(), con.get("createdAt").getAsLong(),
							RemoteServiceRole.valueOf(con.get("role").getAsString()), con.get("token").getAsString(),
							con.get("location").getAsString(), con.get("platform").getAsString(),
							con.get("serverData").getAsString(), con.get("clientData").getAsString(), publishers,
							subscribers));
		});
		return this;
	}

	protected String toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("sessionId", this.sessionId);
		json.addProperty("createdAt", this.createdAt);
		json.addProperty("customSessionId", this.properties.customSessionId());
		json.addProperty("recording", this.recording);
		json.addProperty("mediaMode", this.properties.mediaMode().name());
		json.addProperty("recordingMode", this.properties.recordingMode().name());
		json.addProperty("defaultOutputMode", this.properties.defaultOutputMode().name());
		json.addProperty("defaultRecordingLayout", this.properties.defaultRecordingLayout().name());
		json.addProperty("defaultCustomLayout", this.properties.defaultCustomLayout());
		JsonObject connections = new JsonObject();
		connections.addProperty("numberOfElements", this.getActiveConnections().size());
		JsonArray jsonArrayConnections = new JsonArray();
		this.getActiveConnections().forEach(con -> {
			JsonObject c = new JsonObject();
			c.addProperty("connectionId", con.getConnectionId());
			c.addProperty("role", con.getRole().name());
			c.addProperty("token", con.getToken());
			c.addProperty("clientData", con.getClientData());
			c.addProperty("serverData", con.getServerData());
			JsonArray pubs = new JsonArray();
			con.getPublishers().forEach(p -> {
				pubs.add(p.toJson());
			});
			JsonArray subs = new JsonArray();
			con.getSubscribers().forEach(s -> {
				subs.add(s);
			});
			c.add("publishers", pubs);
			c.add("subscribers", subs);
			jsonArrayConnections.add(c);
		});
		connections.add("content", jsonArrayConnections);
		json.add("connections", connections);
		return json.toString();
	}

}

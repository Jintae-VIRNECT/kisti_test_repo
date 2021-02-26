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

package com.virnect.mediaserver.summary;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.virnect.mediaserver.cdr.CDREventRecording;
import com.virnect.mediaserver.cdr.CDREventSession;
import com.virnect.mediaserver.core.FinalUser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class SessionSummary {

	private CDREventSession eventSessionEnd;
	private Map<String, FinalUser> users;
	private Collection<CDREventRecording> recordings;

	public SessionSummary(CDREventSession event, Map<String, FinalUser> users,
			Collection<CDREventRecording> recordings) {
		this.eventSessionEnd = event;
		this.users = users;
		this.recordings = recordings == null ? new LinkedList<>() : recordings;
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("createdAt", this.eventSessionEnd.getStartTime());
		json.addProperty("destroyedAt", this.eventSessionEnd.getTimestamp());
		json.addProperty("sessionId", this.eventSessionEnd.getSessionId());
		json.addProperty("customSessionId", this.eventSessionEnd.getSession().getSessionProperties().customSessionId());
		json.addProperty("mediaMode", this.eventSessionEnd.getSession().getSessionProperties().mediaMode().name());
		json.addProperty("recordingMode",
				this.eventSessionEnd.getSession().getSessionProperties().recordingMode().name());

		long duration = (this.eventSessionEnd.getTimestamp() - this.eventSessionEnd.getStartTime()) / 1000;
		json.addProperty("duration", duration);

		json.addProperty("reason",
				this.eventSessionEnd.getReason() != null ? this.eventSessionEnd.getReason().name() : "");

		// Final users
		JsonObject usersJson = new JsonObject();
		usersJson.addProperty("numberOfElements", users.size());
		JsonArray jsonArray = new JsonArray();
		users.values().forEach(finalUser -> {
			jsonArray.add(finalUser.toJson());
		});
		usersJson.add("content", jsonArray);
		json.add("users", usersJson);

		// Accumulated recordings
		JsonObject recordingsJson = new JsonObject();
		recordingsJson.addProperty("numberOfElements", recordings.size());
		JsonArray jsonRecordingsArray = new JsonArray();
		recordings.forEach(rec -> {
			jsonRecordingsArray.add(rec.toJson());
		});
		recordingsJson.add("content", jsonRecordingsArray);
		json.add("recordings", recordingsJson);

		return json;
	}

	public CDREventSession getEventSessionEnd() {
		return this.eventSessionEnd;
	}

	public Map<String, FinalUser> getUsers() {
		return this.users;
	}

}

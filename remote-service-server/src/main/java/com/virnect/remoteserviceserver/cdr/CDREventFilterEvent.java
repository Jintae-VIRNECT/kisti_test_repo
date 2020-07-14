package com.virnect.remoteserviceserver.cdr;

import com.google.gson.JsonObject;
import org.kurento.client.GenericMediaEvent;
import org.kurento.jsonrpc.Props;

public class CDREventFilterEvent extends CDREvent {

	private GenericMediaEvent event;
	private String participantId;
	private String streamId;
	private String filterType;

	public CDREventFilterEvent(String sessionId, String participantId, String streamId, String filterType,
			GenericMediaEvent event) {
		super(CDREventName.filterEventDispatched, sessionId, Long.parseLong(event.getTimestampMillis()));
		this.event = event;
		this.participantId = participantId;
		this.streamId = streamId;
		this.filterType = filterType;
	}

	public String getEventType() {
		return this.event.getType();
	}

	public Props getEventData() {
		return this.event.getData();
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = super.toJson();
		json.addProperty("participantId", this.participantId);
		json.addProperty("streamId", this.streamId);
		json.addProperty("filterType", this.filterType);
		json.addProperty("eventType", this.event.getType());
		json.addProperty("data", this.event.getData().toString());
		return json;
	}

}

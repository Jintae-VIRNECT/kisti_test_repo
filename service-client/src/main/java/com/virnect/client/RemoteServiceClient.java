package com.virnect.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.virnect.client.internal.JsonRoomUtils;
import com.virnect.client.internal.Notification;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.kurento.jsonrpc.client.JsonRpcClient;
import org.kurento.jsonrpc.client.JsonRpcClientWebSocket;
import org.kurento.jsonrpc.client.JsonRpcWSConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.virnect.client.internal.ProtocolElements.*;

public class RemoteServiceClient {
    private static final Logger log = LoggerFactory.getLogger(RemoteServiceClient.class);

    private JsonRpcClient client;
    private ServerJsonRpcHandler handler;

    public RemoteServiceClient(String wsUri) {
        this(new JsonRpcClientWebSocket(wsUri, new JsonRpcWSConnectionListener() {

            @Override
            public void reconnected(boolean sameServer) {
            }

            @Override
            public void disconnected() {
                log.warn("JsonRpcWebsocket connection: Disconnected");
            }

            @Override
            public void connectionFailed() {
                log.warn("JsonRpcWebsocket connection: Connection failed");
            }

            @Override
            public void connected() {
            }

            @Override
            public void reconnecting() {
                log.warn("JsonRpcWebsocket connection: is reconnecting");
            }
        }, new SslContextFactory(true)));
    }

    public RemoteServiceClient(JsonRpcClient client) {
        this.client = client;
        this.handler = new ServerJsonRpcHandler();
        this.client.setServerRequestHandler(this.handler);
    }

    public RemoteServiceClient(JsonRpcClient client, ServerJsonRpcHandler handler) {
        this.client = client;
        this.handler = handler;
        this.client.setServerRequestHandler(this.handler);
    }

    public void close() throws IOException {
        this.client.close();
    }

    public Map<String, List<String>> joinRoom(String roomName, String userName)
            throws IOException {

        JsonObject params = new JsonObject();
        params.addProperty(JOINROOM_ROOM_PARAM, roomName);
        params.addProperty(JOINROOM_USER_PARAM, userName);

        JsonElement result = client.sendRequest(JOINROOM_METHOD, params);
        Map<String, List<String>> peers = new HashMap<String, List<String>>();
        JsonArray jsonPeers = JsonRoomUtils.getResponseProperty(result, "value", JsonArray.class);
        if (jsonPeers.size() > 0) {
            Iterator<JsonElement> peerIt = jsonPeers.iterator();
            while (peerIt.hasNext()) {
                JsonElement peer = peerIt.next();
                String peerId = JsonRoomUtils.getResponseProperty(peer, JOINROOM_PEERID_PARAM,
                        String.class);
                List<String> streams = new ArrayList<String>();
                JsonArray jsonStreams = JsonRoomUtils.getResponseProperty(peer, JOINROOM_PEERSTREAMS_PARAM,
                        JsonArray.class, true);
                if (jsonStreams != null) {
                    Iterator<JsonElement> streamIt = jsonStreams.iterator();
                    while (streamIt.hasNext()) {
                        streams.add(JsonRoomUtils.getResponseProperty(streamIt.next(),
                                JOINROOM_PEERSTREAMID_PARAM, String.class));
                    }
                }
                peers.put(peerId, streams);
            }
        }
        return peers;
    }

    public void leaveRoom() throws IOException {
        client.sendRequest(LEAVEROOM_METHOD, new JsonObject());
    }

    public String publishVideo(String sdpOffer, boolean doLoopback) throws IOException {
        JsonObject params = new JsonObject();
        params.addProperty(PUBLISHVIDEO_SDPOFFER_PARAM, sdpOffer);
        params.addProperty(PUBLISHVIDEO_DOLOOPBACK_PARAM, doLoopback);
        JsonElement result = client.sendRequest(PUBLISHVIDEO_METHOD, params);
        return JsonRoomUtils.getResponseProperty(result, PUBLISHVIDEO_SDPANSWER_PARAM, String.class);
    }

    public void unpublishVideo() throws IOException {
        client.sendRequest(UNPUBLISHVIDEO_METHOD, new JsonObject());
    }

    // sender should look like 'username_streamId'
    public String receiveVideoFrom(String sender, String sdpOffer) throws IOException {
        JsonObject params = new JsonObject();
        params.addProperty(RECEIVEVIDEO_SENDER_PARAM, sender);
        params.addProperty(RECEIVEVIDEO_SDPOFFER_PARAM, sdpOffer);
        JsonElement result = client.sendRequest(RECEIVEVIDEO_METHOD, params);
        return JsonRoomUtils.getResponseProperty(result, RECEIVEVIDEO_SDPANSWER_PARAM, String.class);
    }

    // sender should look like 'username_streamId'
    public void unsubscribeFromVideo(String sender) throws IOException {
        JsonObject params = new JsonObject();
        params.addProperty(UNSUBSCRIBEFROMVIDEO_SENDER_PARAM, sender);
        client.sendRequest(UNSUBSCRIBEFROMVIDEO_METHOD, params);
    }

    public void onIceCandidate(String endpointName, String candidate, String sdpMid,
                               int sdpMLineIndex) throws IOException {
        JsonObject params = new JsonObject();
        params.addProperty(ONICECANDIDATE_EPNAME_PARAM, endpointName);
        params.addProperty(ONICECANDIDATE_CANDIDATE_PARAM, candidate);
        params.addProperty(ONICECANDIDATE_SDPMIDPARAM, sdpMid);
        params.addProperty(ONICECANDIDATE_SDPMLINEINDEX_PARAM, sdpMLineIndex);
        client.sendRequest(ONICECANDIDATE_METHOD, params);
    }

    public void sendMessage(String userName, String roomName, String message) throws IOException {
        JsonObject params = new JsonObject();
        params.addProperty(SENDMESSAGE_MESSAGE_PARAM, message);
        client.sendRequest(SENDMESSAGE_ROOM_METHOD, params);
    }

    public JsonElement customRequest(JsonObject customReqParams) throws IOException {
        return client.sendRequest(CUSTOMREQUEST_METHOD, customReqParams);
    }

    /**
     * Polls the notifications list maintained by this client to obtain new events sent by server.
     * This method blocks until there is a notification to return. This is a one-time operation for
     * the returned element.
     *
     * @return a server notification object, null when interrupted while waiting
     */
    public Notification getServerNotification() {
        return this.handler.getNotification();
    }
}

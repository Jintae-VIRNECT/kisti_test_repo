package com.virnect.remote.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.remote.dto.push.InviteRoomContents;
import com.virnect.data.dto.PushSendRequest;
import com.virnect.data.dto.rest.PushResponse;
import com.virnect.data.global.common.ApiResponse;
import com.virnect.data.application.message.MessageRestService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushMessageClient {

    //private final ObjectMapper mapper = new ObjectMapper();\
    private final ObjectMapper mapper;

    private PushSendRequest pushSendRequest;

    private final MessageRestService messageRestService;

    /*@Qualifier(value = "messageRestService")
    @Autowired
    public void setMessageRestService(MessageRestService messageRestService) {
        this.messageRestService = messageRestService;
    }*/

    public void setPush(
        String pushService,
        String pushEvent,
        String workspaceId,
        String userId,
        List<String> targetUserIds
    ) {
        pushSendRequest = new PushSendRequest();
        pushSendRequest.setService(pushService);
        pushSendRequest.setEvent(pushEvent);
        pushSendRequest.setWorkspaceId(workspaceId);
        pushSendRequest.setUserId(userId);
        pushSendRequest.setTargetUserIds(targetUserIds);
    }

    public ApiResponse<PushResponse> sendPushInvite(
        String sessionId,
        String title,
        String sender,
        String profile,
        String leaderId
    ) {
        //set push message invite room contents
        InviteRoomContents inviteRoomContents = new InviteRoomContents();
        inviteRoomContents.setSessionId(sessionId);
        inviteRoomContents.setTitle(title);
        inviteRoomContents.setNickName(sender);
        inviteRoomContents.setProfile(profile);
        inviteRoomContents.setLeaderId(leaderId);

        try {
            String jsonString = mapper.writeValueAsString(inviteRoomContents);
            pushSendRequest.setContents(mapper.readValue(jsonString, new TypeReference<Map<Object, Object>>() {}));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return this.messageRestService.sendPush(pushSendRequest);
    }

    public ApiResponse<PushResponse> sendPushEvict() {
        pushSendRequest.setContents(new HashMap<>());
        return this.messageRestService.sendPush(pushSendRequest);
    }

    public ApiResponse<PushResponse> sendPush(PushSendRequest pushSendRequest) {
        return this.messageRestService.sendPush(pushSendRequest);
    }

    /*public PushSendRequest sendInviteContents(String sessionId,
                                             String title,
                                             String sender,
                                             String profile) {
        //set push message invite room contents
        InviteRoomContents inviteRoomContents = new InviteRoomContents();
        inviteRoomContents.setSessionId(sessionId);
        inviteRoomContents.setTitle(title);
        inviteRoomContents.setNickName(sender);
        inviteRoomContents.setProfile(profile);

        try {
            String jsonString = mapper.writeValueAsString(inviteRoomContents);
            pushSendRequest.setContents(mapper.readValue(jsonString, new TypeReference<Map<Object, Object>>() {}));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return pushSendRequest;
    }*/
}

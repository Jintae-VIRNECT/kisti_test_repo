package com.virnect.serviceserver.test.integration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.virnect.java.client.RemoteServiceRole;
import com.virnect.mediaserver.core.Participant;
import com.virnect.mediaserver.core.Token;
import com.virnect.mediaserver.kurento.core.KurentoSessionManager;
import com.virnect.mediaserver.kurento.kms.KmsManager;
import com.virnect.serviceserver.api.kms.KurentoSessionRestController;
import com.virnect.serviceserver.test.integration.config.IntegrationTestConfiguration;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = {
        "service.remote_sessions_garbage_interval=1",
        "service.remote_sessions_garbage_threshold=1",
        "spring.cloud.config.enabled=false"
})
@TestPropertySource(locations = "classpath:integration-test.properties")
@ContextConfiguration(classes = { IntegrationTestConfiguration.class })
@WebAppConfiguration
@ActiveProfiles("local")
public class SessionGarbageCollectorIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(SessionGarbageCollectorIntegrationTest.class);

    @SpyBean
    private KmsManager kmsManager;

    //@MockBean
    //private SessionManager sessionManager;
    @Autowired
    private KurentoSessionManager sessionManager;

    @Autowired
    private KurentoSessionRestController sessionRestController;

    @Test
    @DisplayName("Sessions not active garbage collector")
    void garbageCollectorOfSessionsNotActiveTest() throws Exception {
        sessionManager.setKurentoSessionListener(null);
        log.info("Sessions not active garbage collector");

        JsonObject jsonResponse;

        getSessionId();
        jsonResponse = listSessions();

        Assert.assertEquals("Wrong number of sessions", 1, jsonResponse.get("numberOfElements").getAsInt());

        Thread.sleep(2000);

        jsonResponse = listSessions();
        Assert.assertEquals("Wrong number of sessions", 0, jsonResponse.get("numberOfElements").getAsInt());

        getSessionId();
        getSessionId();
        String sessionId = getSessionId();
        jsonResponse = listSessions();
        Assert.assertEquals("Wrong number of sessions", 3, jsonResponse.get("numberOfElements").getAsInt());

        String token = getToken(sessionId);
        joinParticipant(sessionId, token);

        Thread.sleep(2000);

        jsonResponse = listSessions();
        Assert.assertEquals("Wrong number of sessions", 1, jsonResponse.get("numberOfElements").getAsInt());
    }

    private String getSessionId() {
        String stringResponse = (String) sessionRestController.getSessionId(new HashMap<>()).getBody();
        return new Gson().fromJson(stringResponse, JsonObject.class).get("id").getAsString();
    }

    private String getToken(String sessionId) {
        Map<String, String> map = new HashMap<>();
        map.put("session", sessionId);
        String stringResponse = (String) sessionRestController.newToken(map).getBody();
        return new Gson().fromJson(stringResponse, JsonObject.class).get("token").getAsString();
    }

    private JsonObject listSessions() {
        String stringResponse = (String) sessionRestController.listSessions(false).getBody();
        return new Gson().fromJson(stringResponse, JsonObject.class);
    }

    private void joinParticipant(String sessionId, String token) {
        Token t = new Token(token, RemoteServiceRole.PUBLISHER, "SERVER_METADATA", null, null);
        String uuid = UUID.randomUUID().toString();
        String participantPrivateId = "PARTICIPANT_PRIVATE_ID_" + uuid;
        String finalUserId = "FINAL_USER_ID_" + uuid;
        Participant participant = sessionManager.newParticipant(sessionId, participantPrivateId, t, "CLIENT_METADATA",
                null, "Chrome", finalUserId);
        sessionManager.joinRoom(participant, sessionId, null);
    }
}

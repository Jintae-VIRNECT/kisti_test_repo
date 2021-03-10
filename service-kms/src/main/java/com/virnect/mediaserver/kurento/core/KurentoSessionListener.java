package com.virnect.mediaserver.kurento.core;

import com.virnect.mediaserver.core.EndReason;
import com.virnect.mediaserver.core.Participant;
import com.virnect.mediaserver.core.Session;

import java.util.Set;

public interface KurentoSessionListener {
    void createSession(Session sessionNotActive);

    //boolean joinSession(Participant participant, String sessionId, Set<Participant> existingParticipants, Integer transactionId);

    boolean joinSession(Participant participant, String sessionId, Integer transactionId);

    void leaveSession(Participant participant, String sessionId, Set<Participant> remainingParticipants, Integer transactionId, EndReason reason);

    void destroySession(KurentoSession session, EndReason reason);
}

package com.virnect.data.service;

import com.virnect.data.dao.MemberHistory;
import com.virnect.data.dao.RoomHistory;
import com.virnect.data.repository.MemberHistoryRepository;
import com.virnect.data.repository.RoomHistoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Do some processing for the request.
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly=true)
public class HistoryService {
    private static final String TAG = HistoryService.class.getSimpleName();

    private final RoomHistoryRepository roomHistoryRepository;
    private final MemberHistoryRepository memberHistoryRepository;

    //========================================== History Services =====================================//
    //public ApiResponse<RoomHistoryInfoListResponse> getRoomHistoryInfoList(String workspaceId, String userId, boolean paging, Pageable pageable) {
    public MemberHistory getMemberHistory(String workspaceId, String sessionId, String userId) {
        return this.memberHistoryRepository.findByWorkspaceIdAndSessionIdAndUuid(workspaceId, sessionId, userId).orElse(null);
    }

    public List<MemberHistory> getMemberHistoryList(String workspaceId, String userId) {
        return this.memberHistoryRepository.findByWorkspaceIdAndUuid(workspaceId, userId);
    }

    public List<MemberHistory> getMemberHistoryList(String sessionId) {
        return this.memberHistoryRepository.findAllBySessionId(sessionId);
    }

    public Page<MemberHistory> getMemberHistoryList(String workspaceId, String userId, Pageable pageable) {
        return this.memberHistoryRepository.findByWorkspaceIdAndUuidAndRoomHistoryIsNotNullAndHistoryDeletedFalse(workspaceId, userId, pageable);
        //return this.memberHistoryRepository.findByWorkspaceIdAndUuidAndRoomHistoryIsNotNull(workspaceId, userId, pageable);
    }

    public Page<RoomHistory> getRoomHistory(String workspaceId,Pageable pageable) {
        return this.roomHistoryRepository.findRoomHistoryByWorkspaceId(workspaceId, pageable);
        //return this.roomHistoryRepository.findRoomHistoriesByWorkspaceIdAndMemberHistoriesIsNotNull(workspaceId, pageable);
    }

    public RoomHistory getRoomHistory(String workspaceId, String sessionId) {
        return this.roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
    }

    @Transactional
    public void removeRoomHistory(List<MemberHistory> memberHistoryList) {
        memberHistoryList.forEach(memberHistory -> {
            if(memberHistory.getRoomHistory() != null) {
                //memberHistory.setRoomHistory(null);
                memberHistory.setHistoryDeleted(true);
                this.memberHistoryRepository.save(memberHistory);
            }
        });
    }

    @Transactional
    public void removeRoomHistory(MemberHistory memberHistory) {
        if(memberHistory.getRoomHistory() != null) {
            //memberHistory.setRoomHistory(null);
            memberHistory.setHistoryDeleted(true);
            this.memberHistoryRepository.save(memberHistory);
        }
    }
}

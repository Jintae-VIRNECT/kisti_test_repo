package com.virnect.data.service;

import com.virnect.data.ApiResponse;
import com.virnect.data.dao.MemberHistory;
import com.virnect.data.dao.MemberStatus;
import com.virnect.data.dao.MemberType;
import com.virnect.data.dao.RoomHistory;
import com.virnect.data.repository.MemberHistoryRepository;
import com.virnect.data.repository.RoomHistoryRepository;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.request.PageRequest;
import com.virnect.data.dto.request.RoomHistoryDeleteRequest;
import com.virnect.data.dto.response.*;
import com.virnect.data.dto.feign.UserInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.feign.service.UserRestService;
import com.virnect.data.feign.service.WorkspaceRestService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    //private final WorkspaceRestService workspaceRestService;
    //private final UserRestService userRestService;
    //private final ModelMapper modelMapper;

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
        return this.memberHistoryRepository.findByWorkspaceIdAndUuidAndRoomHistoryIsNotNull(workspaceId, userId, pageable);
    }

    public RoomHistory getRoomHistory(String workspaceId, String sessionId) {
        return this.roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElse(null);
    }

    @Transactional
    public void removeRoomHistory(List<MemberHistory> memberHistoryList) {
        memberHistoryList.forEach(memberHistory -> {
            if(memberHistory.getRoomHistory() != null) {
                memberHistory.setRoomHistory(null);
                this.memberHistoryRepository.save(memberHistory);
            }
        });
    }

    @Transactional
    public void removeRoomHistory(MemberHistory memberHistory) {
        memberHistory.setRoomHistory(null);
        this.memberHistoryRepository.save(memberHistory);
    }
}

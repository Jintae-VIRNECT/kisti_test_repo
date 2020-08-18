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
import com.virnect.data.dto.rest.UserInfoResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.data.feign.UserRestService;
import com.virnect.data.feign.WorkspaceRestService;
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

    private final WorkspaceRestService workspaceRestService;
    private final UserRestService userRestService;

    private final ModelMapper modelMapper;

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



    public ApiResponse<RoomHistoryInfoListResponse> getRoomHistoryInfoList(String workspaceId, String userId, boolean paging, PageRequest pageable) {
        log.debug("getRoomHistoryInfoList");
        if (!paging) {
            // get all member history by uuid
            List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findByWorkspaceIdAndUuid(workspaceId, userId);

            // Page Metadata
            PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                    .currentPage(0)
                    .currentSize(0)
                    .totalPage(0)
                    .totalElements(0)
                    .build();

            // find specific member has room history
            List<RoomHistory> roomHistoryList = new ArrayList<>();
            memberHistoryList.forEach(memberHistory -> {
                if (memberHistory.getRoomHistory() != null) {
                    roomHistoryList.add(memberHistory.getRoomHistory());
                }
            });

            List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                    .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                    .collect(Collectors.toList());

            // Get Member List by Room Session Ids
            for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                List<MemberHistory> memberList = this.memberHistoryRepository.findAllBySessionId(response.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = memberList.stream()
                        .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                // find and get extra information from use-server using uuid
                if (!memberInfoList.isEmpty()) {
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                        log.debug("getUsers: " + userInfo.getData().toString());

                        memberInfoResponse.setMemberStatus(MemberStatus.UNLOAD);
                        memberInfoResponse.setEmail(userInfo.getData().getEmail());
                        memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                        memberInfoResponse.setLastName(userInfo.getData().getLastName());
                        memberInfoResponse.setNickname(userInfo.getData().getNickname());
                        memberInfoResponse.setProfile(userInfo.getData().getProfile());
                    }

                    Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                        @Override
                        public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                            if(t1.getMemberType().equals(MemberType.LEADER)) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                // Set Member List to Room Information Response
                response.setMemberList(memberInfoList);
            }
            return new ApiResponse<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));

        } else {
            //Page<RoomHistory> roomPage;

            // get all member history by uuid
            //List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllByUuid(userId);

            Page<MemberHistory> memberPage;
            memberPage = this.memberHistoryRepository.findByWorkspaceIdAndUuidAndRoomHistoryIsNotNull(workspaceId, userId, pageable.of());
            // get all member history by uuid
            //List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findByWorkspaceIdAndUuid(workspaceId, userId);

            // find specific member has room history
           /* List<RoomHistory> roomHistoryList = new ArrayList<>();
            memberHistoryList.forEach(memberHistory -> {
                if (memberHistory.getRoomHistory() != null) {
                    roomHistoryList.add(memberHistory.getRoomHistory());
                }
            });*/

            //roomPage = this.roomHistoryRepository.findAll(pageable);
            // find specific member has room history
            List<RoomHistory> roomHistoryList = new ArrayList<>();
            memberPage.getContent().forEach(memberHistory -> {
                //if (memberHistory.getRoomHistory() != null) {
                roomHistoryList.add(memberHistory.getRoomHistory());
                //}
            });

            List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                    .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                    .collect(Collectors.toList());

            /*List<RoomHistoryInfoResponse> roomHistoryInfoList = roomPage.stream()
                    .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                    .collect(Collectors.toList());*/

            /*List<RoomHistoryInfoResponse> roomHistoryInfoList = roomPage.stream()
                    .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                    .collect(Collectors.toList());*/

            // Page Metadata
            PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                    .currentPage(pageable.of().getPageNumber())
                    .currentSize(pageable.of().getPageSize())
                    .totalPage(memberPage.getTotalPages())
                    .totalElements(memberPage.getNumberOfElements())
                    .build();

            //roomInfoList.forEach(info -> log.info("{}", info));
            log.info("Paging Metadata: {}", pageMeta.toString());

            // Get Member List by Room Session Ids
            for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllBySessionId(response.getSessionId());

                // Mapping Member List Data to Member Information List
                List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                        .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                // find and get extra information from use-server using uuid
                if (!memberInfoList.isEmpty()) {
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                        log.debug("getUsers: " + userInfo.getData().toString());

                        memberInfoResponse.setMemberStatus(MemberStatus.UNLOAD);
                        memberInfoResponse.setEmail(userInfo.getData().getEmail());
                        memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                        memberInfoResponse.setLastName(userInfo.getData().getLastName());
                        memberInfoResponse.setNickname(userInfo.getData().getNickname());
                        memberInfoResponse.setProfile(userInfo.getData().getProfile());
                    }
                    Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                        @Override
                        public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                            if(t1.getMemberType().equals(MemberType.LEADER)) {
                                return 1;
                            }
                            return 0;
                        }
                    });

                }
                // Set Member List to Room Information Response
                response.setMemberList(memberInfoList);
                //log.debug("getRoomInfoList: {}", response.toString());
            }
            return new ApiResponse<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));
        }
    }

    @Transactional
    public ApiResponse<RoomHistoryDetailInfoResponse> getRoomHistoryDetailInfo(String workspaceId, String sessionId) {
        log.info("ROOM HISTORY INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
        // Get Specific Room using Session ID
        RoomHistory roomHistory = roomHistoryRepository.findRoomHistoryByWorkspaceIdAndSessionId(workspaceId, sessionId).orElseThrow(() -> new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND));

        // mapping data
        RoomHistoryDetailInfoResponse resultResponse = modelMapper.map(roomHistory, RoomHistoryDetailInfoResponse.class);

        // Get Member List by Room Session ID
        List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllBySessionId(resultResponse.getSessionId());

        // Mapping Member List Data to Member Information List
        List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                .collect(Collectors.toList());

        // find and get extra information from use-server using uuid
        if (!memberInfoList.isEmpty()) {
            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                ApiResponse<UserInfoResponse> userInfo = this.userRestService.getUserInfoByUuid(memberInfoResponse.getUuid());
                log.debug("getUsers: " + userInfo.getData().toString());

                memberInfoResponse.setMemberStatus(MemberStatus.UNLOAD);
                memberInfoResponse.setEmail(userInfo.getData().getEmail());
                memberInfoResponse.setFirstName(userInfo.getData().getFirstName());
                memberInfoResponse.setLastName(userInfo.getData().getLastName());
                memberInfoResponse.setNickname(userInfo.getData().getNickname());
                memberInfoResponse.setProfile(userInfo.getData().getProfile());
            }
            Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                @Override
                public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                    if(t1.getMemberType().equals(MemberType.LEADER)) {
                        return 1;
                    }
                    return 0;
                }
            });
        }
        // Set Member List to Room Detail Information Response
        resultResponse.setMemberList(memberInfoList);

        return new ApiResponse<>(resultResponse);
    }

    @Transactional
    public ApiResponse<ResultResponse> removeAllRoomHistory(String workspaceId, String userId) {
        log.info("ROOM HISTORY INFO DELETE ALL BY USER ID => [{}]", userId);
        ResultResponse resultResponse = new ResultResponse();
        List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findMemberHistoriesByWorkspaceIdAndUuid(workspaceId, userId);
        memberHistoryList.forEach(memberHistory -> {
            if(memberHistory.getRoomHistory() != null) {
                memberHistory.setRoomHistory(null);
                this.memberHistoryRepository.save(memberHistory);
            }
        });
        resultResponse.setResult(true);
        return new ApiResponse<>(resultResponse);
    }

    /*@Transactional
    public ApiResponse<Boolean> removeRoomHistory(String workspaceId, String sessionId, String userId) {
        log.info("ROOM HISTORY INFO DELETE BY USER ID => [{}]", userId);
        MemberHistory memberHistory = this.memberHistoryRepository.findByWorkspaceIdAndSessionIdAndUuid(workspaceId, sessionId, userId).orElseThrow(
                () -> new RemoteServiceException(ErrorCode.ERR_HISTORY_ROOM_MEMBER_NOT_FOUND));
        //if(memberHistoryList.getSessionId().equals(sessionId)) {
        if(memberHistory.getUuid().equals(userId)) {
            memberHistory.setRoomHistory(null);
            this.memberHistoryRepository.save(memberHistory);
        }
        return new ApiResponse<>(true);
    }*/

    @Transactional
    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, RoomHistoryDeleteRequest roomHistoryDeleteRequest) {
        log.info("ROOM HISTORY INFO DELETE BY USER ID => [{}]", roomHistoryDeleteRequest.getUuid());
        ResultResponse resultResponse = new ResultResponse();
        for (String sessionId: roomHistoryDeleteRequest.getSessionIdList()) {
            log.info("ROOM HISTORY INFO DELETE BY SESSION ID => [{}]", sessionId);
            MemberHistory memberHistory = this.memberHistoryRepository.findByWorkspaceIdAndSessionIdAndUuid(workspaceId, sessionId, roomHistoryDeleteRequest.getUuid())
                    .orElseThrow(
                            () -> new RestServiceException(ErrorCode.ERR_HISTORY_ROOM_MEMBER_NOT_FOUND));

            if(memberHistory.getUuid().equals(roomHistoryDeleteRequest.getUuid())) {
                memberHistory.setRoomHistory(null);
                this.memberHistoryRepository.save(memberHistory);
            }
        }
        resultResponse.setResult(true);
        return new ApiResponse<>(resultResponse);
    }
}

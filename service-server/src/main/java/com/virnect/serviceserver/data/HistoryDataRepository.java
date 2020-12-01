package com.virnect.serviceserver.data;

import com.google.gson.JsonObject;
import com.virnect.data.dao.*;
import com.virnect.service.ApiResponse;
import com.virnect.service.dto.DeleteResultResponse;
import com.virnect.service.dto.PageMetadataResponse;
import com.virnect.service.dto.PageRequest;
import com.virnect.service.dto.feign.WorkspaceMemberInfoResponse;
import com.virnect.service.dto.service.request.RoomHistoryDeleteRequest;
import com.virnect.service.dto.service.response.MemberInfoResponse;
import com.virnect.service.dto.service.response.RoomHistoryDetailInfoResponse;
import com.virnect.service.dto.service.response.RoomHistoryInfoListResponse;
import com.virnect.service.dto.service.response.RoomHistoryInfoResponse;
import com.virnect.service.error.ErrorCode;
import com.virnect.serviceserver.utils.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryDataRepository extends DataRepository {
    private static final String TAG = HistoryDataRepository.class.getSimpleName();

    //========================================= ROOM HISTORY INFORMATION RELATION ===========================================//
    public ApiResponse<RoomHistoryInfoListResponse> loadRoomHistoryPageList(
            String workspaceId,
            String userId,
            Pageable pageable) {
        return new RepoDecoder<Page<RoomHistory>, RoomHistoryInfoListResponse>(RepoDecoderType.READ) {
            String sessionId;

            private Page<RoomHistory> toPaging(Page<MemberHistory> memberPage) {
                // find specific member has room history and room history is not null
                List<RoomHistory> roomHistoryList = memberPage.getContent().stream()
                        .map(MemberHistory::getRoomHistory)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());


                return new PageImpl<RoomHistory>(roomHistoryList, pageable, roomHistoryList.size());
            }

            @Override
            Page<RoomHistory> loadFromDatabase() {
                return historyService.getRoomHistory(workspaceId, pageable);
                //return historyService.getMemberHistoryList(workspaceId, userId, pageable);
            }

            @Override
            DataProcess<RoomHistoryInfoListResponse> invokeDataProcess() {
                // get all member history by uuid
                //Page<MemberHistory> memberPage = loadFromDatabase();
                Page<RoomHistory> roomHistoryPage = loadFromDatabase();

                // find specific member has room history and room history is not null
                /*List<RoomHistory> roomHistoryList = memberPage.getContent().stream()
                        .map(MemberHistory::getRoomHistory)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());*/

                // Page Metadata
                PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                        .currentPage(pageable.getPageNumber())
                        .currentSize(pageable.getPageSize())
                        .numberOfElements(roomHistoryPage.getNumberOfElements())
                        .totalPage(roomHistoryPage.getTotalPages())
                        .totalElements(roomHistoryPage.getTotalElements())
                        .last(roomHistoryPage.isLast())
                        .build();

                Map<RoomHistory, List<MemberHistory>> roomHistoryListMap = roomHistoryPage.getContent().stream()
                        .filter(roomHistory -> {
                            for(MemberHistory memberHistory : roomHistory.getMemberHistories()) {
                                if(memberHistory.getUuid().equals(userId) && memberHistory.getRoomHistory() != null)
                                    return true;
                            }
                            return false;
                        })
                        .collect(Collectors.toMap(roomHistory -> roomHistory, RoomHistory::getMemberHistories));

                List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
                //for (RoomHistory roomHistory : roomHistoryList) {
                for (RoomHistory roomHistory : roomHistoryListMap.keySet()) {
                    //sessionId = roomHistory.getSessionId();

                    RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
                    roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

                    List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
                            .filter(memberHistory -> !memberHistory.getMemberType().equals(MemberType.SECESSION))
                            .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                            .collect(Collectors.toList());

                    //List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(sessionId);
                    // Mapping Member List Data to Member Information List
                    /*List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                            .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // remove members who is evicted
                    //memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                    // find and get extra information from use-server using uuid
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        if(memberInfoResponse.getMemberType().equals(MemberType.LEADER)) {
                            ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                            log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                            //todo://user infomation does not have role and role id change to workspace member info
                            WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                            memberInfoResponse.setRole(workspaceMemberData.getRole());
                            //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                            memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                            memberInfoResponse.setName(workspaceMemberData.getName());
                            memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                            memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                        }
                    }

                    Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                        @Override
                        public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                            if (t1.getMemberType().equals(MemberType.LEADER)) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    // Set Member List to Room Information Response
                    roomHistoryInfoResponse.setMemberList(memberInfoList);

                    roomHistoryInfoList.add(roomHistoryInfoResponse);
                }


                return new DataProcess<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomHistoryInfoListResponse> loadRoomHistoryList(
            String workspaceId,
            String userId,
            Pageable pageable) {
        return new RepoDecoder<Page<RoomHistory>, RoomHistoryInfoListResponse>(RepoDecoderType.READ) {
            String sessionId;

            @Override
            Page<RoomHistory> loadFromDatabase() {
                return historyService.getRoomHistory(workspaceId, pageable);
            }

            @Override
            DataProcess<RoomHistoryInfoListResponse> invokeDataProcess() {
                // get all member history by uuid
                //PageRequest pageRequest = new PageRequest();
                //Page<MemberHistory> memberPage = historyService.getMemberHistoryList(workspaceId, userId, pageRequest.of());
                Page<RoomHistory> roomHistoryPage = loadFromDatabase();


                // find specific member has room history and room history is not null
                /*List<RoomHistory> roomHistoryList = memberPage.getContent().stream()
                        .map(MemberHistory::getRoomHistory)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());*/

                // Page Metadata
                PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                        .currentPage(0)
                        .currentSize(0)
                        .numberOfElements(roomHistoryPage.getNumberOfElements())
                        .totalPage(roomHistoryPage.getTotalPages())
                        .totalElements(roomHistoryPage.getTotalElements())
                        .last(roomHistoryPage.isLast())
                        .build();

                Map<RoomHistory, List<MemberHistory>> roomHistoryListMap = roomHistoryPage.getContent().stream()
                        .filter(roomHistory -> {
                            for(MemberHistory memberHistory : roomHistory.getMemberHistories()) {
                                if(memberHistory.getUuid().equals(userId) && memberHistory.getRoomHistory() != null)
                                    return true;
                            }
                            return false;
                        })
                        .collect(Collectors.toMap(roomHistory -> roomHistory, RoomHistory::getMemberHistories));


                List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
                //for (RoomHistory roomHistory : roomHistoryList) {
                for (RoomHistory roomHistory : roomHistoryListMap.keySet()) {
                    //sessionId = roomHistory.getSessionId();

                    RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
                    roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

                    //List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(sessionId);
                    // Mapping Member List Data to Member Information List
                    /*List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                            .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // remove members who is evicted
                    //memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                    List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
                            .filter(memberHistory -> !memberHistory.getMemberType().equals(MemberType.SECESSION))
                            .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                            .collect(Collectors.toList());

                    // find and get extra information from use-server using uuid
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        if(memberInfoResponse.getMemberType().equals(MemberType.LEADER)) {
                            ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                            log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                            //todo://user infomation does not have role and role id change to workspace member info
                            WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                            memberInfoResponse.setRole(workspaceMemberData.getRole());
                            //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                            memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                            memberInfoResponse.setName(workspaceMemberData.getName());
                            memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                            memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                        }
                    }

                    Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                        @Override
                        public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                            if (t1.getMemberType().equals(MemberType.LEADER)) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                    // Set Member List to Room Information Response
                    roomHistoryInfoResponse.setMemberList(memberInfoList);

                    roomHistoryInfoList.add(roomHistoryInfoResponse);
                }
                return new DataProcess<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));
            }

            private void fetchFromRepository() {

            }
        }.asApiResponse();
    }

    public ApiResponse<RoomHistoryDetailInfoResponse> loadRoomHistoryDetail(String workspaceId, String sessionId) {
        return new RepoDecoder<RoomHistory, RoomHistoryDetailInfoResponse>(RepoDecoderType.READ) {

            List<MemberInfoResponse> memberInfoList;

            @Override
            RoomHistory loadFromDatabase() {
                return historyService.getRoomHistory(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomHistoryDetailInfoResponse> invokeDataProcess() {
                LogMessage.formedInfo(
                        TAG,
                        "invokeDataProcess",
                        "loadRoomHistoryDetail",
                        "room history detail info retrieve by session id",
                        sessionId
                );

                RoomHistory roomHistory = loadFromDatabase();
                if(roomHistory == null) {
                    RoomHistoryDetailInfoResponse empty = new RoomHistoryDetailInfoResponse();
                    return new DataProcess<>(empty, ErrorCode.ERR_ROOM_NOT_FOUND);
                } else {
                    // mapping data
                    RoomHistoryDetailInfoResponse resultResponse = modelMapper.map(roomHistory, RoomHistoryDetailInfoResponse.class);
                    resultResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

                    // Get Member List by Room Session ID
                    // Mapping Member List Data to Member Information List
                    memberInfoList = historyService.getMemberHistoryList(resultResponse.getSessionId())
                            .stream()
                            .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                            .collect(Collectors.toList());

                    // remove members who is evicted
                    memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                    // find and get extra information from use-server using uuid
                    fetchFromRepository();

                    // Set Member List to Room Detail Information Response
                    resultResponse.setMemberList(memberInfoList);
                    return new DataProcess<>(resultResponse);
                }

            }

            private void fetchFromRepository() {
                if (!memberInfoList.isEmpty()) {
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                        //todo://user infomation does not have role and role id change to workspace member info
                        WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                        memberInfoResponse.setRole(workspaceMemberData.getRole());
                        //memberInfoResponse.setRoleId(workspaceMemberData.getRoleId());
                        memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                        memberInfoResponse.setName(workspaceMemberData.getName());
                        memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                        memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                    }
                    memberInfoList.sort((t1, t2) -> {
                        if (t1.getMemberType().equals(MemberType.LEADER)) {
                            return 1;
                        }
                        return 0;
                    });
                }
            }
        }.asApiResponse();
    }

    /**
     *
     * @param workspaceId
     * @param userId
     * @return
     */
    public ApiResponse<DeleteResultResponse> removeRoomHistory(String workspaceId, String userId) {
        return new RepoDecoder<List<MemberHistory>, DeleteResultResponse>(RepoDecoderType.DELETE) {
            @Override
            List<MemberHistory> loadFromDatabase() {
                return historyService.getMemberHistoryList(workspaceId, userId);
            }

            @Override
            DataProcess<DeleteResultResponse> invokeDataProcess() {
                LogMessage.formedInfo(
                        TAG,
                        "invokeDataProcess",
                        "removeRoomHistory",
                        "room history info delete all by user id",
                        userId
                );

                List<MemberHistory> memberHistoryList = loadFromDatabase();
                historyService.removeRoomHistory(memberHistoryList);
                DeleteResultResponse deleteResultResponse = processResult();
                return new DataProcess<>(deleteResultResponse);
            }

            private DeleteResultResponse processResult() {
                DeleteResultResponse deleteResultResponse = new DeleteResultResponse();
                deleteResultResponse.userId = userId;
                deleteResultResponse.setResult(true);

                return deleteResultResponse;
            }
        }.asApiResponse();
    }

    public ApiResponse<DeleteResultResponse> removeRoomHistory(String workspaceId, RoomHistoryDeleteRequest roomHistoryDeleteRequest) {
        return new RepoDecoder<MemberHistory, DeleteResultResponse>(RepoDecoderType.DELETE) {
            private String sessionId;
            private String userId;

            @Override
            MemberHistory loadFromDatabase() {
                return historyService.getMemberHistory(workspaceId, sessionId, userId);
            }
            @Override
            DataProcess<DeleteResultResponse> invokeDataProcess() {
                LogMessage.formedInfo(
                        TAG,
                        "invokeDataProcess",
                        "removeRoomHistory",
                        "some room history info delete by user id",
                        roomHistoryDeleteRequest.getUuid()
                );

                for (String sessionId: roomHistoryDeleteRequest.getSessionIdList()) {
                    this.sessionId = sessionId;
                    this.userId = roomHistoryDeleteRequest.getUuid();
                    MemberHistory memberHistory = loadFromDatabase();
                    if(memberHistory != null) {
                        historyService.removeRoomHistory(memberHistory);
                    } else {
                        //not send error polling
                        LogMessage.formedInfo(
                                TAG,
                                "invokeDataProcess",
                                "removeRoomHistory",
                                "room history info delete but member history data is null by session id",
                                roomHistoryDeleteRequest.getUuid()
                        );
                    }
                }
                DeleteResultResponse deleteResultResponse = processResult();
                return new DataProcess<>(deleteResultResponse);
            }

            private DeleteResultResponse processResult() {
                DeleteResultResponse deleteResultResponse = new DeleteResultResponse();
                deleteResultResponse.userId = roomHistoryDeleteRequest.getUuid();
                deleteResultResponse.setResult(true);

                return deleteResultResponse;
            }
        }.asApiResponse();
    }
    /*public DataProcess<JsonObject> requestRoom() {
        return new RepoDecoder<JsonObject, JsonObject>() {

        }.asResponseData();
    }*/
}

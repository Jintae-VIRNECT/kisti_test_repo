package com.virnect.serviceserver.data;

import com.virnect.data.dao.MemberHistory;
import com.virnect.data.dao.MemberStatus;
import com.virnect.data.dao.MemberType;
import com.virnect.data.dao.RoomHistory;
import com.virnect.data.service.HistoryService;
import com.virnect.service.ApiResponse;
import com.virnect.service.dto.PageMetadataResponse;
import com.virnect.service.dto.PageRequest;
import com.virnect.service.dto.ResultResponse;
import com.virnect.service.dto.feign.WorkspaceMemberInfoResponse;
import com.virnect.service.dto.service.request.RoomHistoryDeleteRequest;
import com.virnect.service.dto.service.response.MemberInfoResponse;
import com.virnect.service.dto.service.response.RoomHistoryDetailInfoResponse;
import com.virnect.service.dto.service.response.RoomHistoryInfoListResponse;
import com.virnect.service.dto.service.response.RoomHistoryInfoResponse;
import com.virnect.service.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryDataRepository extends DataRepository {
    private static final String TAG = HistoryDataRepository.class.getSimpleName();



    //========================================= ROOM HISTORY INFORMATION RELATION ===========================================//
    public ApiResponse<RoomHistoryInfoListResponse> loadRoomHistoryInfoList(
            String workspaceId,
            String userId,
            boolean paging,
            Pageable pageable) {
        return new RepoDecoder<MemberHistory, RoomHistoryInfoListResponse>(RepoDecoderType.READ) {
            @Override
            MemberHistory loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<RoomHistoryInfoListResponse> invokeDataProcess() {
                if (!paging) {
                    // get all member history by uuid
                    PageRequest pageRequest = new PageRequest();
                    Page<MemberHistory> memberPage = historyService.getMemberHistoryList(workspaceId, userId, pageRequest.of());

                    List<RoomHistory> roomHistoryList = new ArrayList<>();
                    memberPage.getContent().forEach(memberHistory -> {
                        roomHistoryList.add(memberHistory.getRoomHistory());
                    });


                    List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
                    for(RoomHistory roomHistory: roomHistoryList) {
                        RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
                        roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());
                        roomHistoryInfoList.add(roomHistoryInfoResponse);
                    }


                    /*List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                            .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // find specific member has room history
                    /*List<RoomHistory> roomHistoryList = new ArrayList<>();
                    memberHistoryList.forEach(memberHistory -> {
                        if (memberHistory.getRoomHistory() != null) {
                            roomHistoryList.add(memberHistory.getRoomHistory());
                        }
                    });

                    List<RoomHistoryInfoResponse> roomHistoryInfoList = roomHistoryList.stream()
                            .map(roomHistory -> modelMapper.map(roomHistory, RoomHistoryInfoResponse.class))
                            .collect(Collectors.toList());*/

                    // Page Metadata
                    PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                            .currentPage(0)
                            .currentSize(0)
                            .numberOfElements(memberPage.getNumberOfElements())
                            .totalPage(memberPage.getTotalPages())
                            .totalElements(memberPage.getTotalElements())
                            .last(memberPage.isLast())
                            .build();

                    // Get Member List by Room Session Ids
                    for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                        List<MemberHistory> memberList = historyService.getMemberHistoryList(response.getSessionId());

                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = memberList.stream()
                                .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from use-server using uuid
                        if (!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
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

                            Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                                @Override
                                public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                                    if (t1.getMemberType().equals(MemberType.LEADER)) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            });
                        }
                        // Set Member List to Room Information Response
                        response.setMemberList(memberInfoList);
                    }
                    return new DataProcess<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));

                } else {
                    //Page<RoomHistory> roomPage;
                    // get all member history by uuid
                    //List<MemberHistory> memberHistoryList = this.memberHistoryRepository.findAllByUuid(userId);
                    Page<MemberHistory> memberPage = historyService.getMemberHistoryList(workspaceId, userId, pageable);
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
                        roomHistoryList.add(memberHistory.getRoomHistory());
                    });

                    List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
                    for(RoomHistory roomHistory: roomHistoryList) {
                        RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
                        roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());
                        roomHistoryInfoList.add(roomHistoryInfoResponse);
                    }

                    // Page Metadata
                    PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                            .currentPage(pageable.getPageNumber())
                            .currentSize(pageable.getPageSize())
                            .numberOfElements(memberPage.getNumberOfElements())
                            .totalPage(memberPage.getTotalPages())
                            .totalElements(memberPage.getTotalElements())
                            .last(memberPage.isLast())
                            .build();

                    //roomInfoList.forEach(info -> log.info("{}", info));
                    log.info("Paging Metadata: {}", pageMeta.toString());

                    // Get Member List by Room Session Ids
                    for (RoomHistoryInfoResponse response : roomHistoryInfoList) {
                        List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(response.getSessionId());

                        // Mapping Member List Data to Member Information List
                        List<MemberInfoResponse> memberInfoList = memberHistoryList.stream()
                                .map(memberHistory -> modelMapper.map(memberHistory, MemberInfoResponse.class))
                                .collect(Collectors.toList());

                        // remove members who is evicted
                        memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                        // find and get extra information from use-server using uuid
                        if (!memberInfoList.isEmpty()) {
                            for (MemberInfoResponse memberInfoResponse : memberInfoList) {
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
                            Collections.sort(memberInfoList, new Comparator<MemberInfoResponse>() {
                                @Override
                                public int compare(MemberInfoResponse t1, MemberInfoResponse t2) {
                                    if (t1.getMemberType().equals(MemberType.LEADER)) {
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
                    return new DataProcess<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomHistoryDetailInfoResponse> loadRoomHistoryDetail(String workspaceId, String sessionId) {
        return new RepoDecoder<RoomHistory, RoomHistoryDetailInfoResponse>(RepoDecoderType.READ) {
            @Override
            RoomHistory loadFromDatabase() {
                return historyService.getRoomHistory(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomHistoryDetailInfoResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO RETRIEVE BY SESSION ID => [{}]", sessionId);
                //ApiResponse<RoomHistoryDetailInfoResponse> response = new ApiResponse<>();
                RoomHistory roomHistory = loadFromDatabase();
                if(roomHistory == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                    /*response.setErrorResponseData(ErrorCode.ERR_ROOM_NOT_FOUND);
                    return response;*/
                } else {
                    // mapping data
                    RoomHistoryDetailInfoResponse resultResponse = modelMapper.map(roomHistory, RoomHistoryDetailInfoResponse.class);
                    resultResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

                    // Get Member List by Room Session ID
                    // Mapping Member List Data to Member Information List
                    List<MemberInfoResponse> memberInfoList = historyService.getMemberHistoryList(resultResponse.getSessionId())
                            .stream()
                            .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                            .collect(Collectors.toList());

                    // remove members who is evicted
                    memberInfoList.removeIf(memberInfoResponse -> memberInfoResponse.getMemberStatus().equals(MemberStatus.EVICTED));

                    // find and get extra information from use-server using uuid
                    if (!memberInfoList.isEmpty()) {
                        for (MemberInfoResponse memberInfoResponse : memberInfoList) {
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
                    return new DataProcess<>(resultResponse);
                }

            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, String userId) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO DELETE ALL BY USER ID => [{}]", userId);
                ResultResponse resultResponse = new ResultResponse();
                List<MemberHistory> memberHistoryList = historyService.getMemberHistoryList(workspaceId, userId);
                historyService.removeRoomHistory(memberHistoryList);
                resultResponse.setResult(true);
                return new DataProcess<>(resultResponse);
            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, RoomHistoryDeleteRequest roomHistoryDeleteRequest) {
        return new RepoDecoder<Void, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            Void loadFromDatabase() {
                return null;
            }
            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                log.info("ROOM HISTORY INFO DELETE BY USER ID => [{}]", roomHistoryDeleteRequest.getUuid());
                ResultResponse resultResponse = new ResultResponse();
                //resultResponse.setResult(false);
                //ApiResponse<ResultResponse> apiResponse = new ApiResponse<>(resultResponse);
                for (String sessionId: roomHistoryDeleteRequest.getSessionIdList()) {
                    log.info("ROOM HISTORY INFO DELETE BY SESSION ID => [{}]", sessionId);
                    MemberHistory memberHistory = historyService.getMemberHistory(workspaceId, sessionId, roomHistoryDeleteRequest.getUuid());
                    if(memberHistory == null) {
                        log.info("ROOM HISTORY INFO DELETE BUT MEMBER HISTORY DATA IS NULL BY SESSION ID => [{}]", sessionId);
                        //apiResponse.getData().setResult(false);
                        //apiResponse.setErrorResponseData(ErrorCode.ERR_HISTORY_ROOM_MEMBER_NOT_FOUND);
                    } else {
                        if (memberHistory.getUuid().equals(roomHistoryDeleteRequest.getUuid())) {
                            historyService.removeRoomHistory(memberHistory);
                        }
                    }
                }
                resultResponse.setResult(true);
                return new DataProcess<>(resultResponse);
            }
        }.asApiResponse();
    }
    /*public DataProcess<JsonObject> requestRoom() {
        return new RepoDecoder<JsonObject, JsonObject>() {

        }.asResponseData();
    }*/
}

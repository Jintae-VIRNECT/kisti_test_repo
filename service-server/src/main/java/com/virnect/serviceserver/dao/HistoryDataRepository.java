package com.virnect.serviceserver.dao;

import com.virnect.data.dao.*;
import com.virnect.serviceserver.global.common.ApiResponse;
import com.virnect.serviceserver.dto.response.PageMetadataResponse;
import com.virnect.serviceserver.dto.response.ResultResponse;
import com.virnect.serviceserver.dto.rest.WorkspaceMemberInfoListResponse;
import com.virnect.serviceserver.dto.rest.WorkspaceMemberInfoResponse;
import com.virnect.serviceserver.dto.request.room.RoomHistoryDeleteRequest;
import com.virnect.serviceserver.dto.response.member.MemberInfoResponse;
import com.virnect.serviceserver.dto.response.room.RoomHistoryDetailInfoResponse;
import com.virnect.serviceserver.dto.response.room.RoomHistoryInfoListResponse;
import com.virnect.serviceserver.dto.response.room.RoomHistoryInfoResponse;
import com.virnect.serviceserver.error.ErrorCode;
import com.virnect.serviceserver.infra.utils.LogMessage;
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
        return new RepoDecoder<Page<MemberHistory>, RoomHistoryInfoListResponse>(RepoDecoderType.READ) {
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
            Page<MemberHistory> loadFromDatabase() {
                return historyService.getMemberHistoryList(workspaceId, userId, pageable);
            }

            @Override
            DataProcess<RoomHistoryInfoListResponse> invokeDataProcess() {
                // get all member history by uuid
                Page<MemberHistory> memberHistoryPage = loadFromDatabase();


                // Page Metadata
                PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                        .currentPage(pageable.getPageNumber())
                        .currentSize(pageable.getPageSize())
                        .numberOfElements(memberHistoryPage.getNumberOfElements())
                        .totalPage(memberHistoryPage.getTotalPages())
                        .totalElements(memberHistoryPage.getTotalElements())
                        .last(memberHistoryPage.isLast())
                        .build();

                // find specific member has room history and room history is not null
                /*Map<RoomHistory, List<MemberHistory>> roomHistoryListMap = memberHistoryPage.getContent().stream()
                        .sorted((memberHistory, t1) -> memberHistory.getEndDate().compareTo(t1.getEndDate()))
                        .collect(Collectors.toMap(MemberHistory::getRoomHistory, memberHistory -> memberHistory.getRoomHistory().getMemberHistories()));*/

                List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
                for (MemberHistory memberHistory : memberHistoryPage.getContent()) {
                    RoomHistory roomHistory = memberHistory.getRoomHistory();
                    RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
                    roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

                    List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
                            .filter(member-> !member.getMemberType().equals(MemberType.SECESSION))
                            .map(member -> modelMapper.map(member, MemberInfoResponse.class))
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

                    memberInfoList.sort((t1, t2) -> {
                        if (t1.getMemberType().equals(MemberType.LEADER)) {
                            return 1;
                        }
                        return 0;
                    });
                    // Set Member List to Room Information Response
                    roomHistoryInfoResponse.setMemberList(memberInfoList);

                    roomHistoryInfoList.add(roomHistoryInfoResponse);
                }

                return new DataProcess<>(new RoomHistoryInfoListResponse(roomHistoryInfoList, pageMeta));
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomHistoryInfoListResponse> searchRoomHistoryPageList(
            String workspaceId,
            String userId,
            String search,
            Pageable pageable) {
        return new RepoDecoder<Page<RoomHistory>, RoomHistoryInfoListResponse>(RepoDecoderType.READ) {
            List<MemberInfoResponse> memberInfoList = new ArrayList<>();

            private List<MemberInfoResponse> fetchFromRepository() {
                // fetch workspace member information
                ApiResponse<WorkspaceMemberInfoListResponse> feignResponse = workspaceRestService.getWorkspaceMemberInfoList(
                        workspaceId,
                        "remote",
                        search,
                        pageable.getPageNumber(),
                        pageable.getPageSize()
                );

                List<WorkspaceMemberInfoResponse> workspaceMemberInfoList = feignResponse.getData().getMemberInfoList();
                List<MemberInfoResponse> memberInfoList = workspaceMemberInfoList.stream()
                        .map(memberInfo -> modelMapper.map(memberInfo, MemberInfoResponse.class))
                        .collect(Collectors.toList());

                log.info("fetchFromRepository::searchRoomHistoryPageList::getPageMeta:: {}", feignResponse.getData().getPageMeta());

                for (MemberInfoResponse memberInfoResponse: memberInfoList) {
                    log.info("fetchFromRepository::searchRoomHistoryPageList:: {}", memberInfoResponse.toString());
                }

                return memberInfoList;
            }

            @Override
            Page<RoomHistory> loadFromDatabase() {
                List<String> userIds = new ArrayList<>();
                for (MemberInfoResponse memberInfo: memberInfoList) {
                    if(memberInfo.getUuid() == null || memberInfo.getUuid().isEmpty()) {
                        //if memberInfo is empty
                        log.info("loadFromDatabase::searchRoomHistoryPageList:: some member dose not have uuid");
                    } else {
                        userIds.add(memberInfo.getUuid());
                    }
                }

                if(userIds.isEmpty()) {
                    log.info("loadFromDatabase::searchRoomHistoryPageList::memberInfoList is empty can not find, search with room title");
                    return historyService.getRoomHistory(workspaceId, userId, search, pageable);
                } else {
                    log.info("loadFromDatabase::searchRoomHistoryPageList::memberInfoList is not empty");
                    return historyService.getRoomHistory(workspaceId, userId, userIds, search, pageable);
                }
            }

            @Override
            DataProcess<RoomHistoryInfoListResponse> invokeDataProcess() {
                memberInfoList = fetchFromRepository();

                // get all member history by uuid
                Page<RoomHistory> roomHistoryPage = loadFromDatabase();

                /*for (RoomHistory roomHistory: roomHistoryPage.getContent()) {
                    log.info("searchRoomHistoryPageList :: {}, {}", search, roomHistory.getTitle());
                    for (MemberHistory m : roomHistory.getMemberHistories()) {
                        log.info("searchRoomHistoryPageList :: Members :: {}, {}", m.getWorkspaceId(), m.getUuid());
                    }
                }*/

                // Page Metadata
                PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                        .currentPage(pageable.getPageNumber())
                        .currentSize(pageable.getPageSize())
                        .numberOfElements(roomHistoryPage.getNumberOfElements())
                        .totalPage(roomHistoryPage.getTotalPages())
                        .totalElements(roomHistoryPage.getTotalElements())
                        .last(roomHistoryPage.isLast())
                        .build();

                List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
                for (RoomHistory roomHistory : roomHistoryPage.getContent()) {
                    RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
                    roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

                    List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
                            .filter(member-> !member.getMemberType().equals(MemberType.SECESSION))
                            .map(member -> modelMapper.map(member, MemberInfoResponse.class))
                            .collect(Collectors.toList());

                    // find and get extra information from use-server using uuid
                    for (MemberInfoResponse memberInfoResponse : memberInfoList) {
                        if(memberInfoResponse.getMemberType().equals(MemberType.LEADER)) {
                            ApiResponse<WorkspaceMemberInfoResponse> workspaceMemberInfo = workspaceRestService.getWorkspaceMemberInfo(workspaceId, memberInfoResponse.getUuid());
                            log.debug("workspaceMemberInfo: " + workspaceMemberInfo.getData().toString());
                            WorkspaceMemberInfoResponse workspaceMemberData = workspaceMemberInfo.getData();
                            memberInfoResponse.setRole(workspaceMemberData.getRole());
                            memberInfoResponse.setEmail(workspaceMemberData.getEmail());
                            memberInfoResponse.setName(workspaceMemberData.getName());
                            memberInfoResponse.setNickName(workspaceMemberData.getNickName());
                            memberInfoResponse.setProfile(workspaceMemberData.getProfile());
                        }
                    }

                    memberInfoList.sort((t1, t2) -> {
                        if (t1.getMemberType().equals(MemberType.LEADER)) {
                            return 1;
                        }
                        return 0;
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
        return new RepoDecoder<Page<MemberHistory>, RoomHistoryInfoListResponse>(RepoDecoderType.READ) {
            String sessionId;

            @Override
            Page<MemberHistory> loadFromDatabase() {
                return historyService.getMemberHistoryList(workspaceId, userId, pageable);
            }

            @Override
            DataProcess<RoomHistoryInfoListResponse> invokeDataProcess() {
                // get all member history by uuid
                Page<MemberHistory> memberHistoryPage = loadFromDatabase();

                // Page Metadata
                PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                        .currentPage(0)
                        .currentSize(0)
                        .numberOfElements(memberHistoryPage.getNumberOfElements())
                        .totalPage(memberHistoryPage.getTotalPages())
                        .totalElements(memberHistoryPage.getTotalElements())
                        .last(memberHistoryPage.isLast())
                        .build();

                // find specific member has room history and room history is not null

                List<RoomHistoryInfoResponse> roomHistoryInfoList = new ArrayList<>();
                for (MemberHistory memberHistory : memberHistoryPage.getContent()) {
                    RoomHistory roomHistory = memberHistory.getRoomHistory();
                    RoomHistoryInfoResponse roomHistoryInfoResponse = modelMapper.map(roomHistory, RoomHistoryInfoResponse.class);
                    roomHistoryInfoResponse.setSessionType(roomHistory.getSessionPropertyHistory().getSessionType());

                    List<MemberInfoResponse> memberInfoList = roomHistory.getMemberHistories().stream()
                            .filter(member -> !member.getMemberType().equals(MemberType.SECESSION))
                            .map(member -> modelMapper.map(member, MemberInfoResponse.class))
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

                    memberInfoList.sort((t1, t2) -> {
                        if (t1.getMemberType().equals(MemberType.LEADER)) {
                            return 1;
                        }
                        return 0;
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
    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, String userId) {
        return new RepoDecoder<List<MemberHistory>, ResultResponse>(RepoDecoderType.DELETE) {
            @Override
            List<MemberHistory> loadFromDatabase() {
                return historyService.getMemberHistoryList(workspaceId, userId);
            }

            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
                LogMessage.formedInfo(
                        TAG,
                        "invokeDataProcess",
                        "removeRoomHistory",
                        "room history info delete all by user id",
                        userId
                );

                List<MemberHistory> memberHistoryList = loadFromDatabase();
                historyService.removeRoomHistory(memberHistoryList);
                ResultResponse resultResponse = processResult();
                return new DataProcess<>(resultResponse);
            }

            private ResultResponse processResult() {
                ResultResponse resultResponse = new ResultResponse();
                resultResponse.userId = userId;
                resultResponse.setResult(true);

                return resultResponse;
            }
        }.asApiResponse();
    }

    public ApiResponse<ResultResponse> removeRoomHistory(String workspaceId, RoomHistoryDeleteRequest roomHistoryDeleteRequest) {
        return new RepoDecoder<MemberHistory, ResultResponse>(RepoDecoderType.DELETE) {
            private String sessionId;
            private String userId;

            @Override
            MemberHistory loadFromDatabase() {
                return historyService.getMemberHistory(workspaceId, sessionId, userId);
            }
            @Override
            DataProcess<ResultResponse> invokeDataProcess() {
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
                ResultResponse resultResponse = processResult();
                return new DataProcess<>(resultResponse);
            }

            private ResultResponse processResult() {
                ResultResponse resultResponse = new ResultResponse();
                resultResponse.userId = roomHistoryDeleteRequest.getUuid();
                resultResponse.setResult(true);

                return resultResponse;
            }
        }.asApiResponse();
    }
    /*public DataProcess<JsonObject> requestRoom() {
        return new RepoDecoder<JsonObject, JsonObject>() {

        }.asResponseData();
    }*/
}

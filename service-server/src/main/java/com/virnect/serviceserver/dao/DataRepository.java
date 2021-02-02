package com.virnect.serviceserver.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.client.RemoteServiceException;
import com.virnect.client.RemoteServiceException.Code;
import com.virnect.data.dao.*;
import com.virnect.data.service.HistoryService;
import com.virnect.mediaserver.core.EndReason;
import com.virnect.mediaserver.core.Participant;
import com.virnect.service.ApiResponse;
import com.virnect.service.FileService;
import com.virnect.service.SessionService;
import com.virnect.serviceserver.dto.constraint.LicenseConstants;
import com.virnect.serviceserver.dto.constraint.LicenseItem;
import com.virnect.serviceserver.dto.rest.LicenseInfoListResponse;
import com.virnect.serviceserver.dto.rest.LicenseInfoResponse;
import com.virnect.serviceserver.dto.rest.StopRecordingResponse;
import com.virnect.serviceserver.dto.rest.UserInfoResponse;
import com.virnect.serviceserver.dto.rpc.ClientMetaData;
import com.virnect.serviceserver.error.ErrorCode;
import com.virnect.serviceserver.error.exception.RestServiceException;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.application.license.LicenseRestService;
import com.virnect.serviceserver.application.record.RecordRestService;
import com.virnect.serviceserver.application.user.UserRestService;
import com.virnect.serviceserver.application.workspace.WorkspaceRestService;
import com.virnect.serviceserver.infra.utils.LogMessage;
import com.virnect.serviceserver.infra.utils.PushMessageClient;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class DataRepository {
    private static final String TAG = DataRepository.class.getSimpleName();

    protected RemoteServiceConfig config;
    protected SessionService sessionService;
    protected HistoryService historyService;
    protected FileService fileService;

    protected PushMessageClient pushMessageClient;

    protected WorkspaceRestService workspaceRestService;
    protected UserRestService userRestService;
    protected LicenseRestService licenseRestService;
    protected RecordRestService recordRestService;
    protected ModelMapper modelMapper;

    @Autowired
    public void setConfig(RemoteServiceConfig remoteServiceConfig) {
        this.config = remoteServiceConfig;
    }

    @Qualifier(value = "sessionService")
    @Autowired
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Qualifier(value = "historyService")
    @Autowired
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Qualifier(value = "fileService")
    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Qualifier(value = "pushMessageClient")
    @Autowired
    public void setPushMessageClient(PushMessageClient pushMessageClient) {
        this.pushMessageClient = pushMessageClient;
    }

    @Autowired
    public void setWorkspaceRestService(WorkspaceRestService workspaceRestService) {
        this.workspaceRestService = workspaceRestService;
    }

    @Autowired
    public void setUserRestService(UserRestService userRestService) {
        this.userRestService = userRestService;
    }

    @Autowired
    public void setLicenseRestService(LicenseRestService licenseRestService) {
        this.licenseRestService = licenseRestService;
    }

    @Autowired
    public void setRecordRestService(RecordRestService recordRestService) {
        this.recordRestService = recordRestService;
    }

    @Qualifier(value = "modelMapper")
    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public DataProcess<UserInfoResponse> checkUserValidation(String userId) {
        return new RepoDecoder<ApiResponse<UserInfoResponse>, UserInfoResponse>(RepoDecoderType.FETCH) {
            @Override
            ApiResponse<UserInfoResponse> loadFromDatabase() {
                return userRestService.getUserInfoByUserId(userId);
            }

            @Override
            DataProcess<UserInfoResponse> invokeDataProcess() {
                ApiResponse<UserInfoResponse> feignResponse = loadFromDatabase();
                //todo:check something?

                return new DataProcess<>(feignResponse.getData());
            }
        }.asResponseData();
    }

    public DataProcess<LicenseItem> checkLicenseValidation(String workspaceId, String userId) {
        return new RepoDecoder<DataProcess<LicenseInfoResponse>, LicenseItem>(RepoDecoderType.FETCH) {
            @Override
            DataProcess<LicenseInfoResponse> loadFromDatabase() {
                ApiResponse<LicenseInfoListResponse> licenseValidation = licenseRestService.getUserLicenseValidation(workspaceId, userId);
                if(licenseValidation.getCode() != ErrorCode.ERR_SUCCESS.getCode()) {
                    log.info("licenseValidation code is not ok");
                    return new DataProcess<>(licenseValidation.getCode(), licenseValidation.getMessage());
                }

                LicenseInfoResponse currentLicense = null;
                for (LicenseInfoResponse licenseInfoResponse: licenseValidation.getData().getLicenseInfoList()) {
                    if (licenseInfoResponse.getProductName().contains(LicenseConstants.PRODUCT_NAME)) {
                        currentLicense = licenseInfoResponse;
                    }
                }
                return new DataProcess<>(currentLicense);
            }

            @Override
            DataProcess<LicenseItem> invokeDataProcess() {
                LicenseItem licenseItem = LicenseItem.ITEM_PRODUCT;
                LicenseInfoResponse currentLicense = loadFromDatabase().getData();
                if(currentLicense == null) {
                    return new DataProcess<>(ErrorCode.ERR_LICENSE_PRODUCT_VALIDITY);
                } else {
                    if (!currentLicense.getStatus().equals(LicenseConstants.STATUS_USE)) {
                        return new DataProcess<>(ErrorCode.ERR_LICENSE_NOT_VALIDITY);
                    }
                    return new DataProcess<>(licenseItem);
                }
            }
        }.asResponseData();
    }

    public DataProcess<Boolean> generateRoomSession(String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                log.info("GENERATE ROOM SESSION RETRIEVE BY SESSION ID => [{}]", sessionId);
                sessionService.createSession(sessionId);
                return new DataProcess<>(true);
            }
        }.asResponseData();
    }

    /*public ApiResponse<RoomProfileUpdateResponse> updateRoom(
            String workspaceId,
            String sessionId,
            RoomProfileUpdateRequest roomProfileUpdateRequest) {
        return new RepoDecoder<Room, RoomProfileUpdateResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomProfileUpdateResponse> invokeDataProcess() {
                log.info("ROOM INFO UPDATE PROFILE BY SESSION ID => [{}, {}]", workspaceId, sessionId);
                RoomProfileUpdateResponse profileUpdateResponse = new RoomProfileUpdateResponse();
                String profile = Default.ROOM_PROFILE.getValue();

                Room room = loadFromDatabase();
                if(room != null) {
                    if(room.getLeaderId().equals(roomProfileUpdateRequest.getUuid())) {
                        if (roomProfileUpdateRequest.getProfile() != null) {
                            try {
                                profile = localFileManagementService.upload(roomProfileUpdateRequest.getProfile());
                            } catch (IOException e) {
                                log.error(e.getMessage());
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }
                        }
                        profileUpdateResponse.setSessionId(sessionId);
                        profileUpdateResponse.setProfile(profile);
                        sessionService.updateRoom(room, profile);
                        return new DataProcess<>(profileUpdateResponse);
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                }
            }
        }.asApiResponse();
    }*/

    public DataProcess<ErrorCode> joinSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, ErrorCode>(RepoDecoderType.UPDATE) {
            ClientMetaData clientMetaData = null;
            Room room;
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(sessionId);
            }

            @Override
            DataProcess<ErrorCode> invokeDataProcess() {
                room = loadFromDatabase();
                if(room == null) {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                }

                JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                clientMetaData = null;
                try {
                    clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                //assert clientMetaData != null;

                log.info("session join and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session join and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session join and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

                Member member = sessionService.getMember(room.getWorkspaceId(), sessionId, clientMetaData.getClientData());
                try {
                    if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID); //Code.EXISTING_USER_IN_ROOM_ERROR_CODE
                    } else {
                        member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                        member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                        member.setConnectionId(participant.getParticipantPublicId());
                        member.setMemberStatus(MemberStatus.LOAD);
                    }
                    sessionService.setMember(member);
                    /*if (member == null) {
                        member = setData();
                    } else {
                        if (member.getMemberStatus().equals(MemberStatus.LOAD)) {
                            return new DataProcess<>(ErrorCode.ERR_ROOM_MEMBER_STATUS_INVALID); //Code.EXISTING_USER_IN_ROOM_ERROR_CODE
                        } else {
                            member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                            member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                            member.setConnectionId(participant.getParticipantPublicId());
                            member.setMemberStatus(MemberStatus.LOAD);
                        }
                    }
                    sessionService.setMember(member);*/
                    //sessionService.joinSession(sessionId, participant.getParticipantPublicId(), clientMetaData);
                } catch (NullPointerException exception) {
                    LogMessage.formedError(
                            TAG,
                            "JOIN SESSION EVENT",
                            "joinSession",
                            exception.getClass().getName(),
                            exception.getMessage());
                    throw new RemoteServiceException(Code.USER_METADATA_FORMAT_INVALID_ERROR_CODE, "Invalid metadata lacking parameter");
                }
                return new DataProcess<>(ErrorCode.ERR_SUCCESS);

                /*if(!Objects.equals(clientMetaData.getRoleType(), MemberType.LEADER.name())
                || !Objects.equals(clientMetaData.getRoleType(), MemberType.EXPERT.name())
                || !Objects.equals(clientMetaData.getRoleType(), MemberType.WORKER.name())
                ) {
                    return new DataProcess<>(false);
                } else {

                }*/
            }

            private Member setData() {
                Member member = Member.builder()
                        .room(room)
                        .memberType(MemberType.valueOf(clientMetaData.getRoleType()))
                        .uuid(clientMetaData.getClientData())
                        .workspaceId(room.getWorkspaceId())
                        .sessionId(room.getSessionId())
                        .build();

                member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                member.setConnectionId(participant.getParticipantPublicId());
                member.setMemberStatus(MemberStatus.LOAD);

                return member;
            }
        }.asResponseData();
    }

    public DataProcess<Boolean> leaveSession(Participant participant, String sessionId, EndReason reason) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            ClientMetaData clientMetaData = null;
            Room room = null;

            private void preDataProcess() {
                JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                assert clientMetaData != null;

                log.info("session leave and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session leave and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session leave and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());
            }

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(sessionId);
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                preDataProcess();

                room = loadFromDatabase();
                if(room == null) {
                    LogMessage.formedError(
                            TAG,
                            "LEAVE SESSION EVENT",
                            "leaveSession",
                            reason.toString(),
                            ErrorCode.ERR_ROOM_NOT_FOUND.getMessage());
                } else {
                    for (Member member : room.getMembers()) {
                        if (member.getUuid().equals(clientMetaData.getClientData())) {
                            //set status unload
                            member.setMemberStatus(MemberStatus.UNLOAD);
                            //set connection id to empty
                            member.setConnectionId("");
                            //set end time
                            LocalDateTime endTime = LocalDateTime.now();
                            member.setEndDate(endTime);

                            //time diff seconds
                            Long totalDuration = member.getDurationSec();
                            Duration duration = Duration.between(member.getStartDate(), endTime);
                            member.setDurationSec(totalDuration + duration.getSeconds());

                            //save member
                            sessionService.setMember(member);
                        }
                    }
                }

                /*SessionType sessionType = room.getSessionProperty().getSessionType();
                if(sessionType.equals(SessionType.OPEN)) {
                    //room.getMembers().removeIf(member -> member.getUuid().equals(clientMetaData.getClientData()));
                    sessionService.setRoom(room);
                } else {
                    for (Member member : room.getMembers()) {
                        if (member.getUuid().equals(clientMetaData.getClientData())) {
                            //set status unload
                            member.setMemberStatus(MemberStatus.UNLOAD);
                            //set connection id to empty
                            member.setConnectionId("");
                            //set end time
                            LocalDateTime endTime = LocalDateTime.now();
                            member.setEndDate(endTime);

                            //time diff seconds
                            Long totalDuration = member.getDurationSec();
                            Duration duration = Duration.between(member.getStartDate(), endTime);
                            member.setDurationSec(totalDuration + duration.getSeconds());

                            //save member
                            sessionService.setMember(member);
                        }
                    }
                }*/
                return new DataProcess<>(true);
            }
        }.asResponseData();
    }

    public DataProcess<Boolean> closeSession(Participant participant, String sessionId, EndReason reason) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            ClientMetaData clientMetaData = null;
            Room room = null;

            private void preDataProcess() {
                JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                try {
                    clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                assert clientMetaData != null;

                log.info("session leave and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session leave and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session leave and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());
            }

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(sessionId);
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                preDataProcess();

                room = loadFromDatabase();
                if(room == null) {
                    LogMessage.formedInfo(
                            TAG,
                            "LEAVE SESSION EVENT",
                            "leaveSession",
                            reason.toString());
                    return new DataProcess<>(true);
                } else {
                    LogMessage.formedError(
                            TAG,
                            "LEAVE SESSION EVENT",
                            "leaveSession",
                            reason.toString(),
                            ErrorCode.ERR_ROOM_CLOSE_FAIL.getMessage());
                    //do update room data
                    setLogging();
                    sessionService.deleteRoom(room);
                    return new DataProcess<>(false);
                }
            }
            private void setLogging() {
                setHistory();
            }

            private void setHistory() {
                // Remote Room History Entity Create
                RoomHistory roomHistory = RoomHistory.builder()
                        .sessionId(room.getSessionId())
                        .title(room.getTitle())
                        .description(room.getDescription())
                        .profile(room.getProfile())
                        .leaderId(room.getLeaderId())
                        .workspaceId(room.getWorkspaceId())
                        .maxUserCount(room.getMaxUserCount())
                        .licenseName(room.getLicenseName())
                        .build();

                // Remote Session Property Entity Create
                SessionProperty sessionProperty = room.getSessionProperty();
                SessionPropertyHistory sessionPropertyHistory = SessionPropertyHistory.builder()
                        .mediaMode(sessionProperty.getMediaMode())
                        .recordingMode(sessionProperty.getRecordingMode())
                        .defaultOutputMode(sessionProperty.getDefaultOutputMode())
                        .defaultRecordingLayout(sessionProperty.getDefaultRecordingLayout())
                        .recording(sessionProperty.isRecording())
                        .keepalive(sessionProperty.isKeepalive())
                        .sessionType(sessionProperty.getSessionType())
                        .roomHistory(roomHistory)
                        .build();

                roomHistory.setSessionPropertyHistory(sessionPropertyHistory);

                // Set room member history
                // Mapping Member List Data to Member History List
                for (Member roomMember : room.getMembers()) {
                    MemberHistory memberHistory = MemberHistory.builder()
                            .roomHistory(roomHistory)
                            .workspaceId(roomMember.getWorkspaceId())
                            .uuid(roomMember.getUuid())
                            .memberType(roomMember.getMemberType())
                            .deviceType(roomMember.getDeviceType())
                            .sessionId(roomMember.getSessionId())
                            .startDate(roomMember.getStartDate())
                            .endDate(roomMember.getEndDate())
                            .durationSec(roomMember.getDurationSec())
                            .build();

                    //sessionService.setMemberHistory(memberHistory);
                    roomHistory.getMemberHistories().add(memberHistory);

                    //delete member
                    sessionService.deleteMember(roomMember);
                }

                //set active time
                roomHistory.setActiveDate(room.getActiveDate());

                //set un active  time
                LocalDateTime endTime = LocalDateTime.now();
                roomHistory.setUnactiveDate(endTime);

                //time diff seconds
                Duration duration = Duration.between(room.getActiveDate(), endTime);
                roomHistory.setDurationSec(duration.getSeconds());

                //save room history
                sessionService.setRoomHistory(roomHistory);
            }
        }.asResponseData();
    }

    public DataProcess<Boolean> disconnectSession(Participant participant, String sessionId, EndReason reason) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.DELETE) {
            Room room = null;
            ClientMetaData clientMetaData = null;

            private void preDataProcess() {
                JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                try {
                    clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                assert clientMetaData != null;

                log.info("session disconnect and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session disconnect and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session disconnect and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());
            }

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(sessionId);
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                preDataProcess();

                room = loadFromDatabase();
                if(room == null) {
                    throw new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND);
                }

                for (Member member : room.getMembers()) {
                    if (member.getUuid().equals(clientMetaData.getClientData())) {
                        log.info("session disconnect and sessionEventHandler evict user id::{}", member.getUuid());
                        //set status evicted
                        member.setMemberStatus(MemberStatus.EVICTED);
                        //set connection id to empty
                        member.setConnectionId("");
                        //set end time
                        LocalDateTime endTime = LocalDateTime.now();
                        member.setEndDate(endTime);

                        //time diff seconds
                        Long totalDuration = member.getDurationSec();
                        Duration duration = Duration.between(member.getStartDate(), endTime);
                        member.setDurationSec(totalDuration + duration.getSeconds());

                        //set room null
                        //member.setRoom(null);

                        //save member
                        sessionService.setMember(member);
                    }
                }
                return new DataProcess<>(true);
            }
        }.asResponseData();

    }

    public DataProcess<Boolean> destroySession(String sessionId, EndReason reason) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.DELETE) {
            Room room = null;

            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(sessionId);
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                room = loadFromDatabase();
                if(room == null) {
                    LogMessage.formedError(
                            TAG,
                            "DESTROY SESSION EVENT",
                            "destroySession",
                            reason.toString(),
                            ErrorCode.ERR_ROOM_NOT_FOUND.getMessage());
                    return new DataProcess<>(false);
                } else {
                    LogMessage.formedInfo(
                            TAG,
                            "DESTROY SESSION EVENT",
                            "destroySession",
                            reason.toString(),
                            sessionId);
                    setLogging();
                    sessionService.deleteRoom(room);
                    //sessionService.destroySession(sessionId);
                    return new DataProcess<>(true);
                }

            }

            private void setLogging() {
                setHistory();
                // check the same session id history room is already exist
                /*RoomHistory roomHistory = sessionService.getRoomHistory(room.getSessionId());
                if(roomHistory != null) {
                    log.info("FOUND THE SAME SESSION ID => [{}]", roomHistory.getSessionId());
                    derivedHistory(roomHistory);
                } else {
                    setHistory();
                }*/
            }

            private void derivedHistory(RoomHistory roomHistory) {
                roomHistory.setTitle(room.getTitle());
                roomHistory.setDescription(room.getDescription());
                roomHistory.setProfile(room.getProfile());
                roomHistory.setMaxUserCount(room.getMaxUserCount());
                roomHistory.setLicenseName(room.getLicenseName());

                // Remote Session Property Entity Create
                SessionProperty sessionProperty = room.getSessionProperty();
                SessionPropertyHistory sessionPropertyHistory = roomHistory.getSessionPropertyHistory();
                sessionPropertyHistory.setMediaMode(sessionProperty.getMediaMode());
                sessionPropertyHistory.setRecordingMode(sessionProperty.getRecordingMode());
                sessionPropertyHistory.setDefaultOutputMode(sessionProperty.getDefaultOutputMode());
                sessionPropertyHistory.setDefaultRecordingLayout(sessionProperty.getDefaultRecordingLayout());
                sessionPropertyHistory.setRecording(sessionProperty.isRecording());
                sessionPropertyHistory.setKeepalive(sessionProperty.isKeepalive());
                sessionPropertyHistory.setSessionType(sessionProperty.getSessionType());
                sessionPropertyHistory.setRoomHistory(roomHistory);

                roomHistory.setSessionPropertyHistory(sessionPropertyHistory);

                // Set room member history
                // Get Member history list and set room null
                List<MemberHistory> memberHistoryList = roomHistory.getMemberHistories();
                for (MemberHistory memberHistory: memberHistoryList) {
                    memberHistory.setHistoryDeleted(true);
                    sessionService.setMemberHistory(memberHistory);
                }

                // Get Member List by Room Session Ids
                // Mapping Member List Data to Member History List
                for (Member member : room.getMembers()) {
                    MemberHistory memberHistory = MemberHistory.builder()
                            .roomHistory(roomHistory)
                            .workspaceId(member.getWorkspaceId())
                            .uuid(member.getUuid())
                            .memberType(member.getMemberType())
                            .deviceType(member.getDeviceType())
                            .sessionId(member.getSessionId())
                            .startDate(member.getStartDate())
                            .endDate(member.getEndDate())
                            .durationSec(member.getDurationSec())
                            .build();

                    sessionService.setMemberHistory(memberHistory);
                    roomHistory.getMemberHistories().add(memberHistory);

                    //delete member
                    sessionService.deleteMember(member);
                }

                //set active time do not update active date
                //oldRoomHistory.setActiveDate(room.getActiveDate());
                //set un active  time
                LocalDateTime endTime = LocalDateTime.now();
                roomHistory.setUnactiveDate(endTime);

                //time diff seconds
                Duration duration = Duration.between(room.getActiveDate(), endTime);
                Long totalDuration = duration.getSeconds() + roomHistory.getDurationSec();
                roomHistory.setDurationSec(totalDuration);

                //save room history
                sessionService.setRoomHistory(roomHistory);
            }

            private void setHistory() {
                // Remote Room History Entity Create
                RoomHistory roomHistory = RoomHistory.builder()
                        .sessionId(room.getSessionId())
                        .title(room.getTitle())
                        .description(room.getDescription())
                        .profile(room.getProfile())
                        .leaderId(room.getLeaderId())
                        .workspaceId(room.getWorkspaceId())
                        .maxUserCount(room.getMaxUserCount())
                        .licenseName(room.getLicenseName())
                        .build();

                // Remote Session Property Entity Create
                SessionProperty sessionProperty = room.getSessionProperty();
                SessionPropertyHistory sessionPropertyHistory = SessionPropertyHistory.builder()
                        .mediaMode(sessionProperty.getMediaMode())
                        .recordingMode(sessionProperty.getRecordingMode())
                        .defaultOutputMode(sessionProperty.getDefaultOutputMode())
                        .defaultRecordingLayout(sessionProperty.getDefaultRecordingLayout())
                        .recording(sessionProperty.isRecording())
                        .keepalive(sessionProperty.isKeepalive())
                        .sessionType(sessionProperty.getSessionType())
                        .roomHistory(roomHistory)
                        .build();

                roomHistory.setSessionPropertyHistory(sessionPropertyHistory);

                // Set room member history
                // Mapping Member List Data to Member History List
                for (Member roomMember : room.getMembers()) {
                    MemberHistory memberHistory = MemberHistory.builder()
                            .roomHistory(roomHistory)
                            .workspaceId(roomMember.getWorkspaceId())
                            .uuid(roomMember.getUuid())
                            .memberType(roomMember.getMemberType())
                            .deviceType(roomMember.getDeviceType())
                            .sessionId(roomMember.getSessionId())
                            .startDate(roomMember.getStartDate())
                            .endDate(roomMember.getEndDate())
                            .durationSec(roomMember.getDurationSec())
                            .build();

                    //sessionService.setMemberHistory(memberHistory);
                    roomHistory.getMemberHistories().add(memberHistory);

                    //delete member
                    sessionService.deleteMember(roomMember);
                }

                //set active time
                roomHistory.setActiveDate(room.getActiveDate());

                //set un active  time
                LocalDateTime endTime = LocalDateTime.now();
                roomHistory.setUnactiveDate(endTime);

                //time diff seconds
                Duration duration = Duration.between(room.getActiveDate(), endTime);
                roomHistory.setDurationSec(duration.getSeconds());

                //save room history
                sessionService.setRoomHistory(roomHistory);
            }
        }.asResponseData();
    }

    public DataProcess<Boolean> stopRecordSession(String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.FETCH) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(sessionId);
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                Room room = loadFromDatabase();
                LogMessage.formedInfo(
                        TAG,
                        "stopRecordSession",
                        "invokeDataProcess",
                        "destroy session",
                        sessionId);

                if(room != null) {
                    if(config.remoteServiceProperties.mediaServerProperties.recordingProperty.isRecording()) {
                        ApiResponse<StopRecordingResponse> apiResponse = recordRestService.stopRecordingBySessionId(room.getWorkspaceId(), room.getLeaderId(), room.getSessionId());
                        if (apiResponse.getCode() == 200) {
                            if (apiResponse.getData() != null) {
                                for (String recordingId : apiResponse.getData().getRecordingIds()) {
                                    LogMessage.formedInfo(
                                            TAG,
                                            "stopRecordSession",
                                            "invokeDataProcess",
                                            "recording id response",
                                            recordingId);
                                }
                            }
                        } else {
                            LogMessage.formedInfo(
                                    TAG,
                                    "stopRecordSession",
                                    "invokeDataProcess",
                                    "recording api response error code",
                                    String.valueOf(apiResponse.getCode()));
                        }
                    }
                    return new DataProcess<>(true);
                } else {
                    return new DataProcess<>(false);
                }

            }
        }.asResponseData();
    }

    @Deprecated
    public DataProcess<String> evictParticipant(String workspaceId, String sessionId, String userId) {
        return new RepoDecoder<Member, String>(RepoDecoderType.READ) {
            @Override
            Member loadFromDatabase() {
                return sessionService.getMember(workspaceId, sessionId, userId);
            }

            @Override
            DataProcess<String> invokeDataProcess() {
                Member member = loadFromDatabase();
                if(member != null) {
                    return new DataProcess<>(member.getConnectionId());
                }
                return new DataProcess<>("");
            }
        }.asResponseData();
    }

    public DataProcess<List<String>> getConnectionIds(String workspaceId, String sessionId) {
        return new RepoDecoder<Room, List<String>>(RepoDecoderType.READ) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<List<String>> invokeDataProcess() {
                Room room = loadFromDatabase();
                List<Member> memberList = room.getMembers();

                //Do not use collection method removeIf with Data Access Object
                //memberList.removeIf(member -> member.getUuid().equals(room.getLeaderId()));

                List<String> connectionIds = new ArrayList<>();
                for (Member member: memberList) {
                    if(member.getConnectionId() != null &&
                            !member.getUuid().equals(room.getLeaderId())) {
                        connectionIds.add(member.getConnectionId());
                    }
                }
                return new DataProcess<>(connectionIds);
            }
        }.asResponseData();

    }





}

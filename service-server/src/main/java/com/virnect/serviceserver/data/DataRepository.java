package com.virnect.serviceserver.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.virnect.data.dao.*;
import com.virnect.data.service.HistoryService;
import com.virnect.mediaserver.core.Participant;
import com.virnect.service.ApiResponse;
import com.virnect.service.FileService;
import com.virnect.service.SessionService;
import com.virnect.service.constraint.LicenseConstants;
import com.virnect.service.constraint.LicenseItem;
import com.virnect.service.dto.feign.LicenseInfoListResponse;
import com.virnect.service.dto.feign.LicenseInfoResponse;
import com.virnect.service.dto.feign.StopRecordingResponse;
import com.virnect.service.dto.feign.UserInfoResponse;
import com.virnect.service.dto.rpc.ClientMetaData;
import com.virnect.service.error.ErrorCode;
import com.virnect.service.error.exception.RestServiceException;
import com.virnect.serviceserver.config.RemoteServiceConfig;
import com.virnect.serviceserver.feign.service.LicenseRestService;
import com.virnect.serviceserver.feign.service.RecordRestService;
import com.virnect.serviceserver.feign.service.UserRestService;
import com.virnect.serviceserver.feign.service.WorkspaceRestService;
import com.virnect.serviceserver.utils.PushMessageClient;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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

    public DataProcess<Boolean> joinSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            ClientMetaData clientMetaData = null;
            Room room;
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(sessionId);
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                room = loadFromDatabase();
                if(room == null) {
                    throw new RestServiceException(ErrorCode.ERR_ROOM_NOT_FOUND);
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
                assert clientMetaData != null;

                log.info("session join and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session join and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session join and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

                Member member = setData();
                sessionService.setMember(member);
                //sessionService.joinSession(sessionId, participant.getParticipantPublicId(), clientMetaData);
                return new DataProcess<>(true);

                /*if(!Objects.equals(clientMetaData.getRoleType(), MemberType.LEADER.name())
                || !Objects.equals(clientMetaData.getRoleType(), MemberType.EXPERT.name())
                || !Objects.equals(clientMetaData.getRoleType(), MemberType.WORKER.name())
                ) {
                    return new DataProcess<>(false);
                } else {

                }*/
            }

            private Member setData() {
                Member member = sessionService.getMember(room.getWorkspaceId(), sessionId, clientMetaData.getClientData());
                if(member == null) {
                    member = Member.builder()
                            .room(room)
                            .memberType(MemberType.valueOf(clientMetaData.getRoleType()))
                            .uuid(clientMetaData.getClientData())
                            .workspaceId(room.getWorkspaceId())
                            .sessionId(room.getSessionId())
                            .build();
                } else {
                    member.setMemberType(MemberType.valueOf(clientMetaData.getRoleType()));
                }
                member.setDeviceType(DeviceType.valueOf(clientMetaData.getDeviceType()));
                member.setConnectionId(participant.getParticipantPublicId());
                member.setMemberStatus(MemberStatus.LOAD);

                return member;
            }
        }.asResponseData();
    }

    public DataProcess<Boolean> leaveSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ClientMetaData clientMetaData = null;
                try {
                    clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                assert clientMetaData != null;

                log.info("session leave and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session leave and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session leave and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

                sessionService.leaveSession(sessionId, clientMetaData);
                return new DataProcess<>(true);
            }
        }.asResponseData();
    }

    public DataProcess<Boolean> disconnectSession(Participant participant, String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                JsonObject jsonObject = JsonParser.parseString(participant.getClientMetadata()).getAsJsonObject();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ClientMetaData clientMetaData = null;
                try {
                    clientMetaData = objectMapper.readValue(jsonObject.toString(), ClientMetaData.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                assert clientMetaData != null;

                log.info("session disconnect and clientMetaData is :[ClientData] {}", clientMetaData.getClientData());
                log.info("session disconnect and clientMetaData is :[RoleType] {}", clientMetaData.getRoleType());
                log.info("session disconnect and clientMetaData is :[DeviceType] {}", clientMetaData.getDeviceType());

                sessionService.disconnectSession(sessionId, clientMetaData);
                return new DataProcess<>(true);
            }
        }.asResponseData();

    }

    public DataProcess<Boolean> destroySession(String sessionId) {
        return new RepoDecoder<Room, Boolean>(RepoDecoderType.DELETE) {
            @Override
            Room loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<Boolean> invokeDataProcess() {
                sessionService.destroySession(sessionId);
                return new DataProcess<>(true);
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
                log.info("STOP RECORD::#stopRecordSession::destroy session => [{}]",sessionId);
                if(room != null) {
                    ApiResponse<StopRecordingResponse> apiResponse = recordRestService.stopRecordingBySessionId(room.getWorkspaceId(), room.getLeaderId(), room.getSessionId());
                    if(apiResponse.getCode() == 200) {
                        if(apiResponse.getData() != null) {
                            for (String recordingId : apiResponse.getData().getRecordingIds()) {
                                log.info("STOP RECORD::#stopRecordSession::response => [{}]", recordingId);
                            }
                        }
                    } else {
                        log.info("STOP RECORD::#stopRecordSession::err response => [{}]", apiResponse.getCode());
                    }

                    return new DataProcess<>(true);
                } else {
                    return new DataProcess<>(false);
                }

            }
        }.asResponseData();
    }

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
